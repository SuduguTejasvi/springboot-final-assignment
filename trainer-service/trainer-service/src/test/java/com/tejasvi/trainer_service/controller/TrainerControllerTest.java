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

    @Mock
    private BindingResult bindingResult;

    private TrainerDTO trainerDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testGetAllTrainers() {
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
    void testGetTrainerById() {
        TrainerDTO mockTrainer = new TrainerDTO(1L, "John Doe", "Yoga","NIT certificate",4,"8888888888","Medidation");

        when(trainerService.getTrainerById(1L)).thenReturn(mockTrainer);

        ResponseEntity<TrainerDTO> response = trainerController.getTrainerById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", response.getBody().getName());
        verify(trainerService, times(1)).getTrainerById(1L);
    }
    @Test
    void testAddTrainer_Success() {
        TrainerDTO trainerDto = new TrainerDTO(1L, "John Doe", "Yoga","NIT certificate",4,"8888888888","Medidation");
        BindingResult bindingRes = mock(BindingResult.class);
        when(bindingRes.hasErrors()).thenReturn(false);
        ResponseEntity<String> response = trainerController.addTrainer(trainerDto, bindingRes);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Trainer successfully added", response.getBody());
        verify(trainerService, times(1)).save(any(TrainerDTO.class));
        verify(helper, times(1)).handleBindingErrors(bindingRes);
    }
    @Test
    void testAddTrainer_ValidationFailure() {
        TrainerDTO trainerDto = new TrainerDTO(1L, "John Doe", "Yoga","NIT certificate",4,"8888888888","Medidation");
        BindingResult bindingRes = mock(BindingResult.class);
        when(bindingRes.hasErrors()).thenReturn(true);
        when(helper.handleBindingErrors(bindingRes)).thenReturn("Validation Error");
        ResponseEntity<String> response = trainerController.addTrainer(trainerDto, bindingRes);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Error", response.getBody());
        verify(trainerService, times(0)).save(any(TrainerDTO.class));
    }
    @Test
    void testUpdateTrainer_Success() {
        TrainerDTO trainerDto = new TrainerDTO(1L, "John Doe", "Yoga","NIT certificate",4,"8888888888","Medidation");;
        BindingResult bindingRes = mock(BindingResult.class);
        when(bindingRes.hasErrors()).thenReturn(false);
        ResponseEntity<String> response = trainerController.updateTrainer(trainerDto, bindingRes, 1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Trainer successfully updated", response.getBody());
        verify(trainerService, times(1)).update(trainerDto);
    }
    @Test
    void testUpdateTrainer_WhenValidationFails_ReturnsBadRequest() {
        long trainerId = 1L;
        String expectedErrorMessage = "Validation error";
        when(helper.handleBindingErrors(bindingResult)).thenReturn(expectedErrorMessage);
        ResponseEntity<String> responseEntity = trainerController.updateTrainer(trainerDTO, bindingResult, trainerId);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(expectedErrorMessage, responseEntity.getBody());
        verify(helper).handleBindingErrors(bindingResult);
    }
    @Test
    void testPatchTrainer_Success() {
        TrainerDTO trainerDto = new TrainerDTO();
        BindingResult bindingRes = mock(BindingResult.class);
        when(bindingRes.hasErrors()).thenReturn(false);
        when(trainerService.patchTrainer(eq(1L), any(TrainerDTO.class))).thenReturn("Trainer successfully patched");
        ResponseEntity<String> response = trainerController.patchTrainer(trainerDto, bindingRes, 1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Trainer successfully patched", response.getBody());
        verify(trainerService, times(1)).patchTrainer(1L,trainerDto);
    }
    @Test
    void testPatchTrainer_WhenValidationFails_ReturnsBadRequest() {
        long trainerId = 1L;
        String expectedErrorMessage = "Validation error";
        when(helper.handleBindingErrors(bindingResult)).thenReturn(expectedErrorMessage);
        ResponseEntity<String> responseEntity = trainerController.patchTrainer(trainerDTO, bindingResult, trainerId);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(expectedErrorMessage, responseEntity.getBody());
        verify(helper).handleBindingErrors(bindingResult);
        verify(trainerService, never()).patchTrainer(anyLong(), any(TrainerDTO.class));
    }
    @Test
    void testDeleteTrainer_Success() {
        ResponseEntity<String> response = trainerController.deleteTrainer(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Trainer successfully deleted", response.getBody());
        verify(trainerService, times(1)).delete(1L);
    }

    @Test
    void testGetUsersByTrainerId() {
        UsersTrainerResponse mockResponse = new UsersTrainerResponse();
        when(trainerService.getUsersByTrainerId(1L)).thenReturn(mockResponse);

        ResponseEntity<UsersTrainerResponse> response = trainerController.getUsersByTrainerId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(trainerService, times(1)).getUsersByTrainerId(1L);
    }

}
