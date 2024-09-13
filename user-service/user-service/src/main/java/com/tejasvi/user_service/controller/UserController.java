package com.tejasvi.user_service.controller;

import com.tejasvi.user_service.dto.UserDTO;
import com.tejasvi.user_service.service.UserService;
import com.tejasvi.user_service.util.Constants;
import com.tejasvi.user_service.util.Helpers;
import com.tejasvi.user_service.vo.UserTrainerResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.BASE_PATH)
@Slf4j
public class UserController {
    private final UserService userService;
    private Helpers helpers;

    @Autowired
    public UserController(UserService userService, Helpers helpers){
        this.userService = userService;
        this.helpers = helpers;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> fetchAllUsers(){
        log.info("Fetching all user details");
        List<UserDTO> users = userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping(Constants.USER_ID_PATH)
    public ResponseEntity<UserDTO> fetchUserById(@PathVariable("userId") long userId){
        log.info("Fetching details for user with ID: {}", userId);
        UserDTO user = userService.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        String validationResult = helpers.validateUserRequest(bindingResult);
        if (validationResult != null) {
            log.error("User creation failed due to validation errors");
            return new ResponseEntity<>(validationResult, HttpStatus.BAD_REQUEST);
        }

        userService.save(userDTO);
        log.info("User created successfully with ID: {}", userDTO.getId());
        return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
    }

    @PutMapping(Constants.USER_ID_PATH)
    public ResponseEntity<String> updateUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult, @PathVariable("userId") long userId) {
        String validationResult = helpers.validateUserRequest(bindingResult);
        if (validationResult != null) {
            log.error("User update failed due to validation errors for user ID: {}", userId);
            return new ResponseEntity<>(validationResult, HttpStatus.BAD_REQUEST);
        }
        userDTO.setId(userId);
        userService.update(userDTO);
        log.info("User with ID: {} updated successfully", userId);
        return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
    }

    @PatchMapping(Constants.USER_ID_PATH)
    public ResponseEntity<String> partiallyUpdateUser(@RequestBody UserDTO userDTO, BindingResult bindingResult, @PathVariable("userId") long userId) {
        String validationResult = helpers.validateUserRequest(bindingResult);
        if (validationResult != null) {
            log.error("User patch failed due to validation errors for user ID: {}", userId);
            return new ResponseEntity<>(validationResult, HttpStatus.BAD_REQUEST);
        }
        userService.patchUser(userId, userDTO);
        log.info("User with ID: {} partially updated successfully", userId);
        return new ResponseEntity<>("User patched successfully", HttpStatus.OK);
    }

    @DeleteMapping(Constants.USER_ID_PATH)
    public ResponseEntity<String> removeUser(@PathVariable("userId") long userId){
        log.info("Deleting user with ID: {}", userId);
        userService.deleteById(userId);
        log.info("User with ID: {} deleted successfully", userId);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }

    @GetMapping(Constants.TRAINER_ID_PATH)
    public ResponseEntity<UserTrainerResponse> fetchUsersWithTrainerById(@PathVariable("userId") long userId){
        log.info("Fetching users along with trainer details for user ID: {}", userId);
        UserTrainerResponse response = userService.getUsersWithTrainers(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(Constants.BY_TRAINER_ID_PATH)
    public ResponseEntity<List<UserDTO>> fetchUsersByTrainerId(@PathVariable("trainerId") long trainerId){
        log.info("Fetching users for trainer ID: {}", trainerId);
        List<UserDTO> users = userService.findByTrainerId(trainerId);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
