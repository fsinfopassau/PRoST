{
  description = "NextJS Template";

  inputs = {
    nixpkgs.url = "nixpkgs";
    systems.url = "github:nix-systems/x86_64-linux";
    flake-utils = {
      url = "github:numtide/flake-utils";
      inputs.systems.follows = "systems";
    };
  };

  outputs = { nixpkgs, flake-utils, ... }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
        inherit (pkgs) importNpmLock;
        pname = "frontend"; # <same as package.json name>
        version = "0.1.0";
        buildInputs = with pkgs; [ nodejs_20 nodePackages_latest.pnpm ];
        nativeBuildInputs = buildInputs;
        npmDepsHash =
          #"sha256-IUXm18nr61fYDFYhSTmZI0fFwwKfJGFiqfpljqM+/Ao="; # <prefetch-npm-deps package-lock.json>
          "sha256-rtnM+9sJ3EEzoTY7MAZ2+Rcndj+H7h7n64g0nOt7Lzo=";
      in {
        devShells.default = pkgs.mkShell {
          inherit buildInputs;
          shellHook = ''
            #!/usr/bin/env bash
          '';
        };
        packages.default = pkgs.buildNpmPackage {
          inherit pname version buildInputs npmDepsHash nativeBuildInputs;
          src = ./.;
          npmDeps = importNpmLock { npmRoot = ./.; };

          VITE_API_URL = "http://localhost:8081";
          VITE_BASE_PATH = "/prost";

          npmConfigHook = importNpmLock.npmConfigHook;
          installPhase = ''
            mkdir -p $out/
            cp -r ./dist/* $out/
          '';
        };
      });
}
