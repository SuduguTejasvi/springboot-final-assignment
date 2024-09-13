package com.tejasvi.trainer_service.service;

import com.tejasvi.trainer_service.dto.TrainerDTO;
import com.tejasvi.trainer_service.entity.Trainer;
import com.tejasvi.trainer_service.exception.TrainerNotFoundException;
import com.tejasvi.trainer_service.repository.TrainerRepo;
import com.tejasvi.trainer_service.service.serviceimpl.TraineeServiceImpl;
import com.tejasvi.trainer_service.util.TrainerDTOConvertor;
import com.tejasvi.trainer_service.vo.User;
import com.tejasvi.trainer_service.vo.UsersTrainerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TrainerServiceTest {

    @Mock
    private TrainerRepo trainerRepo;

    @Mock
    private TrainerDTOConvertor convertor;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private TrainerDTO trainerDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTrainers_Success() {
        Trainer trainer = new Trainer();
        TrainerDTO trainerDto = new TrainerDTO();
        when(trainerRepo.findAll()).thenReturn(Collections.singletonList(trainer));
        when(convertor.entityToDTOConvertor(trainer)).thenReturn(trainerDto);

        assertEquals(Collections.singletonList(trainerDto), traineeService.getTrainers());
        verify(trainerRepo, times(1)).findAll();
        verify(convertor, times(1)).entityToDTOConvertor(trainer);
    }
    @Test
    void testGetTrainers_failure() {
        String errorMessage = "Database error";
        when(trainerRepo.findAll()).thenThrow(new RuntimeException(errorMessage));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            traineeService.getTrainers();
        });
        assertEquals("Failed to fetch trainers", exception.getMessage());
    }

    @Test
    void getTrainerById_Success() {
        Trainer trainer = new Trainer();
        TrainerDTO trainerDto = new TrainerDTO();
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.of(trainer));
        when(convertor.entityToDTOConvertor(trainer)).thenReturn(trainerDto);
        assertEquals(trainerDto, traineeService.getTrainerById(1L));
        verify(trainerRepo, times(1)).findById(1L);
        verify(convertor, times(1)).entityToDTOConvertor(trainer);
    }
    @Test
    void testGetTrainerById_Failure() {
        long trainerId = 1L;
        String unexpectedErrorMessage = "Unexpected database error";
        when(trainerRepo.findById(trainerId)).thenThrow(new RuntimeException(unexpectedErrorMessage));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            traineeService.getTrainerById(trainerId);
        });
        assertEquals("An unexpected error occurred while fetching the trainer", exception.getMessage());
        assertEquals(unexpectedErrorMessage, exception.getCause().getMessage());
    }
    @Test
    void getTrainerById_TrainerNotFound() {
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.empty());

        TrainerNotFoundException thrown = assertThrows(TrainerNotFoundException.class, () -> {
            traineeService.getTrainerById(1L);
        });
        assertEquals("Trainer Not found", thrown.getMessage());
        verify(trainerRepo, times(1)).findById(1L);
    }

    @Test
    void save_Success() {
        TrainerDTO trainerDto = new TrainerDTO();
        Trainer trainer = new Trainer();
        when(convertor.dTOToEntityConvertor(trainerDto)).thenReturn(trainer);

        assertEquals("Successfully saved trainer", traineeService.save(trainerDto));
        verify(trainerRepo, times(1)).save(trainer);
    }
    @Test
    void testSave_Failure() {
        String unexpectedErrorMessage = "Unexpected database error";
        when(trainerRepo.save(any(Trainer.class))).thenThrow(new RuntimeException(unexpectedErrorMessage));
        when(convertor.dTOToEntityConvertor(trainerDTO)).thenReturn(new Trainer());  // Mock the conversion
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            traineeService.save(trainerDTO);
        });
        assertEquals("Failed to save trainer", exception.getMessage());
        assertEquals(unexpectedErrorMessage, exception.getCause().getMessage());
    }

    @Test
    void testUpdate_Success() {
        long trainerId = 1L;
        Trainer trainer = new Trainer(trainerId, "John Doe", "Yoga", "NIT certificate", 4, "8888888888", "Meditation");
        TrainerDTO trainerDto = new TrainerDTO(trainerId, "John Doe", "Yoga", "NIT certificate", 4, "8888888888", "Meditation");
        when(trainerRepo.findById(trainerId)).thenReturn(Optional.of(trainer));
        when(convertor.entityToDTOConvertor(trainer)).thenReturn(trainerDto);
        when(convertor.dTOToEntityConvertor(trainerDto)).thenReturn(trainer);
        when(trainerRepo.save(trainer)).thenReturn(trainer);
        TrainerDTO resultDTO = traineeService.getTrainerById(trainerId);
        String updateMsg = traineeService.update(trainerDto);
        assertEquals(trainerDto, resultDTO);
        assertEquals("Successfully updated trainer", updateMsg);

    }

    @Test
    void delete_Success() {
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.of(new Trainer()));

        assertEquals("Successfully deleted trainer", traineeService.delete(1L));
        verify(trainerRepo, times(1)).findById(1L);
        verify(trainerRepo, times(1)).deleteById(1L);
    }

    @Test
    void testDelete_Failure() {
        long trainerId = 1L;

        when(trainerRepo.findById(trainerId)).thenThrow(new RuntimeException("Trainer Not found"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            traineeService.delete(trainerId);
        });

        assertEquals("Failed to delete trainer", thrown.getMessage());

    }

    @Test
    void getUsersByTrainerId_Success() {
        Trainer trainer = new Trainer();
        User user = new User();
        UsersTrainerResponse response = new UsersTrainerResponse();
        response.setTrainer(trainer);
        response.setUser(Collections.singletonList(user));
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.of(trainer));
        when(restTemplate.getForObject(anyString(), eq(User[].class)))
                .thenReturn(new User[]{user}); // Ensure this matches your actual code

        assertEquals(response, traineeService.getUsersByTrainerId(1L));
        verify(trainerRepo, times(1)).findById(1L);
        verify(restTemplate, times(1)).getForObject("http://localhost:9001/users/by-trainer/1", User[].class);
    }


    @Test
    void test_update_success(){
        Trainer trainer=new Trainer(1L, "John Doe", "Yoga","NIT certificate",4,"8888888888","Medidation");
        TrainerDTO trainerDto=new TrainerDTO(1L, "John Doe", "Yoga","NIT certificate",4,"8888888888","Medidation");
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.of(trainer));
        when(convertor.entityToDTOConvertor(trainer)).thenReturn(trainerDto);
        assertEquals(trainerDto, traineeService.getTrainerById(1L));
        String msg=traineeService.save(trainerDto);
        assertEquals("Successfully saved trainer",msg);
    }
    @Test
    void test_update_notFoundId(){
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.empty());
        TrainerNotFoundException trow=assertThrows(TrainerNotFoundException.class,()->{
            traineeService.getTrainerById(anyLong());
        });
        assertEquals("Trainer Not found",trow.getMessage());
        verify(trainerRepo,times(1)).findById(anyLong());
    }
    @Test
    void testUpdate_Failure() {
        TrainerDTO trainerDto= new TrainerDTO();
        trainerDto.setId(1L);
        when(trainerRepo.findById(anyLong())).thenThrow(new RuntimeException("Trainer not found"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            traineeService.update(trainerDto);
        });

        assertEquals("Failed to update trainer", exception.getMessage());

    }


    @Test
    void test_patch_success(){
        Trainer trainer=new Trainer();
        TrainerDTO trainerDto=new TrainerDTO();
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.of(trainer));
        when(convertor.entityToDTOConvertor(trainer)).thenReturn(trainerDto);
        assertEquals(trainerDto, traineeService.getTrainerById(1L));
        String msg=traineeService.patchTrainer(1L,trainerDto);
        assertEquals("Successfully updated",msg);

    }
    @Test
    void testPatchTrainer_Failure() {
        long trainerId = 1L;
        TrainerDTO updateDTO = new TrainerDTO(trainerId, "New Name", null, null, 0, "0987654321", null);

        when(trainerRepo.findById(trainerId)).thenThrow(new RuntimeException("Trainer not found"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            traineeService.patchTrainer(trainerId, updateDTO);
        });

        assertEquals("Failed to patch update trainer", thrown.getMessage());

    }

    @Test
    void testPatchTrainer_InvalidExperienceYears() {
        long trainerId = 1L;
        TrainerDTO existingTrainerDTO = new TrainerDTO(trainerId, "Old Name", "Old Speciality", "Old Certificate", 5, "1234567890", "Old Workout Plan");
        TrainerDTO updateDTO = new TrainerDTO(trainerId, null, null, null, -1, null, null);
        Trainer existingTrainer = new Trainer(trainerId, "Old Name", "Old Speciality", "Old Certificate", 5, "1234567890", "Old Workout Plan");
        when(trainerRepo.findById(trainerId)).thenReturn(Optional.of(existingTrainer));
        when(convertor.dTOToEntityConvertor(existingTrainerDTO)).thenReturn(existingTrainer);
        when(trainerRepo.save(any(Trainer.class))).thenThrow(new RuntimeException("Trainer not found"));

       RuntimeException exception= assertThrows(RuntimeException.class,()->{
           traineeService.patchTrainer(trainerId, updateDTO);
       });
       assertEquals("Failed to patch update trainer",exception.getMessage());
    }

    @Test
    void testPatchTrainer_PartialUpdate() {
        // Given
        TrainerDTO existingTrainer = new TrainerDTO();
        existingTrainer.setId(1L);
        existingTrainer.setName("Old Name");
        existingTrainer.setSpeciality("Old Speciality");
        existingTrainer.setCertificate("Old Certificate");
        existingTrainer.setExperienceYears(5);
        existingTrainer.setPhoneNumber("1234567890");
        existingTrainer.setWorkoutPlan("Old Plan");
        Trainer trainer=new Trainer(1L,"Old Name","Old Speciality","Old Certificate",5,"1234567890","Old Plan");
        TrainerDTO updateDTO = new TrainerDTO();
        updateDTO.setName("New Name");
        updateDTO.setSpeciality(null);
        updateDTO.setCertificate("New Certificate");
        updateDTO.setExperienceYears(0);
        updateDTO.setPhoneNumber(null);
        updateDTO.setWorkoutPlan("New Plan");
        when(trainerRepo.findById(1L)).thenReturn(Optional.of(trainer));
        when(convertor.entityToDTOConvertor(trainer)).thenReturn(existingTrainer);
        String result=traineeService.patchTrainer(1L,updateDTO);
        assertEquals("New Name",existingTrainer.getName());
        assertEquals("New Certificate",existingTrainer.getCertificate());
        assertEquals("New Plan",existingTrainer.getWorkoutPlan());
        assertEquals("Old Speciality",existingTrainer.getSpeciality());
        assertEquals(5,existingTrainer.getExperienceYears());
        assertEquals("1234567890",existingTrainer.getPhoneNumber());
        assertEquals("Successfully updated",result);
    }

    @Test
    void testPatchTrainer_CompleteUpdate() {
        TrainerDTO existingTrainer = new TrainerDTO();
        existingTrainer.setId(1L);
        existingTrainer.setName("Old Name");
        existingTrainer.setSpeciality("Old Speciality");
        existingTrainer.setCertificate("Old Certificate");
        existingTrainer.setExperienceYears(5);
        existingTrainer.setPhoneNumber("1234567890");
        existingTrainer.setWorkoutPlan("Old Plan");
        Trainer trainer=new Trainer(1L,"Old Name","Old Speciality","Old Certificate",5,"1234567890","Old Plan");
        TrainerDTO updateDTO = new TrainerDTO();
        updateDTO.setName("New Name");
        updateDTO.setSpeciality("New Speciality");
        updateDTO.setCertificate("New Certificate");
        updateDTO.setExperienceYears(6);
        updateDTO.setPhoneNumber("2345678901");
        updateDTO.setWorkoutPlan("New Plan");
        when(trainerRepo.findById(1L)).thenReturn(Optional.of(trainer));
        when(convertor.entityToDTOConvertor(trainer)).thenReturn(existingTrainer);
        String result=traineeService.patchTrainer(1L,updateDTO);
        assertEquals("New Name",existingTrainer.getName());
        assertEquals("New Certificate",existingTrainer.getCertificate());
        assertEquals("New Plan",existingTrainer.getWorkoutPlan());
        assertEquals("New Speciality",existingTrainer.getSpeciality());
        assertEquals(6,existingTrainer.getExperienceYears());
        assertEquals("2345678901",existingTrainer.getPhoneNumber());
        assertEquals("Successfully updated",result);
    }

    @Test
    void testPatchTrainer_NofeildUpdated() {
        TrainerDTO existingTrainer = new TrainerDTO();
        existingTrainer.setId(1L);
        existingTrainer.setName("Old Name");
        existingTrainer.setSpeciality("Old Speciality");
        existingTrainer.setCertificate("Old Certificate");
        existingTrainer.setExperienceYears(5);
        existingTrainer.setPhoneNumber("1234567890");
        existingTrainer.setWorkoutPlan("Old Plan");
        Trainer trainer=new Trainer(1L,"Old Name","Old Speciality","Old Certificate",5,"1234567890","Old Plan");
        TrainerDTO updateDTO = new TrainerDTO();
        updateDTO.setName(null);
        updateDTO.setSpeciality(null);
        updateDTO.setCertificate(null);
        updateDTO.setExperienceYears(0);
        updateDTO.setPhoneNumber(null);
        updateDTO.setWorkoutPlan(null);
        when(trainerRepo.findById(1L)).thenReturn(Optional.of(trainer));
        when(convertor.entityToDTOConvertor(trainer)).thenReturn(existingTrainer);
        String result=traineeService.patchTrainer(1L,updateDTO);
        assertEquals("Old Name",existingTrainer.getName());
        assertEquals("Old Certificate",existingTrainer.getCertificate());
        assertEquals("Old Plan",existingTrainer.getWorkoutPlan());
        assertEquals("Old Speciality",existingTrainer.getSpeciality());
        assertEquals(5,existingTrainer.getExperienceYears());
        assertEquals("1234567890",existingTrainer.getPhoneNumber());
        assertEquals("Successfully updated",result);
    }



    @Test
    void delete_Failure(){
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.empty());
        TrainerNotFoundException trow=assertThrows(TrainerNotFoundException.class,()->{
            traineeService.getTrainerById(anyLong());
        });
        assertEquals("Trainer Not found",trow.getMessage());
        verify(trainerRepo,times(1)).findById(anyLong());

    }
    @Test
    void testGetUsersByTrainerId_Success() {
        long trainerId = 1L;
        Trainer trainer = new Trainer();
        trainer.setId(trainerId);

        User[] usersArray = { new User(), new User() };

        when(trainerRepo.findById(trainerId)).thenReturn(Optional.of(trainer));
        when(restTemplate.getForObject("http://localhost:9001/users/by-trainer/" + trainerId, User[].class))
                .thenReturn(usersArray); // Ensure this matches your actual code

        UsersTrainerResponse response = traineeService.getUsersByTrainerId(trainerId);

        assertNotNull(response);
        assertEquals(trainer, response.getTrainer());
        assertEquals(2, response.getUser().size());
    }

    @Test
    void testGetUsersByTrainerId_UsersArrayNull() {
        long trainerId = 1L;
        Trainer trainer = new Trainer();
        trainer.setId(trainerId);

        when(trainerRepo.findById(trainerId)).thenReturn(Optional.of(trainer));
        when(restTemplate.getForObject("http://localhost:9001/users/by-trainer/" + trainerId, User[].class))
                .thenReturn(null);

        UsersTrainerResponse response = traineeService.getUsersByTrainerId(trainerId);

        assertNotNull(response);
        assertEquals(trainer, response.getTrainer());
        assertTrue(response.getUser().isEmpty());
    }


    @Test
    void testGetUsersByTrainerId_TrainerNotFound() {
        long trainerId = 1L;
        when(trainerRepo.findById(trainerId)).thenReturn(Optional.empty());
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            traineeService.getUsersByTrainerId(trainerId);
        });
        assertEquals("Failed to fetch users for trainer", thrown.getMessage());
    }

    @Test
    void testGetUsersByTrainerId_RestTemplateError() {
        long trainerId = 1L;
        Trainer trainer = new Trainer();
        trainer.setId(trainerId);

        when(trainerRepo.findById(trainerId)).thenReturn(Optional.of(trainer));
        when(restTemplate.getForObject("http://localhost:9001/users/by-trainer/" + trainerId, User[].class))
                .thenThrow(new RuntimeException("External service error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            traineeService.getUsersByTrainerId(trainerId);
        });

        assertEquals("External service error", thrown.getMessage());
    }

}
