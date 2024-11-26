{ pkgs, stdenv, ... }:
stdenv.mkDerivation rec {
  name = "PRoST";
  src = ../.;
  buildInputs = with pkgs; [ docker-compose ];
  buildPhase = ''
    cp -R . $out
    pushd $out
    ${pkgs.docker}/bin/docker compose build
    popd
  '';
  #${pkgs.docker}/bin/docker compose build $out/docker-compose.yml
  installPhase = ''
    mkdir -p $out/bin
    echo "${pkgs.docker}/bin/docker compose up" > $out/bin/${name}
  '';
}
