package com.tejasvi.user_service.util;

import com.tejasvi.user_service.dto.UserDTO;
import com.tejasvi.user_service.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class Convertor {

    private final ModelMapper modelMapper;

    @Autowired
    public Convertor(ModelMapper modelMapper){
        this.modelMapper=modelMapper;
    }

    public UserDTO userEntityToDTOConvertor(User user){
        return modelMapper.map(user, UserDTO.class);
    }
    public User userDTOTOEntityConvertor(UserDTO userDTO){
        return modelMapper.map(userDTO, User.class);
    }
}
