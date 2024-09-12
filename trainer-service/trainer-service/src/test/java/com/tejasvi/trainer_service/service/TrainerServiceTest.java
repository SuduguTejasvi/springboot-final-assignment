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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTrainers_Success() {
        Trainer trainer = new Trainer();
        TrainerDTO trainerDTO = new TrainerDTO();
        when(trainerRepo.findAll()).thenReturn(Collections.singletonList(trainer));
        when(convertor.entityToDTOConvertor(trainer)).thenReturn(trainerDTO);

        assertEquals(Collections.singletonList(trainerDTO), traineeService.getTrainers());
        verify(trainerRepo, times(1)).findAll();
        verify(convertor, times(1)).entityToDTOConvertor(trainer);
    }

    @Test
    void getTrainerById_Success() {
        Trainer trainer = new Trainer();
        TrainerDTO trainerDTO = new TrainerDTO();
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.of(trainer));
        when(convertor.entityToDTOConvertor(trainer)).thenReturn(trainerDTO);

        assertEquals(trainerDTO, traineeService.getTrainerById(1L));
        verify(trainerRepo, times(1)).findById(1L);
        verify(convertor, times(1)).entityToDTOConvertor(trainer);
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
        TrainerDTO trainerDTO = new TrainerDTO();
        Trainer trainer = new Trainer();
        when(convertor.dTOToEntityConvertor(trainerDTO)).thenReturn(trainer);

        assertEquals("Successfully saved trainer", traineeService.save(trainerDTO));
        verify(trainerRepo, times(1)).save(trainer);
    }


    @Test
    void delete_Success() {
        TrainerDTO trainerDTO = new TrainerDTO();
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.of(new Trainer()));

        assertEquals("Successfully deleted trainer", traineeService.delete(1L));
        verify(trainerRepo, times(1)).findById(1L);
        verify(trainerRepo, times(1)).deleteById(1L);
    }

    @Test
    void getUsersByTrainerId_Success() {
        Trainer trainer = new Trainer();
        User user = new User();
        UsersTrainerResponse response = new UsersTrainerResponse();
        response.setTrainer(trainer);
        response.setUser(Collections.singletonList(user));
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.of(trainer));
        when(restTemplate.getForEntity(anyString(), eq(User[].class)))
                .thenReturn(ResponseEntity.ok(new User[]{user}));

        assertEquals(response, traineeService.getUsersByTrainerId(1L));
        verify(trainerRepo, times(1)).findById(1L);
        verify(restTemplate, times(1)).getForEntity("http://localhost:9001/user/users/1", User[].class);
    }

    @Test
    public void test_update_success(){
        Trainer trainer=new Trainer(1L, "John Doe", "Yoga","NIT certificate",4,"8888888888","Medidation");
        long trainer_id=1L;
        TrainerDTO trainerDTO=new TrainerDTO(1L, "John Doe", "Yoga","NIT certificate",4,"8888888888","Medidation");
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.of(trainer));
        when(convertor.entityToDTOConvertor(trainer)).thenReturn(trainerDTO);
        assertEquals(trainerDTO, traineeService.getTrainerById(1L));
        String msg=traineeService.save(trainerDTO);
        assertEquals("Successfully saved trainer",msg);
    }
    @Test
    public void test_update_failure(){
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.empty());
        TrainerNotFoundException trow=assertThrows(TrainerNotFoundException.class,()->{
            traineeService.getTrainerById(anyLong());
        });
        assertEquals("Trainer Not found",trow.getMessage());
        verify(trainerRepo,times(1)).findById(anyLong());
    }
    @Test
    public void test_patch_success(){
        Trainer trainer=new Trainer();
        TrainerDTO trainerDTO=new TrainerDTO();
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.of(trainer));
        when(convertor.entityToDTOConvertor(trainer)).thenReturn(trainerDTO);
        assertEquals(trainerDTO, traineeService.getTrainerById(1L));
        String msg=traineeService.patchTrainer(1L,trainerDTO);
        assertEquals("Successfully updated",msg);

    }
    @Test
    public void test_patch_failure(){
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.empty());
        TrainerNotFoundException trow=assertThrows(TrainerNotFoundException.class,()->{
            traineeService.getTrainerById(anyLong());
        });
        assertEquals("Trainer Not found",trow.getMessage());
        verify(trainerRepo,times(1)).findById(anyLong());
    }
    @Test
    public void delete_failure(){
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.empty());
        TrainerNotFoundException trow=assertThrows(TrainerNotFoundException.class,()->{
            traineeService.getTrainerById(anyLong());
        });
        assertEquals("Trainer Not found",trow.getMessage());
        verify(trainerRepo,times(1)).findById(anyLong());

    }
    @Test
    public void testGetUsersByTrainerId_Success() {
        long trainerId = 1L;
        Trainer trainer = new Trainer();
        trainer.setId(trainerId);

        User[] usersArray = { new User(), new User() };
        ResponseEntity<User[]> responseEntity = ResponseEntity.ok(usersArray);

        when(trainerRepo.findById(trainerId)).thenReturn(Optional.of(trainer));
        when(restTemplate.getForEntity("http://localhost:9001/user/users/" + trainerId, User[].class))
                .thenReturn(responseEntity);
        UsersTrainerResponse response = traineeService.getUsersByTrainerId(trainerId);
        assertNotNull(response);
        assertEquals(trainer, response.getTrainer());
        assertEquals(2, response.getUser().size());
    }

    @Test
    public void testGetUsersByTrainerId_TrainerNotFound() {
        long trainerId = 1L;
        when(trainerRepo.findById(trainerId)).thenReturn(Optional.empty());
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            traineeService.getUsersByTrainerId(trainerId);
        });
        assertEquals("Failed to fetch users for trainer", thrown.getMessage());
    }

    @Test
    public void testGetUsersByTrainerId_RestTemplateError() {
        long trainerId = 1L;
        Trainer trainer = new Trainer();
        trainer.setId(trainerId);

        when(trainerRepo.findById(trainerId)).thenReturn(Optional.of(trainer));
        when(restTemplate.getForEntity("http://localhost:9001/user/users/" + trainerId, User[].class))
                .thenThrow(new RuntimeException("External service error"));
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            traineeService.getUsersByTrainerId(trainerId);
        });
        assertEquals("Failed to fetch users for trainer", thrown.getMessage());
    }
}
