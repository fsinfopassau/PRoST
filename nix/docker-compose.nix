# Auto-generated using compose2nix v0.2.3.
self:
{ pkgs, lib, config, ... }:
let cfg = config.services.prost;
in {
  config = {
    # Runtime
    virtualisation.docker = {
      enable = true;
      autoPrune.enable = true;
    };
    virtualisation.oci-containers.backend = "docker";

    # Containers
    virtualisation.oci-containers.containers."PRoST-Backend" = {
      image = "prost-backend:latest";
      imageFile = pkgs.dockerTools.buildImage {
        name = "prost-backend";
        tag = "latest";
        fromImage = pkgs.dockerTools.pullImage {
          imageName = "openjdk";
          imageDigest =
            "sha256:aaa3b3cb27e3e520b8f116863d0580c438ed55ecfa0bc126b41f68c3f62f9774";
          sha256 = "0qn0ikh9zkkz5pzi133wvdpf6zx815935wy3ymawp1mfvskv0xz1";
          finalImageName = "openjdk";
          finalImageTag = "17-slim";
        };
        config = {
          WorkingDir = "/app";
          Cmd = [ "${self.packages.x86_64-linux.backend}/bin/prost_backend" ];
          Env = [ "DATA_LOCATION=/data" ];
          ExposedPorts = { "8081" = { }; };
        };
      };
      environment = { "LDAP_URI" = cfg.ldapUri; };
      volumes = [ "${cfg.backend.volumePath}:/data:rw" ];
      ports = [ "8081:8081/tcp" ];
      dependsOn = [ "PRoST-LDAP" "mariadb" ];
      log-driver = "journald";
      extraOptions = [ "--network-alias=backend" "--network=prost_prost" ];
    };
    systemd.services."docker-PRoST-Backend" = {
      serviceConfig = { Restart = lib.mkOverride 500 "no"; };
      after = [ "docker-network-prost_prost.service" ];
      requires = [ "docker-network-prost_prost.service" ];
      partOf = [ "docker-compose-prost-root.target" ];
      wantedBy = [ "docker-compose-prost-root.target" ];
      serviceConfig.ExecStartPre = let
        script = pkgs.writeShellScriptBin "pre-start" ''
          #!${pkgs.bash}/bin/bash


          docker load -i ${
            config.virtualisation.oci-containers.containers."PRoST-Backend".imageFile
          }
        '';
      in lib.mkForce [ "${script}/bin/pre-start" ];
    };
    virtualisation.oci-containers.containers."PRoST-LDAP" = with cfg.ldap; {
      image = "bitnami/openldap:latest";
      environment = {
        "LDAP_ADMIN_DN" = admin.dn;
        "LDAP_ADMIN_PASSWORD" = admin.password;
        "LDAP_ADMIN_USERNAME" = admin.username;
        "LDAP_CUSTOM_LDIF_DIR" = "/ldif";
        "LDAP_ROOT" = root;
      };
      volumes = [ "${dir}:/ldif:rw" "prost_prost-ldap:/bitnami/openldap:rw" ];
      log-driver = "journald";
      extraOptions = [ "--network-alias=ldap" "--network=prost_prost" ];
    };
    systemd.services."docker-PRoST-LDAP" = {
      serviceConfig = { Restart = lib.mkOverride 500 "no"; };
      after = [
        "docker-network-prost_prost.service"
        "docker-volume-prost_prost-ldap.service"
      ];
      requires = [
        "docker-network-prost_prost.service"
        "docker-volume-prost_prost-ldap.service"
      ];
      partOf = [ "docker-compose-prost-root.target" ];
      wantedBy = [ "docker-compose-prost-root.target" ];
    };
    virtualisation.oci-containers.containers."mariadb" = with cfg.mariadb; {
      image = "lscr.io/linuxserver/mariadb:latest";
      environment = {
        "MYSQL_DATABASE" = database;
        "MYSQL_PASSWORD" = password;
        "MYSQL_ROOT_PASSWORD" = root_password;
        "MYSQL_USER" = user;
      };
      volumes = [ "${configDir}:/config:rw" ];
      log-driver = "journald";
      extraOptions = [ "--network-alias=mariadb" "--network=prost_prost" ];
    };
    systemd.services."docker-mariadb" = {
      serviceConfig = { Restart = lib.mkOverride 500 "no"; };
      after = [ "docker-network-prost_prost.service" ];
      requires = [ "docker-network-prost_prost.service" ];
      partOf = [ "docker-compose-prost-root.target" ];
      wantedBy = [ "docker-compose-prost-root.target" ];
    };

    # Networks
    systemd.services."docker-network-prost_default" = {
      path = [ pkgs.docker ];
      serviceConfig = {
        Type = "oneshot";
        RemainAfterExit = true;
        ExecStop = "docker network rm -f prost_default";
      };
      script = ''
        docker network inspect prost_default || docker network create prost_default
      '';
      partOf = [ "docker-compose-prost-root.target" ];
      wantedBy = [ "docker-compose-prost-root.target" ];
    };
    systemd.services."docker-network-prost_prost" = {
      path = [ pkgs.docker ];
      serviceConfig = {
        Type = "oneshot";
        RemainAfterExit = true;
        ExecStop = "docker network rm -f prost_prost";
      };
      script = ''
        docker network inspect prost_prost || docker network create prost_prost
      '';
      partOf = [ "docker-compose-prost-root.target" ];
      wantedBy = [ "docker-compose-prost-root.target" ];
    };

    # Volumes
    systemd.services."docker-volume-prost_prost-ldap" = {
      path = [ pkgs.docker ];
      serviceConfig = {
        Type = "oneshot";
        RemainAfterExit = true;
      };
      script = ''
        docker volume inspect prost_prost-ldap || docker volume create prost_prost-ldap
      '';
      partOf = [ "docker-compose-prost-root.target" ];
      wantedBy = [ "docker-compose-prost-root.target" ];
    };

    # Root service
    # When started, this will automatically create all resources and start
    # the containers. When stopped, this will teardown all resources.
    systemd.targets."docker-compose-prost-root" = {
      unitConfig = { Description = "Root target generated by compose2nix."; };
      wantedBy = [ "multi-user.target" ];
    };
  };
}
