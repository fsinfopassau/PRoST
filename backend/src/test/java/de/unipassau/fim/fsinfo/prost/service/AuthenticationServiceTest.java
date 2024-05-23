package de.unipassau.fim.fsinfo.prost.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.unipassau.fim.fsinfo.prost.data.UserAccessRole;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dto.AuthorizedPRoSTUserDTO;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.prost.security.CustomUserDetailsContextMapper.CustomUserDetails;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserService userService;

  @InjectMocks
  private AuthenticationService authenticationService;

  private Authentication auth;
  private CustomUserDetails testDetails;
  private ProstUser testUser; // Mocked user object

  @BeforeEach
  public void setUp() {

    testUser = new ProstUser("testUser", "Test User", "test@test.com",
        true);
    testDetails = new CustomUserDetails();
    testDetails.setUsername(testUser.getId());
    testDetails.setEmail(testUser.getEmail());
    testDetails.setDisplayName(testUser.getDisplayName());
    auth = mock(Authentication.class);
  }

  @Test
  public void testAuth_NullAuthentication_ReturnsEmpty() {
    Optional<AuthorizedPRoSTUserDTO> result = authenticationService.auth(null);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testAuth_NoRoles_ReturnsEmpty() {
    when(auth.getAuthorities()).thenReturn(Collections.emptyList());

    Optional<AuthorizedPRoSTUserDTO> result = authenticationService.auth(auth);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testGetHighestRole_NoRoles_ReturnsEmpty() {
    Optional<UserAccessRole> result = authenticationService.getHighestRole(Collections.emptyList());
    assertTrue(result.isEmpty());
  }

  @Test
  public void testGetHighestRole_WithRoles_ReturnsHighestRole_1() {
    UserAccessRole role1 = UserAccessRole.FSINFO;
    UserAccessRole role3 = UserAccessRole.KAFFEEKASSE;
    UserAccessRole role2 = UserAccessRole.KIOSK;

    Collection<UserAccessRole> roles = Arrays.asList(role1, role2, role3);

    Optional<UserAccessRole> result = authenticationService.getHighestRole(roles);

    assertTrue(result.isPresent());
    assertEquals(role3, result.get());
  }

  @Test
  public void testGetHighestRole_WithRoles_ReturnsHighestRole_2() {
    UserAccessRole role1 = UserAccessRole.FSINFO;

    Collection<UserAccessRole> roles = List.of(role1);

    Optional<UserAccessRole> result = authenticationService.getHighestRole(roles);

    assertTrue(result.isPresent());
    assertEquals(role1, result.get());
  }

  @Test
  public void testGetRoles_NullAuthentication_ReturnsEmptyList() {
    Collection<UserAccessRole> roles = authenticationService.getRoles(null);
    assertTrue(roles.isEmpty());
  }

  @Test
  public void testGetRoles_NoAuthorities_ReturnsEmptyList() {
    when(auth.getAuthorities()).thenReturn(Collections.emptyList());

    Collection<UserAccessRole> roles = authenticationService.getRoles(auth);
    assertTrue(roles.isEmpty());
  }

  @Test
  public void testGetRoles_WithValidAuthorities_ReturnsUserAccessRoles() {
    GrantedAuthority authority1 = new SimpleGrantedAuthority(UserAccessRole.FSINFO.name());
    GrantedAuthority authority2 = new SimpleGrantedAuthority(UserAccessRole.KIOSK.name());

    List list = new ArrayList<GrantedAuthority>();
    list.add(authority1);
    list.add(authority2);
    when(auth.getAuthorities()).thenReturn(list);

    Collection<UserAccessRole> roles = authenticationService.getRoles(auth);

    assertEquals(2, roles.size());
    assertTrue(roles.contains(UserAccessRole.FSINFO));
    assertTrue(roles.contains(UserAccessRole.KIOSK));
  }

  @Test
  public void testGetRoles_WithInvalidAuthority_ReturnsEmptyList() {
    GrantedAuthority invalidAuthority = new SimpleGrantedAuthority("INVALID_ROLE");
    List list = new ArrayList<GrantedAuthority>();
    list.add(invalidAuthority);
    
    when(auth.getAuthorities()).thenReturn(list);
    when(auth.getPrincipal()).thenReturn(testDetails);

    Collection<UserAccessRole> roles = authenticationService.getRoles(auth);

    assertTrue(roles.isEmpty());
  }

  @Test
  public void testGetRoles_WithNullAuthority_ReturnsEmptyList() {
    when(auth.getAuthorities()).thenReturn(Collections.singletonList(null));

    Collection<UserAccessRole> roles = authenticationService.getRoles(auth);

    assertTrue(roles.isEmpty());
  }

  @Test
  public void testGetRoles_WithMixedAuthorities_ReturnsValidUserAccessRoles() {
    GrantedAuthority validAuthority = new SimpleGrantedAuthority(UserAccessRole.FSINFO.name());
    GrantedAuthority invalidAuthority = new SimpleGrantedAuthority("INVALID_ROLE");
    List list = new ArrayList<GrantedAuthority>();
    list.add(validAuthority);
    list.add(invalidAuthority);

    when(auth.getAuthorities()).thenReturn(list);
    when(auth.getPrincipal()).thenReturn(testDetails);

    Collection<UserAccessRole> roles = authenticationService.getRoles(auth);

    assertEquals(1, roles.size());
    assertTrue(roles.contains(UserAccessRole.FSINFO));
  }

  @Test
  public void testAuth_ValidAuthentication_UserFoundInRepository() {
    List list = new ArrayList<GrantedAuthority>();
    list.add(new SimpleGrantedAuthority(UserAccessRole.KAFFEEKASSE.name()));
    list.add(new SimpleGrantedAuthority(UserAccessRole.FSINFO.name()));

    when(auth.getAuthorities()).thenReturn(list);
    when(auth.getPrincipal()).thenReturn(testDetails);

    when(userRepository.findById("testUser")).thenReturn(Optional.of(testUser));

    Optional<AuthorizedPRoSTUserDTO> result = authenticationService.auth(auth);

    assertTrue(result.isPresent());
    assertEquals(testUser.getId(), result.get().getId());
    assertEquals(UserAccessRole.KAFFEEKASSE, result.get().getAccessRole());
  }

  @Test
  public void testAuth_ValidAuthentication_UserNotFound_CreatedSuccessfully() {
    List list = new ArrayList<GrantedAuthority>();
    list.add(new SimpleGrantedAuthority(UserAccessRole.FSINFO.name()));

    when(auth.getAuthorities()).thenReturn(list);
    when(auth.getPrincipal()).thenReturn(testDetails);

    when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

    when(userService.createUser(testUser.getId(), testUser.getDisplayName(), testUser.getEmail(),
        false))
        .thenReturn(Optional.of(testUser));

    Optional<AuthorizedPRoSTUserDTO> result = authenticationService.auth(auth);

    assertTrue(result.isPresent());
    assertEquals(testUser.getId(), result.get().getId());
    assertEquals(UserAccessRole.FSINFO, result.get().getAccessRole());
  }

  @Test
  public void testAuth_ValidAuthentication_WithInvalidAuthorities_ReturnsEmpty() {
    List list = new ArrayList<GrantedAuthority>();
    list.add(new SimpleGrantedAuthority("INVALID_ROLE"));

    when(auth.getAuthorities()).thenReturn(list);
    when(auth.getPrincipal()).thenReturn(testDetails);

    Optional<AuthorizedPRoSTUserDTO> result = authenticationService.auth(auth);

    assertTrue(result.isEmpty());
  }

}
