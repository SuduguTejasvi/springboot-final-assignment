package com.tejasvi.user_service.repository;

import com.tejasvi.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepo extends JpaRepository<User,Long> {

    @Query(value = "SELECT * FROM users WHERE trainer_id = :trainerId", nativeQuery = true)
    List<User> findByTrainerId(@Param("trainerId") long trainerId);

}
