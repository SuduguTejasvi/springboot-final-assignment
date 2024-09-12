package com.tejasvi.user_service.service.serviceimpl;

import com.tejasvi.user_service.dto.UserDTO;
import com.tejasvi.user_service.entity.User;
import com.tejasvi.user_service.exception.UserNotFoundException;
import com.tejasvi.user_service.repository.UserRepo;
import com.tejasvi.user_service.service.UserService;
import com.tejasvi.user_service.util.Convertor;
import com.tejasvi.user_service.vo.Trainer;
import com.tejasvi.user_service.vo.UserTrainerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final Convertor convertor;
    private final RestTemplate restTemplate;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, Convertor convertor, RestTemplate restTemplate) {
        this.userRepo = userRepo;
        this.convertor = convertor;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<UserDTO> getUsers() {
        try {
            log.info("Attempting to retrieve all users...");
            List<User> users = userRepo.findAll();
            log.info("Successfully retrieved {} users.", users.size());
            return users.stream()
                    .map(user -> convertor.userEntityToDTOConvertor(user))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to retrieve users.", e);
            throw e;
        }
    }

    @Override
    public UserDTO getUserById(long id) {
        try {
            log.info("Attempting to retrieve user with ID: {}", id);
            Optional<User> userOptional = userRepo.findById(id);
            if (userOptional.isPresent()) {
                log.info("Successfully retrieved user with ID: {}", id);
                return convertor.userEntityToDTOConvertor(userOptional.get());
            } else {
                log.error("User with ID: {} not found.", id);
                throw new UserNotFoundException("User not found");
            }
        } catch (UserNotFoundException ex) {
            log.warn("User with ID: {} not found. Throwing UserNotFoundException.", id);
            throw ex;
        } catch (Exception ex) {
            log.error("An unexpected error occurred while fetching user with ID: {}", id, ex);
            throw new RuntimeException("An unexpected error occurred while fetching the user", ex);
        }
    }

    @Override
    public String save(UserDTO userDTO) {
        try {
            log.info("Attempting to save user with details: {}", userDTO);
            userRepo.save(convertor.userDTOTOEntityConvertor(userDTO));
            log.info("Successfully saved user with ID: {}", userDTO.getId());
            return "Successfully saved user";
        } catch (Exception e) {
            log.error("Failed to save user with details: {}", userDTO, e);
            throw e;
        }
    }

    @Override
    public String update(UserDTO userDTO) {
        try {
            log.info("Attempting to update user with ID: {}", userDTO.getId());
            Optional<User> userOptional = userRepo.findById(userDTO.getId());
            if (userOptional.isPresent()) {
                log.info("User with ID: {} found. Proceeding with update.", userDTO.getId());
                userRepo.save(convertor.userDTOTOEntityConvertor(userDTO));
                log.info("Successfully updated user with ID: {}", userDTO.getId());
                return "Successfully updated user";
            } else {
                log.error("User with ID: {} not found. Update operation aborted.", userDTO.getId());
                throw new UserNotFoundException("User not found");
            }
        } catch (Exception e) {
            log.error("Failed to update user with ID: {}", userDTO.getId(), e);
            throw e;
        }
    }

    @Override
    public String deleteById(long id) {
        try {
            log.info("Attempting to delete user with ID: {}", id);
            Optional<User> userOptional = userRepo.findById(id);
            if (userOptional.isPresent()) {
                userRepo.deleteById(id);
                log.info("Successfully deleted user with ID: {}", id);
                return "Successfully deleted user";
            } else {
                log.error("User with ID: {} not found. Deletion operation aborted.", id);
                throw new UserNotFoundException("User not found");
            }
        } catch (UserNotFoundException e) {
            log.warn("User with ID: {} not found. Throwing UserNotFoundException.", id);
            throw e;
        } catch (Exception e) {
            log.error("An unexpected error occurred while deleting user with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public String patchUser(long id, UserDTO userDTO) {
        try {
            log.info("Attempting to patch user with ID: {}", id);
            Optional<User> userOptional = userRepo.findById(id);
            if (userOptional.isPresent()) {
                log.info("User with ID: {} found. Proceeding with patch update.", id);
                UserDTO existingUser = convertor.userEntityToDTOConvertor(userOptional.get());
                existingUser.setName(userDTO.getName() != null ? userDTO.getName() : existingUser.getName());
                existingUser.setAge(userDTO.getAge() != 0 ? userDTO.getAge() : existingUser.getAge());
                existingUser.setEmail(userDTO.getEmail() != null ? userDTO.getEmail() : existingUser.getEmail());
                existingUser.setFitnessLevel(userDTO.getFitnessLevel() != null ? userDTO.getFitnessLevel() : existingUser.getFitnessLevel());
                existingUser.setTrainerId(userDTO.getTrainerId() != null ? userDTO.getTrainerId() : existingUser.getTrainerId());
                User updatedUser = convertor.userDTOTOEntityConvertor(existingUser);
                userRepo.save(updatedUser);
                log.info("Successfully patched user with ID: {}", id);
                return "User updated successfully";
            } else {
                log.error("User with ID: {} not found. Patch update operation aborted.", id);
                throw new UserNotFoundException("User not found");
            }
        } catch (Exception e) {
            log.error("Failed to patch update user with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public UserTrainerResponse getUsersWithTrainers(long id) {
        try {
            log.info("Attempting to fetch user and their trainer with User ID: {}", id);
            UserTrainerResponse response = new UserTrainerResponse();
            User user = userRepo.findById(id).orElseThrow(() -> {
                log.error("User with ID: {} not found. Fetching operation aborted.", id);
                return new UserNotFoundException("User not found");
            });
            Trainer trainer = restTemplate.getForObject("http://localhost:9002/trainer/" + user.getTrainerId(), Trainer.class);
            response.setUser(user);
            response.setTrainer(trainer);
            log.info("Successfully fetched user and trainer for User ID: {}", id);
            return response;
        } catch (Exception e) {
            log.error("Failed to fetch user and their trainer for User ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<UserDTO> findByTrainerId(long trainerId) {
        try {
            log.info("Attempting to retrieve users with Trainer ID: {}", trainerId);
            List<User> users = userRepo.findByTrainerId(trainerId);
            log.info("Successfully retrieved {} users with Trainer ID: {}", users.size(), trainerId);
            return users.stream()
                    .map(user -> convertor.userEntityToDTOConvertor(user))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to retrieve users with Trainer ID: {}", trainerId, e);
            throw e;
        }
    }
}
