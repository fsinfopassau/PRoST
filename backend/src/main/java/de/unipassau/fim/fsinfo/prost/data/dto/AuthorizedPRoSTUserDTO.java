package de.unipassau.fim.fsinfo.prost.data.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import de.unipassau.fim.fsinfo.prost.data.UserAccessRole;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import lombok.Getter;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
public class AuthorizedPRoSTUserDTO {

  private String displayName;
  private String email;
  private UserAccessRole accessRole;
  private String id;

  public AuthorizedPRoSTUserDTO(ProstUser user, UserAccessRole role) {
    displayName = user.getDisplayName();
    email = user.getEmail();
    id = user.getId();
    this.accessRole = role;
  }
}
