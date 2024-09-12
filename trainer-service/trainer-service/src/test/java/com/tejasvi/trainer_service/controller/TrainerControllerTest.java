package com.tejasvi.trainer_service.controller;

import com.tejasvi.trainer_service.dto.TrainerDTO;
import com.tejasvi.trainer_service.service.TrainerService;
import com.tejasvi.trainer_service.util.Helper;
import com.tejasvi.trainer_service.vo.UsersTrainerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TrainerControllerTest {

    public MockMvc mockMvc;
    @Mock
    public TrainerService trainerService;
    @Mock
    public Helper helper;
    @InjectMocks
    public TrainerController trainerController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testGetAllTrainers() {
        List<TrainerDTO> mockTrainers = Arrays.asList(
                new TrainerDTO(1L, "John Doe", "Yoga","NIT certificate",4,"8888888888","Medidation"),
                new TrainerDTO(2L, "Jane Smith", "Gym","HIT certificate",7,"9999999999","Cardio")
        );

        when(trainerService.getTrainers()).thenReturn(mockTrainers);

        ResponseEntity<List<TrainerDTO>> response = trainerController.getAllTrainers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(trainerService, times(1)).getTrainers();
    }

    @Test
    public void testGetTrainerById() {
        TrainerDTO mockTrainer = new TrainerDTO(1L, "John Doe", "Yoga","NIT certificate",4,"8888888888","Medidation");

        when(trainerService.getTrainerById(1L)).thenReturn(mockTrainer);

        ResponseEntity<TrainerDTO> response = trainerController.getTrainerById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", response.getBody().getName());
        verify(trainerService, times(1)).getTrainerById(1L);
    }
    @Test
    public void testAddTrainer_Success() {
        TrainerDTO trainerDTO = new TrainerDTO(1L, "John Doe", "Yoga","NIT certificate",4,"8888888888","Medidation");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        ResponseEntity<String> response = trainerController.addTrainer(trainerDTO, bindingResult);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Trainer successfully added", response.getBody());
        verify(trainerService, times(1)).save(any(TrainerDTO.class));
        verify(helper, times(1)).handleBindingErrors(bindingResult);
    }
    @Test
    public void testAddTrainer_ValidationFailure() {
        TrainerDTO trainerDTO = new TrainerDTO(1L, "John Doe", "Yoga","NIT certificate",4,"8888888888","Medidation");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(helper.handleBindingErrors(bindingResult)).thenReturn("Validation Error");
        ResponseEntity<String> response = trainerController.addTrainer(trainerDTO, bindingResult);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Error", response.getBody());
        verify(trainerService, times(0)).save(any(TrainerDTO.class));
    }
    @Test
    public void testUpdateTrainer_Success() {
        TrainerDTO trainerDTO = new TrainerDTO(1L, "John Doe", "Yoga","NIT certificate",4,"8888888888","Medidation");;
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        ResponseEntity<String> response = trainerController.updateTrainer(trainerDTO, bindingResult, 1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Trainer successfully updated", response.getBody());
        verify(trainerService, times(1)).update(trainerDTO);
    }
    @Test
    public void testPatchTrainer_Success() {
        TrainerDTO trainerDTO = new TrainerDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(trainerService.patchTrainer(eq(1L), any(TrainerDTO.class))).thenReturn("Trainer successfully patched");
        ResponseEntity<String> response = trainerController.patchTrainer(trainerDTO, bindingResult, 1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Trainer successfully patched", response.getBody());
        verify(trainerService, times(1)).patchTrainer(1L, trainerDTO);
    }
    @Test
    public void testDeleteTrainer_Success() {
        ResponseEntity<String> response = trainerController.deleteTrainer(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Trainer successfully deleted", response.getBody());
        verify(trainerService, times(1)).delete(1L);
    }

    @Test
    public void testGetUsersByTrainerId() {
        UsersTrainerResponse mockResponse = new UsersTrainerResponse();
        when(trainerService.getUsersByTrainerId(1L)).thenReturn(mockResponse);

        ResponseEntity<UsersTrainerResponse> response = trainerController.getUsersByTrainerId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(trainerService, times(1)).getUsersByTrainerId(1L);
    }

}
