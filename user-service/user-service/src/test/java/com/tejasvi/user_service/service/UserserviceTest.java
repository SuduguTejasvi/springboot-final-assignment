package com.tejasvi.user_service.service;

import com.tejasvi.user_service.dto.UserDTO;
import com.tejasvi.user_service.entity.User;
import com.tejasvi.user_service.enums.Level;
import com.tejasvi.user_service.exception.UserNotFoundException;
import com.tejasvi.user_service.repository.UserRepo;
import com.tejasvi.user_service.service.serviceimpl.UserServiceImpl;
import com.tejasvi.user_service.util.Convertor;
import com.tejasvi.user_service.vo.Trainer;
import com.tejasvi.user_service.vo.UserTrainerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

class UserserviceTest {

    @InjectMocks
    public UserServiceImpl userService;

    @Mock
    public UserRepo userRepo;

    @Mock
    public Convertor convertor;

    public User user;
    public UserDTO userDTO;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1L, "Alice", 25, "alice@example.com", Level.BEGINNER, 101L);
        userDTO = new UserDTO(1L, "Alice", 25, "alice@example.com", Level.ADVANCED, 101L);
    }

    @Test
    void testGetUsers() {
        when(userRepo.findAll()).thenReturn(Arrays.asList(user));
        when(convertor.userEntityToDTOConvertor(any(User.class))).thenReturn(userDTO);
        List<UserDTO> result = userService.getUsers();
        assertEquals(1, result.size());
    }
    @Test
    void testGetUsers_Failed() {
        when(userRepo.findAll()).thenThrow(new RuntimeException("Failed to retrieve users."));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUsers();
        });
        assertEquals("Failed to retrieve users.", exception.getMessage());
    }

    @Test
    void testGetUserById() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(convertor.userEntityToDTOConvertor(any(User.class))).thenReturn(userDTO);
        UserDTO result = userService.getUserById(1L);
        assertNotNull(result);
        assertEquals("Alice", result.getName());
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void testGetUserById_Failed() {
        when(userRepo.findById(anyLong())).thenThrow(new RuntimeException("An unexpected error occurred while fetching the user"));
        RuntimeException exception= assertThrows(RuntimeException.class,()->{
            userService.getUserById(1L);
        });
        assertEquals("An unexpected error occurred while fetching the user",exception.getMessage());
    }
    @Test
    void testSaveUser() {
        when(convertor.userDTOTOEntityConvertor(any(UserDTO.class))).thenReturn(user);

        String result = userService.save(userDTO);

        verify(userRepo, times(1)).save(any(User.class));
        assertEquals("Successfully saved user", result);
    }

    @Test
    void testSaveUser_Failure() {
        User mockUser = new User();
        when(convertor.userDTOTOEntityConvertor(any(UserDTO.class))).thenReturn(mockUser);
        when(userRepo.save(any(User.class))).thenThrow(new RuntimeException("Failed to save user"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.save(new UserDTO());
        });
        assertEquals("Failed to save user", exception.getMessage());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(convertor.userDTOTOEntityConvertor(any(UserDTO.class))).thenReturn(user);

        String result = userService.update(userDTO);

        verify(userRepo, times(1)).save(any(User.class));
        assertEquals("Successfully updated user", result);
    }

    @Test
    void testUpdateUserNotFound() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.update(userDTO));
    }
    @Test
    void testDeleteById() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));

        String result = userService.deleteById(1L);

        verify(userRepo, times(1)).deleteById(anyLong());
        assertEquals("Successfully deleted user", result);
    }

    @Test
    void testDeleteByIdNotFound() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteById(1L));
    }

    @Test
    void testDeleteById_Failure() {
        when(userRepo.findById(anyLong())).thenThrow(new RuntimeException("An unexpected error occurred while deleting user"));

        assertThrows(RuntimeException.class, () -> userService.deleteById(1L));
    }

    @Test
    void testPatchUser() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        UserDTO existingUserDTO = new UserDTO(1L, "Alice", 25, "alice@example.com", Level.BEGINNER, 101L);
        when(convertor.userEntityToDTOConvertor(any(User.class))).thenReturn(existingUserDTO);
        when(convertor.userDTOTOEntityConvertor(any(UserDTO.class))).thenReturn(user);

        String result = userService.patchUser(1L, userDTO);

        verify(userRepo, times(1)).save(any(User.class));
        assertEquals("User updated successfully", result);
    }

    @Test
    void testPatchUser_UserNotFound() {
        long userId = 1L;
        UserDTO userDTO = new UserDTO();
        when(userRepo.findById(userId)).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.patchUser(userId, userDTO);
        });
        assertEquals("User not found", exception.getMessage());
        verify(userRepo, times(1)).findById(userId);
        verify(userRepo, never()).save(any(User.class));
    }
    @Test
    void testPatchUser_AllFieldsUpdated() {
        long userId = 1L;
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Updated Name");
        userDTO.setAge(30);
        userDTO.setEmail("updated.email@example.com");
        userDTO.setFitnessLevel(Level.ADVANCED);
        userDTO.setTrainerId(5L);
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Original Name");
        existingUser.setAge(25);
        existingUser.setEmail("original.email@example.com");
        existingUser.setFitnessLevel(Level.BEGINNER);
        existingUser.setTrainerId(3L);
        when(userRepo.findById(userId)).thenReturn(Optional.of(existingUser));
        UserDTO existingUserDTO = new UserDTO();
        existingUserDTO.setName("Original Name");
        existingUserDTO.setAge(25);
        existingUserDTO.setEmail("original.email@example.com");
        existingUserDTO.setFitnessLevel(Level.BEGINNER);
        existingUserDTO.setTrainerId(3L);
        when(convertor.userEntityToDTOConvertor(existingUser)).thenReturn(existingUserDTO);
        when(userRepo.save(any(User.class))).thenReturn(existingUser);
        String result = userService.patchUser(userId, userDTO);
        assertEquals("User updated successfully", result);
    }
    @Test
    void testPatchUser_NoFieldsUpdated() {
        long userId = 1L;
        UserDTO userDTO = new UserDTO();
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Original Name");
        existingUser.setAge(25);
        existingUser.setEmail("original.email@example.com");
        existingUser.setFitnessLevel(Level.BEGINNER);
        existingUser.setTrainerId(3L);
        UserDTO existingUserDTO = new UserDTO();
        existingUserDTO.setName("Original Name");
        existingUserDTO.setAge(25);
        existingUserDTO.setEmail("original.email@example.com");
        existingUserDTO.setFitnessLevel(Level.BEGINNER);
        existingUserDTO.setTrainerId(3L);
        when(userRepo.findById(userId)).thenReturn(Optional.of(existingUser));
        when(convertor.userEntityToDTOConvertor(existingUser)).thenReturn(existingUserDTO);
        when(convertor.userDTOTOEntityConvertor(existingUserDTO)).thenReturn(existingUser);
        when(userRepo.save(any(User.class))).thenReturn(existingUser);
        String result = userService.patchUser(userId, userDTO);
        assertEquals("User updated successfully", result);
        verify(userRepo, times(1)).save(argThat(updatedUser ->
                updatedUser.getName().equals("Original Name") &&
                        updatedUser.getAge() == 25 &&
                        updatedUser.getEmail().equals("original.email@example.com") &&
                        updatedUser.getFitnessLevel() == Level.BEGINNER &&
                        updatedUser.getTrainerId() == 3L
        ));
        verify(convertor, times(1)).userEntityToDTOConvertor(existingUser);
    }

    @Test
    void testPatchUser_SomeFieldsUpdated() {
        long userId = 1L;
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Updated Name");
        userDTO.setAge(0);  // Age not updated
        userDTO.setEmail("updated.email@example.com");
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Original Name");
        existingUser.setAge(25);
        existingUser.setEmail("original.email@example.com");
        existingUser.setFitnessLevel(Level.BEGINNER);
        existingUser.setTrainerId(3L);
        UserDTO existingUserDTO = new UserDTO();
        existingUserDTO.setName("Original Name");
        existingUserDTO.setAge(25);
        existingUserDTO.setEmail("original.email@example.com");
        existingUserDTO.setFitnessLevel(Level.BEGINNER);
        existingUserDTO.setTrainerId(3L);
        when(userRepo.findById(userId)).thenReturn(Optional.of(existingUser));
        when(convertor.userEntityToDTOConvertor(existingUser)).thenReturn(existingUserDTO);
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Updated Name");
        updatedUser.setAge(25);
        updatedUser.setEmail("updated.email@example.com");
        updatedUser.setFitnessLevel(Level.BEGINNER);
        updatedUser.setTrainerId(3L);
        when(convertor.userDTOTOEntityConvertor(existingUserDTO)).thenReturn(updatedUser);
        when(userRepo.save(any(User.class))).thenReturn(updatedUser);
        String result = userService.patchUser(userId, userDTO);
        assertEquals("User updated successfully", result);
        verify(userRepo, times(1)).save(argThat(savedUser ->
                savedUser.getName().equals("Updated Name") &&
                        savedUser.getAge() == 25 &&  // Age should remain the same
                        savedUser.getEmail().equals("updated.email@example.com") &&
                        savedUser.getFitnessLevel() == Level.BEGINNER &&
                        savedUser.getTrainerId() == 3L
        ));
        verify(convertor, times(1)).userEntityToDTOConvertor(existingUser);
    }

    @Test
    void testGetUsersWithTrainers() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        Trainer trainer = new Trainer(); // Populate with appropriate data
        UserTrainerResponse response = new UserTrainerResponse();
        response.setUser(user);
        response.setTrainer(trainer);
        when(restTemplate.getForObject(anyString(), eq(Trainer.class))).thenReturn(trainer);

        UserTrainerResponse result = userService.getUsersWithTrainers(1L);

        assertNotNull(result);
        assertEquals(user, result.getUser());

    }
    @Test
    void testGetUsersWithTrainers_UserNotFound() {
        long userId = 1L;
        when(userRepo.findById(userId)).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUsersWithTrainers(userId);  // Call the method with the mock user ID
        });
        assertEquals("User not found", exception.getMessage());
        verify(userRepo, times(1)).findById(userId);
    }


    @Test
    void testFindByTrainerId() {
        List<User> users = Arrays.asList(user);
        when(userRepo.findByTrainerId(anyLong())).thenReturn(users);
        when(convertor.userEntityToDTOConvertor(any(User.class))).thenReturn(userDTO);

        List<UserDTO> result = userService.findByTrainerId(101L);

        assertEquals(1, result.size());
    }

    @Test
    void testFindByTrainerId_fsiled() {

        when(userRepo.findByTrainerId(anyLong())).thenThrow(new UserNotFoundException("User not found"));
       UserNotFoundException exception= assertThrows(UserNotFoundException.class,()->{
           userService.findByTrainerId(1L);
       });
       assertEquals("User not found",exception.getMessage());
    }

}
