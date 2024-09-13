package com.tejasvi.trainer_service.controller;

import com.tejasvi.trainer_service.util.Constant;
import com.tejasvi.trainer_service.dto.TrainerDTO;
import com.tejasvi.trainer_service.service.TrainerService;
import com.tejasvi.trainer_service.util.Helper;
import com.tejasvi.trainer_service.vo.UsersTrainerResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(Constant.BASE)
public class TrainerController {

    private final TrainerService trainerService;
    private final Helper helper;

    @Autowired
    public TrainerController(TrainerService trainerService, Helper helper) {
        this.trainerService = trainerService;
        this.helper = helper;
    }

    @GetMapping
    public ResponseEntity<List<TrainerDTO>> getAllTrainers() {
        log.info("Fetching all trainers");
        List<TrainerDTO> trainers = trainerService.getTrainers();
        return new ResponseEntity<>(trainers, HttpStatus.OK);
    }

    @GetMapping(Constant.GET_TRAINER_BY_ID)
    public ResponseEntity<TrainerDTO> getTrainerById(@PathVariable("id") long id) {
        log.info("Fetching trainer with ID {}", id);
        TrainerDTO trainer = trainerService.getTrainerById(id);
        return new ResponseEntity<>(trainer, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> addTrainer(@Valid @RequestBody TrainerDTO trainerDTO, BindingResult bindingResult) {
        String errorMessage = helper.handleBindingErrors(bindingResult);
        if (errorMessage != null) {
            log.error("Validation failed for adding trainer: {}", errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
        trainerService.save(trainerDTO);
        log.info("Trainer added successfully with ID {}", trainerDTO.getId());
        return new ResponseEntity<>("Trainer successfully added", HttpStatus.CREATED);
    }

    @PutMapping(Constant.UPDATE_TRAINER)
    public ResponseEntity<String> updateTrainer(@Valid @RequestBody TrainerDTO trainerDTO, BindingResult bindingResult, @PathVariable("id") long id) {
        String errorMessage = helper.handleBindingErrors(bindingResult);
        if (errorMessage != null) {
            log.error("Validation failed for updating trainer with ID {}: {}", id, errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
        trainerDTO.setId(id);
        trainerService.update(trainerDTO);
        log.info("Trainer with ID {} updated successfully", id);
        return new ResponseEntity<>("Trainer successfully updated", HttpStatus.OK);
    }

    @PatchMapping(Constant.PATCH_TRAINER)
    public ResponseEntity<String> patchTrainer(@RequestBody TrainerDTO trainerDTO, BindingResult bindingResult, @PathVariable("id") long id) {
        String errorMessage = helper.handleBindingErrors(bindingResult);
        if (errorMessage != null) {
            log.error("Validation failed for patching trainer with ID {}: {}", id, errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
        String responseMessage = trainerService.patchTrainer(id, trainerDTO);
        log.info("Trainer with ID {} patched successfully", id);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @DeleteMapping(Constant.DELETE_TRAINER)
    public ResponseEntity<String> deleteTrainer(@PathVariable("id") long id) {
        log.info("Deleting trainer with ID {}", id);
        trainerService.delete(id);
        log.info("Trainer with ID {} deleted successfully", id);
        return new ResponseEntity<>("Trainer successfully deleted", HttpStatus.OK);
    }

    @GetMapping(Constant.GET_USERS_BY_TRAINER_ID)
    public ResponseEntity<UsersTrainerResponse> getUsersByTrainerId(@PathVariable("id") long id) {
        log.info("Fetching users associated with trainer ID {}", id);
        UsersTrainerResponse response = trainerService.getUsersByTrainerId(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
