package com.desafio.cooperativismo.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.BeanUtils;

import com.desafio.cooperativismo.dtos.UserDTO;
import com.desafio.cooperativismo.enums.ErrorMessageEnum;
import com.desafio.cooperativismo.exceptions.MissingParameterException;
import com.desafio.cooperativismo.models.User;

import com.desafio.cooperativismo.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Test
  void getUsers_Success() {
    User user1 = getUserAllArgs();
    User user2 = getUserAllArgs();

    List<User> usersList = new ArrayList<>(Arrays.asList(user1, user2));
    when(userRepository.findAll()).thenReturn(usersList);

    List<User> users = userService.getUsers();
    verify(userRepository).findAll();

    assert (users == usersList);
  }

  @Test
  void getUser_Success() {
    User userMock = getUserAllArgs();
    when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(userMock));

    User user = userService.getUser(userMock.getId());
    verify(userRepository).findById(userMock.getId());

    assert (user.getId() == userMock.getId());
  }

  @Test
  void saveUserAllArgs_Success() {
    UserDTO userDTO = new UserDTO();
    BeanUtils.copyProperties(getUserAllArgs(), userDTO);

    userService.saveUser(userDTO);
    verify(userRepository).save(any(User.class));
  }

  @Test
  void saveUserWithoutName_Fail() {
    MissingParameterException exception = Assertions.assertThrows(MissingParameterException.class, () -> {
      UserDTO userDTO = new UserDTO();
      BeanUtils.copyProperties(getUserAllArgs(), userDTO);
      userDTO.setName(null);

      userService.saveUser(userDTO);
      verify(userRepository, never()).save(any(User.class));
    });

    assertTrue(exception.getMessage().contains(ErrorMessageEnum.REQUIRED_NAME_FIELD.getMessage()));
  }

  @Test
  void saveUserWithoutCpf_Fail() {
    MissingParameterException exception = Assertions.assertThrows(MissingParameterException.class, () -> {
      UserDTO userDTO = new UserDTO();
      BeanUtils.copyProperties(getUserAllArgs(), userDTO);
      userDTO.setCpf(null);

      userService.saveUser(userDTO);
      verify(userRepository, never()).save(any(User.class));
    });

    assertTrue(exception.getMessage().contains(ErrorMessageEnum.REQUIRED_CPF_FIELD.getMessage()));
  }

  @Test
  void updateUserAllArgs_Success() {
    User userMock = getUserAllArgs();
    when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(userMock));

    User user = userService.getUser(userMock.getId());
    verify(userRepository).findById(userMock.getId());

    UserDTO userDTO = new UserDTO();
    BeanUtils.copyProperties(user, userDTO);
    userDTO.setName("Dummy 2");
    userDTO.setCpf("00000000002");

    userService.updateUser(user.getId(), userDTO);
    verify(userRepository).save(any(User.class));
  }

  @Test
  void updateUserWithNullName_Fail() {
    MissingParameterException exception = Assertions.assertThrows(MissingParameterException.class, () -> {
      User userMock = getUserAllArgs();
      when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(userMock));

      User user = userService.getUser(userMock.getId());
      verify(userRepository).findById(userMock.getId());

      UserDTO userDTO = new UserDTO();
      BeanUtils.copyProperties(user, userDTO);
      userDTO.setName(null);
      userDTO.setCpf("00000000002");

      userService.updateUser(user.getId(), userDTO);
      verify(userRepository, never()).save(any(User.class));
    });

    assertTrue(exception.getMessage().contains(ErrorMessageEnum.REQUIRED_NAME_FIELD.getMessage()));
  }

  @Test
  void updateUserWithNullCpf_Fail() {
    MissingParameterException exception = Assertions.assertThrows(MissingParameterException.class, () -> {
      User userMock = getUserAllArgs();
      when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(userMock));

      User user = userService.getUser(userMock.getId());
      verify(userRepository).findById(userMock.getId());

      UserDTO userDTO = new UserDTO();
      BeanUtils.copyProperties(user, userDTO);
      userDTO.setName("Dummy 2");
      userDTO.setCpf(null);

      userService.updateUser(user.getId(), userDTO);
      verify(userRepository, never()).save(any(User.class));
    });

    assertTrue(exception.getMessage().contains(ErrorMessageEnum.REQUIRED_CPF_FIELD.getMessage()));
  }

  @Test
  void deleteUser_Success() {
    User user = getUserAllArgs();

    userService.deleteUser(user.getId());
    verify(userRepository).deleteById(any(Long.class));
  }

  private static User getUserAllArgs() {
    return User
        .builder()
        .id(new Random().nextLong())
        .name("Dummy")
        .cpf("00000000001")
        .build();
  }

}
