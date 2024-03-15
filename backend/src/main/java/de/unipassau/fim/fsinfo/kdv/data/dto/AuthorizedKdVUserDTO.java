package de.unipassau.fim.fsinfo.kdv.data.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import de.unipassau.fim.fsinfo.kdv.data.UserAccessRole;
import de.unipassau.fim.fsinfo.kdv.data.dao.KdvUser;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AuthorizedKdVUserDTO {

  private String displayName;
  private String email;
  private UserAccessRole accessRole;

  public AuthorizedKdVUserDTO(KdvUser user, UserAccessRole role) {
    displayName = user.getDisplayName();
    email = user.getEmail();
    this.accessRole = role;
  }
}
