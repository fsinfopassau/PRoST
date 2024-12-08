{
  description = "PRoST";

  inputs = { nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable"; };

  outputs = { self, nixpkgs }:
    let
      lib = nixpkgs.lib;
      pkgs = import nixpkgs { system = "x86_64-linux"; };

    in {

      packages.x86_64-linux = {
        frontend = let
          inherit (pkgs) importNpmLock;
          pname = "frontend"; # <same as package.json name>
          version = "0.1.0";
          buildInputs = with pkgs; [ nodejs_20 nodePackages_latest.pnpm ];
          nativeBuildInputs = buildInputs;
          npmDepsHash =
            #"sha256-IUXm18nr61fYDFYhSTmZI0fFwwKfJGFiqfpljqM+/Ao="; # <prefetch-npm-deps package-lock.json>
            "sha256-rtnM+9sJ3EEzoTY7MAZ2+Rcndj+H7h7n64g0nOt7Lzo=";
        in pkgs.buildNpmPackage {
          inherit pname version buildInputs npmDepsHash nativeBuildInputs;
          src = ./frontend;
          npmDeps = importNpmLock { npmRoot = ./frontend; };

          VITE_API_URL = "http://localhost:8081";
          VITE_BASE_PATH = "/prost";

          npmConfigHook = importNpmLock.npmConfigHook;
          installPhase = ''
            mkdir -p $out/prost
            cp -r ./dist/* $out/prost
          '';
        };
        backend = pkgs.maven.buildMavenPackage rec {
          pname = "prost_backend";
          version = "1.2.1";

          src = ./backend;
          mvnHash = "sha256-l90tZ7Gih2FEz2fTlZJ4LSPHbL0r9mr25Ww901urF48=";

          nativeBuildInputs = [ pkgs.makeWrapper ];

          installPhase = ''
            mkdir -p $out/bin $out/share/${pname}
            echo $src
            echo $out
            install -Dm644 target/*.jar $out/share/${pname}

            makeWrapper ${pkgs.jdk17}/bin/java $out/bin/${pname} \
              --add-flags "-jar $out/share/${pname}/PRoST-1.0.0.jar"
          '';

          #meta = with lib; {
          #  description =
          #    "Simple command line wrapper around JD Core Java Decompiler project";
          #  homepage = "https://github.com/intoolswetrust/jd-cli";
          #  license = licenses.gpl3Plus;
          #  maintainers = with maintainers; [ majiir ];
          #};
        };
      };
      #packages.x86_64-linux.prost = import ./nix/package.nix {
      #  inherit pkgs;
      #  inherit (pkgs) stdenv;
      #};

      nixosModules = rec {
        default = prost;
        prost = (import ./nix self);
      };
    };
}
