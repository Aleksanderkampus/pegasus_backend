package com.pegasus.application.repository;

import com.pegasus.application.models.Run;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RunRepository extends JpaRepository<Run, Long> {
    List<Run> findAllByUserEmail(String email);

}
