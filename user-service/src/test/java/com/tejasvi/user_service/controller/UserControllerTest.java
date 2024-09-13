package com.tejasvi.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tejasvi.user_service.dto.UserDTO;
import com.tejasvi.user_service.enums.Level;
import com.tejasvi.user_service.service.UserService;
import com.tejasvi.user_service.util.Constants;
import com.tejasvi.user_service.util.Helpers;
import com.tejasvi.user_service.vo.UserTrainerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    public MockMvc mockMvc;
    @Mock
    public UserService userService;
    @InjectMocks
    public UserController userController;
    @Mock
    public Helpers helpers;

    public ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testFetchAllUsers() throws Exception {
        UserDTO user1 = new UserDTO(1L, "Alice", 25, "alice@example.com", Level.BEGINNER, 1L);
        UserDTO user2 = new UserDTO(2L, "Bob", 28, "bob@example.com", Level.BEGINNER, 2L);
        List<UserDTO> users= Arrays.asList(user1,user2);

        when(userService.getUsers()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get(Constants.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    public void testFetchUserById() throws Exception{
        UserDTO user = new UserDTO(1L, "Alice", 25, "alice@example.com", Level.BEGINNER, 101L);

        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get(Constants.BASE_PATH + "/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
    @Test
    public void testCreateUser() throws Exception {
        UserDTO userDTO = new UserDTO(1L, "Alice", 25, "alice@example.com", Level.BEGINNER, 101L);
        when(helpers.validateUserRequest(any())).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.post(Constants.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

    }
    @Test
    public void testCreateUserValidationError() throws Exception {
        UserDTO userDTO = new UserDTO(1L, "", 17, "invalid-email", Level.BEGINNER, 101L);
        when(helpers.validateUserRequest(org.mockito.ArgumentMatchers.any())).thenReturn("Invalid details provided");

        mockMvc.perform(MockMvcRequestBuilders.post(Constants.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserDTO userDTO = new UserDTO(1L, "Alice", 25, "alice@example.com", Level.BEGINNER, 101L);
        when(helpers.validateUserRequest(org.mockito.ArgumentMatchers.any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.put(Constants.BASE_PATH + "/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk());
    }
    @Test
    public void testUpdateUserWithInvalidDetails() throws Exception {
        UserDTO userDTO = new UserDTO(1L, "Alice", 25, "alice@example.com", Level.BEGINNER, 101L);
        when(helpers.validateUserRequest(org.mockito.ArgumentMatchers.any())).thenReturn("Invalid details provided");

        mockMvc.perform(MockMvcRequestBuilders.put(Constants.BASE_PATH + "/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPatchUser() throws Exception {
        UserDTO userDTO = new UserDTO(null, "Alice", 43, null, null, null);
        when(helpers.validateUserRequest(org.mockito.ArgumentMatchers.any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.patch(Constants.BASE_PATH + "/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk());
    }
    @Test
    public void testpatchUserWithInvalidDetails() throws Exception {
        UserDTO userDTO = new UserDTO(1L, "Alice", 25, null,null, null);
        when(helpers.validateUserRequest(org.mockito.ArgumentMatchers.any())).thenReturn("Invalid details provided");

        mockMvc.perform(MockMvcRequestBuilders.patch(Constants.BASE_PATH + "/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void testRemoveUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(Constants.BASE_PATH + "/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testFetchUsersWithTrainerById() throws Exception {
        UserTrainerResponse response = new UserTrainerResponse();

        when(userService.getUsersWithTrainers(1L)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get(Constants.BASE_PATH + Constants.TRAINER_ID_PATH, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testFetchUsersByTrainerId() throws Exception {
        UserDTO user1 = new UserDTO(1L, "Alice", 25, "alice@example.com", Level.BEGINNER, 101L);
        UserDTO user2 = new UserDTO(2L, "Bob", 30, "bob@example.com", Level.INTERMEDIATE, 101L);
        List<UserDTO> users = Arrays.asList(user1, user2);

        when(userService.findByTrainerId(101L)).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get(Constants.BASE_PATH + Constants.BY_TRAINER_ID_PATH, 101L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
