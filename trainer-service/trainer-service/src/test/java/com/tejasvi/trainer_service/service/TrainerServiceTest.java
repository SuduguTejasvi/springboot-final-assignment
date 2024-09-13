package com.tejasvi.trainer_service.service.serviceimpl;

import com.tejasvi.trainer_service.dto.TrainerDTO;
import com.tejasvi.trainer_service.entity.Trainer;
import com.tejasvi.trainer_service.exception.TrainerNotFoundException;
import com.tejasvi.trainer_service.repository.TrainerRepo;
import com.tejasvi.trainer_service.service.TrainerService;
import com.tejasvi.trainer_service.util.TrainerDTOConvertor;
import com.tejasvi.trainer_service.vo.User;
import com.tejasvi.trainer_service.vo.UsersTrainerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TraineeServiceImpl implements TrainerService {
    private final TrainerRepo trainerRepo;
    private final TrainerDTOConvertor convertor;
    private final RestTemplate restTemplate;
    private final String userServiceUrl = "http://localhost:9001/user/users/";

    @Autowired
    public TraineeServiceImpl(TrainerRepo trainerRepo, TrainerDTOConvertor convertor, RestTemplate restTemplate) {
        this.trainerRepo = trainerRepo;
        this.convertor = convertor;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<TrainerDTO> getTrainers() {
        log.info("Attempting to retrieve all trainers from the database.");
        try {
            List<Trainer> trainers = trainerRepo.findAll();
            log.info("Successfully retrieved {} trainers.", trainers.size());
            return trainers.stream()
                    .map(convertor::entityToDTOConvertor)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to fetch trainers due to an unexpected error.", e);
            throw new RuntimeException("Failed to fetch trainers", e);
        }
    }

    @Override
    public TrainerDTO getTrainerById(long id) {
        log.info("Attempting to retrieve trainer with ID {}.", id);
        try {
            Optional<Trainer> trainerOptional = trainerRepo.findById(id);
            if (trainerOptional.isPresent()) {
                log.info("Successfully retrieved trainer with ID {}.", id);
                return convertor.entityToDTOConvertor(trainerOptional.get());
            } else {
                log.warn("Trainer with ID {} not found.", id);
                throw new TrainerNotFoundException("Trainer Not found");
            }
        } catch (TrainerNotFoundException ex) {
            log.error("Trainer with ID {} not found.", id);
            throw ex;
        } catch (Exception ex) {
            log.error("An unexpected error occurred while fetching trainer with ID {}.", id, ex);
            throw new RuntimeException("An unexpected error occurred while fetching the trainer", ex);
        }
    }

    @Override
    public String save(TrainerDTO trainerDTO) {
        log.info("Attempting to save a new trainer to the database.");
        try {
            trainerRepo.save(convertor.dTOToEntityConvertor(trainerDTO));
            log.info("Trainer saved successfully.");
            return "Successfully saved trainer";
        } catch (Exception e) {
            log.error("Failed to save trainer due to an unexpected error.", e);
            throw new RuntimeException("Failed to save trainer", e);
        }
    }

    @Override
    public String update(TrainerDTO trainerDTO) {
        log.info("Attempting to update trainer with ID {}.", trainerDTO.getId());
        try {
            TrainerDTO existingTrainer = getTrainerById(trainerDTO.getId());
            trainerRepo.save(convertor.dTOToEntityConvertor(existingTrainer));
            log.info("Trainer with ID {} updated successfully.", trainerDTO.getId());
            return "Successfully updated trainer";
        } catch (Exception e) {
            log.error("Failed to update trainer with ID {}.", trainerDTO.getId(), e);
            throw new RuntimeException("Failed to update trainer", e);
        }
    }

    @Override
    public String delete(long id) {
        log.info("Attempting to delete trainer with ID {}.", id);
        try {
            getTrainerById(id);
            trainerRepo.deleteById(id);
            log.info("Trainer with ID {} deleted successfully.", id);
            return "Successfully deleted trainer";
        } catch (Exception e) {
            log.error("Failed to delete trainer with ID {}.", id, e);
            throw new RuntimeException("Failed to delete trainer", e);
        }
    }

    @Override
    public UsersTrainerResponse getUsersByTrainerId(long id) {
        try {
            log.info("Attempting to fetch user and their trainer with User ID: {}", id);
            UsersTrainerResponse response = new UsersTrainerResponse();
            Trainer trainer = trainerRepo.findById(id).orElseThrow(() -> {
                log.error("Trainer with ID: {} not found. Fetching operation aborted.", id);
                return new TrainerNotFoundException("Failed to fetch users for trainer");
            });
            User[] usersArray = restTemplate.getForObject("http://localhost:9001/users/by-trainer/" + id, User[].class);
            List<User> users = (usersArray != null) ? Arrays.asList(usersArray) : Collections.emptyList();
            response.setUser(users);
            response.setTrainer(trainer);
            log.info("Successfully fetched user and trainer for User ID: {}", id);
            return response;
        } catch (Exception e) {
            log.error("Failed to fetch user and their trainer for User ID: {}", id, e);
            throw e;
        }
    }



    @Override
    public String patchTrainer(long id, TrainerDTO trainerDTO) {
        log.info("Attempting to patch trainer with ID {}.", id);
        try {
            TrainerDTO existingTrainer = getTrainerById(id);
            existingTrainer.setName(trainerDTO.getName() != null ? trainerDTO.getName() : existingTrainer.getName());
            existingTrainer.setSpeciality(trainerDTO.getSpeciality() != null ? trainerDTO.getSpeciality() : existingTrainer.getSpeciality());
            existingTrainer.setCertificate(trainerDTO.getCertificate() != null ? trainerDTO.getCertificate() : existingTrainer.getCertificate());
            existingTrainer.setExperienceYears(trainerDTO.getExperienceYears() > 0 ? trainerDTO.getExperienceYears() : existingTrainer.getExperienceYears());
            existingTrainer.setPhoneNumber(trainerDTO.getPhoneNumber() != null ? trainerDTO.getPhoneNumber() : existingTrainer.getPhoneNumber());
            existingTrainer.setWorkoutPlan(trainerDTO.getWorkoutPlan() != null ? trainerDTO.getWorkoutPlan() : existingTrainer.getWorkoutPlan());

            trainerRepo.save(convertor.dTOToEntityConvertor(existingTrainer));
            log.info("Trainer with ID {} patched successfully.", id);
            return "Successfully updated";
        } catch (Exception e) {
            log.error("Failed to patch update trainer with ID {}.", id, e);
            throw new RuntimeException("Failed to patch update trainer", e);
        }
    }
}
