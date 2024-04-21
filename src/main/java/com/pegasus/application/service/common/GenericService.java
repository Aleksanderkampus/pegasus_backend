package com.pegasus.application.service.common;

import com.pegasus.application.models.User;
import com.pegasus.application.security.services.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@AllArgsConstructor
@NoArgsConstructor
public class GenericService {

    @Autowired
    private UserDetailsServiceImpl userService;

    protected User getCurrentUserAsEntity() {
        return userService.getCurrentUser();
    }
}