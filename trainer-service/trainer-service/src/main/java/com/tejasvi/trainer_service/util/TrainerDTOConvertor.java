package com.tejasvi.trainer_service.util;

import com.tejasvi.trainer_service.dto.TrainerDTO;
import com.tejasvi.trainer_service.entity.Trainer;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;

@Controller
public class TrainerDTOConvertor {
    private final ModelMapper modelMapper;
    public TrainerDTOConvertor(ModelMapper modelMapper){
        this.modelMapper=modelMapper;
    }
    public TrainerDTO entityToDTOConvertor(Trainer trainer){
        return modelMapper.map(trainer, TrainerDTO.class);
    }
    public Trainer dTOToEntityConvertor(TrainerDTO trainerDTO){
        return modelMapper.map(trainerDTO, Trainer.class);
    }
}
