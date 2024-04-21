package com.pegasus.application.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.pegasus.application.payload.response.JWTResponse;
import com.pegasus.application.exeptions.TokenRefreshException;
import com.pegasus.application.repository.UserRepository;
import com.pegasus.application.security.services.UserDetailsImpl;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pegasus.application.models.ERole;
import com.pegasus.application.models.RefreshToken;
import com.pegasus.application.models.Role;
import com.pegasus.application.models.User;
import com.pegasus.application.payload.request.LoginRequest;
import com.pegasus.application.payload.request.SignupRequest;
import com.pegasus.application.payload.response.UserInfoResponse;
import com.pegasus.application.payload.response.MessageResponse;
import com.pegasus.application.repository.RoleRepository;
import com.pegasus.application.security.jwt.JwtUtils;
import com.pegasus.application.security.services.RefreshTokenService;

//for Angular Client (withCredentials)
//@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials="true")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  RefreshTokenService refreshTokenService;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

    String jwtToken = jwtUtils.generateTokenFromEmail(userDetails.getEmail());

    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());
    
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
    
    ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

    return ResponseEntity.ok()
              .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
              .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
              .body(new UserInfoResponse(userDetails.getId(),
                                         userDetails.getEmail(),
                                         jwtToken,
                                          refreshToken.getToken(),
                                          userDetails.getFirstname(),
                                          userDetails.getLastname(),
                                         roles, userDetails.getWeight()));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken!"));
    }

    // Create new user's account
    User user = new User(
                         signUpRequest.getEmail(),
                         encoder.encode(signUpRequest.getPassword()), signUpRequest.getFirstname(), signUpRequest.getLastname(), signUpRequest.getWeight());

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);

          break;
        default:
          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    User savedUser = userRepository.save(user);

    List<String> savedRoles = savedUser.getRoles().stream()
            .map(item -> item.getName().name())
            .toList();

    String jwtToken = jwtUtils.generateTokenFromEmail(savedUser.getEmail());

    RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getId());

    return ResponseEntity.ok().body(new UserInfoResponse(savedUser.getId(),
            savedUser.getEmail(),
            jwtToken,
            refreshToken.getToken(),
            savedUser.getFirstname(),
            savedUser.getLastname(),
            savedRoles, savedUser.getWeight()));
  }

  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principle.toString() != "anonymousUser") {      
      Long userId = ((UserDetailsImpl) principle).getId();
      refreshTokenService.deleteByUserId(userId);
    }
    
    ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
    ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
        .body(new MessageResponse("You've been signed out!"));
  }

  @PostMapping("/refreshtoken")
  public ResponseEntity<?> refreshtoken(@Valid @RequestBody JWTResponse jwtResponse) {
    String refreshToken = jwtResponse.getRefreshToken();
    String email = jwtUtils.getEmailFromJwtToken(jwtResponse.getJwtToken());
    if ((refreshToken != null) && (!refreshToken.isEmpty())) {
      return refreshTokenService.findByToken(refreshToken, email)
          .map(refreshTokenService::verifyExpiration)
          .map((e) -> refreshTokenService.verifyUser(e, email))
          .map(user -> {
            String jwt = jwtUtils.generateTokenFromEmail(user.getEmail());
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new JWTResponse(jwt, refreshToken));
          })
          .orElseThrow(() -> new TokenRefreshException(refreshToken,
              "Refresh token is not in database!"));
    }
    
    return ResponseEntity.badRequest().body(new MessageResponse("Refresh Token is empty!"));
  }
}
