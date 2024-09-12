package com.tejasvi.trainer_service.repository;

import com.tejasvi.trainer_service.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepo extends JpaRepository<Trainer,Long> {
}
