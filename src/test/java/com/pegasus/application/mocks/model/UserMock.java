package com.pegasus.application.mocks.model;

import com.pegasus.application.models.User;

public class UserMock {

    public static User shallowUser(Long counter) {

        return User.builder()
                .password("A mock password"+counter)
                .email("mockname.mocksurname@mockdomain.com"+counter)
                .build();
    }
}
