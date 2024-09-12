package com.tejasvi.user_service.service;

import com.tejasvi.user_service.dto.UserDTO;
import com.tejasvi.user_service.vo.UserTrainerResponse;

import java.util.List;

public interface UserService {

    public List<UserDTO> getUsers();

    public UserDTO getUserById(long id);

    public String save(UserDTO userDTO);

    public String update(UserDTO userDTO);

    public String deleteById(long id);

    public  String patchUser(long id,UserDTO userDTO);

    UserTrainerResponse getUsersWithTrainers(long id);

    List<UserDTO> findByTrainerId(long trainerId);
}
