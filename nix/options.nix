{ lib, ... }:
let
  inherit (lib) mkEnableOption mkOption;
  inherit (lib.types) str port package;
in {

  options.services.prost = {
    enable = mkEnableOption "PRoST";
    port = mkOption {
      type = port;
      default = 80;
    };
    frontend.package = mkOption { type = package; };
    #TODO: when building from source also allow basepath
    ldapUri = mkOption {
      type = str;
      default = "ldap://ldap:1389/dc=fsinfo,dc=fim,dc=uni-passau,dc=de";
    };
    backend = {
      volumePath = mkOption {
        type = str;
        default = "/home/hannses/tmp/.data";
      };
      #package = ...
      #port can not be specified as it seems hardcoded in some places
    };
    ldap = {
      admin = {
        dn = mkOption {
          type = str;
          default = "cn=admin,dc=fsinfo,dc=fim,dc=uni-passau,dc=de";

        };
        password = mkOption {
          type = str;
          default = "adminpassword";
        };
        username = mkOption {
          type = str;
          default = "admin";
        };
      };

      dir = mkOption {
        type = str;
        default = "/home/hannses/tmp/ldif";
      };
      root = mkOption {
        type = str;
        default = "dc=fsinfo,dc=fim,dc=uni-passau,dc=de";
      };

    };
    mariadb = {
      database = mkOption {
        type = str;
        default = "PRoST_SQL";
      };
      password = mkOption {
        type = str;
        default = "password";
      };
      root_password = mkOption {
        type = str;
        default = "password";
      };
      user = mkOption {
        type = str;
        default = "admin";
      };
      configDir = mkOption {
        type = str;
        default = "/home/hannses/tmp/.data/maria-config";
      };
    };
  };
}
