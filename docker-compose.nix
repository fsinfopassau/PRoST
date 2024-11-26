# Auto-generated using compose2nix v0.2.3.
{ pkgs, lib, ... }:

{
  # Runtime
  virtualisation.podman = {
    enable = true;
    autoPrune.enable = true;
    dockerCompat = true;
    defaultNetwork.settings = {
      # Required for container networking to be able to use names.
      dns_enabled = true;
    };
  };
  virtualisation.oci-containers.backend = "podman";

  # Containers
  virtualisation.oci-containers.containers."PRoST-Backend" = {
    image = "";
    environment = {
      "LDAP_URI" = "ldap://ldap:1389/dc=fsinfo,dc=fim,dc=uni-passau,dc=de";
    };
    volumes = [
      "/home/hannses/programming/PRoST/.data:/data:rw"
    ];
    ports = [
      "8081:8081/tcp"
    ];
    dependsOn = [
      "PRoST-LDAP"
      "mariadb"
    ];
    log-driver = "journald";
    extraOptions = [
      "--network-alias=backend"
      "--network=prost_prost"
    ];
  };
  systemd.services."podman-PRoST-Backend" = {
    serviceConfig = {
      Restart = lib.mkOverride 500 "no";
    };
    after = [
      "podman-network-prost_prost.service"
    ];
    requires = [
      "podman-network-prost_prost.service"
    ];
    partOf = [
      "podman-compose-prost-root.target"
    ];
    wantedBy = [
      "podman-compose-prost-root.target"
    ];
  };
  virtualisation.oci-containers.containers."PRoST-Frontend" = {
    image = "";
    ports = [
      "80:80/tcp"
    ];
    log-driver = "journald";
    extraOptions = [
      "--network-alias=frontend"
      "--network=prost_default"
    ];
  };
  systemd.services."podman-PRoST-Frontend" = {
    serviceConfig = {
      Restart = lib.mkOverride 500 "no";
    };
    after = [
      "podman-network-prost_default.service"
    ];
    requires = [
      "podman-network-prost_default.service"
    ];
    partOf = [
      "podman-compose-prost-root.target"
    ];
    wantedBy = [
      "podman-compose-prost-root.target"
    ];
  };
  virtualisation.oci-containers.containers."PRoST-LDAP" = {
    image = "bitnami/openldap:latest";
    environment = {
      "LDAP_ADMIN_DN" = "cn=admin,dc=fsinfo,dc=fim,dc=uni-passau,dc=de";
      "LDAP_ADMIN_PASSWORD" = "adminpassword";
      "LDAP_ADMIN_USERNAME" = "admin";
      "LDAP_CUSTOM_LDIF_DIR" = "/ldif";
      "LDAP_ROOT" = "dc=fsinfo,dc=fim,dc=uni-passau,dc=de";
    };
    volumes = [
      "/home/hannses/programming/PRoST/ldif:/ldif:rw"
      "prost_prost-ldap:/bitnami/openldap:rw"
    ];
    log-driver = "journald";
    extraOptions = [
      "--network-alias=ldap"
      "--network=prost_prost"
    ];
  };
  systemd.services."podman-PRoST-LDAP" = {
    serviceConfig = {
      Restart = lib.mkOverride 500 "no";
    };
    after = [
      "podman-network-prost_prost.service"
      "podman-volume-prost_prost-ldap.service"
    ];
    requires = [
      "podman-network-prost_prost.service"
      "podman-volume-prost_prost-ldap.service"
    ];
    partOf = [
      "podman-compose-prost-root.target"
    ];
    wantedBy = [
      "podman-compose-prost-root.target"
    ];
  };
  virtualisation.oci-containers.containers."mariadb" = {
    image = "lscr.io/linuxserver/mariadb:latest";
    environment = {
      "MYSQL_DATABASE" = "PRoST_SQL";
      "MYSQL_PASSWORD" = "password";
      "MYSQL_ROOT_PASSWORD" = "password";
      "MYSQL_USER" = "admin";
    };
    volumes = [
      "/home/hannses/programming/PRoST/.data/maria-config:/config:rw"
    ];
    log-driver = "journald";
    extraOptions = [
      "--network-alias=mariadb"
      "--network=prost_prost"
    ];
  };
  systemd.services."podman-mariadb" = {
    serviceConfig = {
      Restart = lib.mkOverride 500 "no";
    };
    after = [
      "podman-network-prost_prost.service"
    ];
    requires = [
      "podman-network-prost_prost.service"
    ];
    partOf = [
      "podman-compose-prost-root.target"
    ];
    wantedBy = [
      "podman-compose-prost-root.target"
    ];
  };

  # Networks
  systemd.services."podman-network-prost_default" = {
    path = [ pkgs.podman ];
    serviceConfig = {
      Type = "oneshot";
      RemainAfterExit = true;
      ExecStop = "podman network rm -f prost_default";
    };
    script = ''
      podman network inspect prost_default || podman network create prost_default
    '';
    partOf = [ "podman-compose-prost-root.target" ];
    wantedBy = [ "podman-compose-prost-root.target" ];
  };
  systemd.services."podman-network-prost_prost" = {
    path = [ pkgs.podman ];
    serviceConfig = {
      Type = "oneshot";
      RemainAfterExit = true;
      ExecStop = "podman network rm -f prost_prost";
    };
    script = ''
      podman network inspect prost_prost || podman network create prost_prost
    '';
    partOf = [ "podman-compose-prost-root.target" ];
    wantedBy = [ "podman-compose-prost-root.target" ];
  };

  # Volumes
  systemd.services."podman-volume-prost_prost-ldap" = {
    path = [ pkgs.podman ];
    serviceConfig = {
      Type = "oneshot";
      RemainAfterExit = true;
    };
    script = ''
      podman volume inspect prost_prost-ldap || podman volume create prost_prost-ldap
    '';
    partOf = [ "podman-compose-prost-root.target" ];
    wantedBy = [ "podman-compose-prost-root.target" ];
  };

  # Root service
  # When started, this will automatically create all resources and start
  # the containers. When stopped, this will teardown all resources.
  systemd.targets."podman-compose-prost-root" = {
    unitConfig = {
      Description = "Root target generated by compose2nix.";
    };
    wantedBy = [ "multi-user.target" ];
  };
}
