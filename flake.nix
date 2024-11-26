{
  description = "PRoST";

  inputs = { nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable"; };

  outputs = { self, nixpkgs }:
    let
      lib = nixpkgs.lib;
      pkgs = import nixpkgs { system = "x86_64-linux"; };
    in {

      packages.x86_64-linux.default = self.packages.x86_64-linux.prost;
      packages.x86_64-linux.prost = import ./nix/package.nix {
        inherit pkgs;
        inherit (pkgs) stdenv;
      };

    };
}
