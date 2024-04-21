package com.pegasus.application.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.pegasus.application.models.RefreshToken;
import com.pegasus.application.models.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);

  boolean existsByUserId(Long userId);

  Optional<RefreshToken> findByUserId(Long id);


  @Modifying
  int deleteByUser(User user);
}
