{ pkgs, config, lib, ... }:
let
  cfg = config.services.prost;
  inherit (pkgs) dockerTools buildEnv stdenv;
  inherit (stdenv) mkDerivation;
  vite_base_path = "/prost";
  frontend_folder = mkDerivation {
    name = "frontend_folder";
    src = ../frontend;
    phases = [ "installPhase" ];
    installPhase = ''
      mkdir -p $out
      cp -r $src/* $out
    '';
  };
  frontend_build_base = dockerTools.pullImage {
    imageName = "docker.io/library/node";
    imageDigest =
      "sha256:f77a1aef2da8d83e45ec990f45df50f1a286c5fe8bbfb8c6e4246c6389705c0b";
    sha256 = "1vjpgzn9ccqssrxqplkig8c578gv7m8596sq41dlar6xggm68ynr";
    finalImageName = "docker.io/library/node";
    finalImageTag = "16";
  };
  frontend_build = let WorkingDir = "/app/frontend";
  in dockerTools.buildImage {
    name = "frontend_build";
    tag = "latest";
    fromImage = frontend_build_base;
    diskSize = 8192;
    buildVMMemorySize = 2048;

    copyToRoot = [ pkgs.nodejs_23 frontend_folder ];
    #buildEnv {
    #  name = "frontend_build-root";
    #  paths = [ frontend_folder ];
    #  extraPrefix = WorkingDir;
    #};

    runAsRoot = ''
      echo "a"
      ${pkgs.nodejs_23}/bin/npm config set loglevel verbose
      ${pkgs.nodejs_23}/bin/npm install
      echo "b"
      ${pkgs.nodejs_23}/bin/npm run build
      echo "c"
    '';

    config = {
      #TODO make this configurable
      Env = [
        "VITE_API_URL=http://localhost:8081"
        "VITE_BASE_PATH=${vite_base_path}"
      ];
      inherit WorkingDir;
    };
  };
  frontend_build_exported = dockerTools.exportImage {
    fromImage = frontend_build_base;
    diskSize = 16000;
  };
  frontend_run_base = dockerTools.pullImage {
    imageName = "docker.io/library/nginx";
    imageDigest =
      "sha256:5acf10cd305853dc2271e3c818d342f3aeb3688b1256ab8f035fda04b91ed303";
    sha256 = "07w24d12079scams3dv3j8ybg8riw771i9yg2ql4ys81pvpbj1mr";
    finalImageName = "docker.io/library/nginx";
    finalImageTag = "alpine";
  };

in {
  #services.prost.frontend.package = frontend_build_base;
  #services.prost.frontend.package = frontend_build;
  services.prost.frontend.package = frontend_build_exported;
  #services.prost.frontend.package = frontend_folder;
  #services.prost.frontend.package = lib.mkDefault (dockerTools.buildImage {
  #  name = "frontend";
  #  tag = "latest";

  #  fromImage = frontend_run_base;

  #  copyToRoot = let
  #    folder = stdenv.mkDerivation {
  #      name = "folder";
  #      src = frontend_build;
  #      phases = [ "installPhase" ];
  #      installPhase = ''
  #        mkdir -p $out
  #        cp -r $src $out
  #      '';
  #    };
  #  in [ folder ];
  #  #buildEnv {
  #  #  name = "frontend_build-root";
  #  #  paths = [ frontend_build ];
  #  #  #pathsToLink = [ "/bin" ];
  #  #};

  #  runAsRoot = ''
  #    echo ${frontend_build}
  #    tar xvf ${frontend_build}
  #    cp  ./nginx.conf /etc/nginx/conf.d/default.conf
  #    cp ${frontend_build}/app/frontend/dist/ /usr/share/nginx/html${vite_base_path}
  #  '';
  #});
}
