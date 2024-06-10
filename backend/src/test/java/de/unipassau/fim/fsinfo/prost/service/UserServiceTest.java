package de.unipassau.fim.fsinfo.prost.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserServiceTest {

  private UserService userService;

  @Mock
  private UserRepository userRepository;

  private ProstUser prostUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userService = new UserService(userRepository);

    prostUser = new ProstUser("testuser", "User One", "user1@test.com", true);
  }

  @Test
  public void testCreateUser_UserAlreadyExists_ReturnsEmpty() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.of(prostUser));
    Optional<ProstUser> result = userService.createUser(prostUser.getId(),
        prostUser.getDisplayName(),
        prostUser.getEmail(),
        true);
    assertTrue(result.isEmpty());
  }


  @Test
  public void testCreateUser_InvalidUserName_ReturnsEmpty() {
    Optional<ProstUser> result = userService.createUser("", prostUser.getDisplayName(),
        prostUser.getEmail(), true);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testCreateUser_InvalidEmail_ReturnsEmpty() {
    Optional<ProstUser> result = userService.createUser(prostUser.getId(),
        prostUser.getDisplayName(),
        "invalid-email", true);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testCreateUser_InvalidDisplayName_ReturnsEmpty() {
    Optional<ProstUser> result = userService.createUser(prostUser.getId(), "", prostUser.getEmail(),
        true);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testCreateUser_SuccessfulCreation_ReturnsUser() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.empty());
    when(userRepository.save(any(ProstUser.class))).thenReturn(prostUser);

    Optional<ProstUser> result = userService.createUser(prostUser.getId(),
        prostUser.getDisplayName(),
        prostUser.getEmail(),
        true);
    assertTrue(result.isPresent());
    assertEquals(prostUser, result.get());
  }

  @Test
  public void testInfo_AllUsers_ReturnsUsersList() {
    when(userRepository.findAll()).thenReturn(List.of(prostUser));
    Optional<List<ProstUser>> result = userService.info(null);
    assertTrue(result.isPresent());
    assertEquals(1, result.get().size());
    assertEquals(prostUser, result.get().get(0));
  }

  @Test
  public void testInfo_SpecificUser_ReturnsUser() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.of(prostUser));
    Optional<List<ProstUser>> result = userService.info(prostUser.getId());
    assertTrue(result.isPresent());
    assertEquals(1, result.get().size());
    assertEquals(prostUser, result.get().get(0));
  }

  @Test
  public void testInfo_UserNotFound_ReturnsEmpty() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.empty());
    Optional<List<ProstUser>> result = userService.info(prostUser.getId());
    assertTrue(result.isEmpty());
  }

  @Test
  public void testDelete_UserNotFound_ReturnsFalse() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.empty());
    boolean result = userService.delete(prostUser.getId());
    assertFalse(result);
  }

  @Test
  public void testDelete_SuccessfulDeletion_ReturnsTrue() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.of(prostUser));
    boolean result = userService.delete(prostUser.getId());
    assertTrue(result);
  }

  @Test
  public void testRename_UserNotFound_ReturnsFalse() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.empty());
    boolean result = userService.rename(prostUser.getId(), "New Name");
    assertFalse(result);
  }

  @Test
  public void testRename_InvalidName_ReturnsFalse() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.of(prostUser));
    boolean result = userService.rename(prostUser.getId(), "");
    assertFalse(result);
  }

  @Test
  public void testRename_SuccessfulRename_ReturnsTrue() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.of(prostUser));
    boolean result = userService.rename(prostUser.getId(), "New Name");
    assertTrue(result);
  }

  @Test
  public void testSetEmail_UserNotFound_ReturnsFalse() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.empty());
    boolean result = userService.setEmail(prostUser.getId(), "newemail@example.com");
    assertFalse(result);
  }

  @Test
  public void testSetEmail_InvalidEmail_ReturnsFalse() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.of(prostUser));
    boolean result = userService.setEmail(prostUser.getId(), "invalid-email");
    assertFalse(result);
  }

  @Test
  public void testSetEmail_SuccessfulSetEmail_ReturnsTrue() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.of(prostUser));
    boolean result = userService.setEmail(prostUser.getId(), "newemail@example.com");
    assertTrue(result);
  }

  @Test
  public void testSetEnabled_UserNotFound_ReturnsFalse() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.empty());
    boolean result = userService.setEnabled(prostUser.getId(), true);
    assertFalse(result);
  }

  @Test
  public void testSetEnabled_SuccessfulSetEnabled_ReturnsTrue() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.of(prostUser));
    boolean result = userService.setEnabled(prostUser.getId(), false);
    assertTrue(result);
  }

  @Test
  public void testSetMoneySpent_UserNotFound_ReturnsFalse() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.empty());
    boolean result = userService.setMoneySpent(prostUser.getId(), "10.00");
    assertFalse(result);
  }

  @Test
  public void testSetMoneySpent_InvalidAmount_ReturnsFalse() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.of(prostUser));
    boolean result = userService.setMoneySpent(prostUser.getId(), "invalid");
    assertFalse(result);
  }

  @Test
  public void testSetMoneySpent_WrongPrecision_ReturnsFalse() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.of(prostUser));
    boolean result = userService.setMoneySpent(prostUser.getId(), "0.001");
    assertFalse(result);
  }

  @Test
  public void testSetMoneySpent_NegativeAmount_ReturnsFalse() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.of(prostUser));
    boolean result = userService.setMoneySpent(prostUser.getId(), "-0.01");
    assertFalse(result);
  }

  @Test
  public void testSetMoneySpent_SuccessfulSetMoneySpent_ReturnsTrue() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.of(prostUser));
    boolean result = userService.setMoneySpent(prostUser.getId(), "100.00");
    assertTrue(result);
  }
}