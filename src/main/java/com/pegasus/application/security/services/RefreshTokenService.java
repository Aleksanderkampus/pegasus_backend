package com.pegasus.application.security.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.pegasus.application.exeptions.TokenRefreshException;
import com.pegasus.application.models.User;
import com.pegasus.application.repository.RefreshTokenRepository;
import com.pegasus.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pegasus.application.models.RefreshToken;

@Service
public class RefreshTokenService {
  @Value("${bezkoder.app.jwtRefreshExpirationMs}")
  private Long refreshTokenDurationMs;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private UserRepository userRepository;

  public Optional<RefreshToken> findByToken(String token, String email) {
      return refreshTokenRepository.findByToken(token);

  }

  public RefreshToken createRefreshToken(Long userId) {
    RefreshToken refreshToken = new RefreshToken();

    if (refreshTokenRepository.existsByUserId(userId)) {
      refreshToken = refreshTokenRepository.findByUserId(userId).get();
    } else {
      refreshToken.setUser(userRepository.findById(userId).get());
    }

    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    refreshToken.setToken(UUID.randomUUID().toString());

    refreshToken = refreshTokenRepository.save(refreshToken);
    return refreshToken;
  }

  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
    }

    return token;
  }

  public User verifyUser(RefreshToken token, String email) {
    if (token.getUser().getEmail().equals(email)) {
      return token.getUser();
    } else {
      throw new TokenRefreshException(token.getToken(), "Refresh token was not not by correct user.");
    }
  }

  @Transactional
  public int deleteByUserId(Long userId) {
    return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
  }
}
