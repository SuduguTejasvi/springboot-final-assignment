package com.tejasvi.trainer_service.service;

import com.tejasvi.trainer_service.dto.TrainerDTO;
import com.tejasvi.trainer_service.vo.UsersTrainerResponse;

import java.util.List;

public interface TrainerService {

    public List<TrainerDTO> getTrainers();

    public TrainerDTO getTrainerById(long id);

    public String save(TrainerDTO trainerDTO);

    public String update(TrainerDTO trainerDTO);

    public String delete(long id);

    UsersTrainerResponse getUsersByTrainerId(long id);

    String patchTrainer(long id, TrainerDTO trainerDTO);
}
