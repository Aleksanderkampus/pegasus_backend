package it.com.pegasus.templates.security;


import com.pegasus.application.payload.response.UserInfoResponse;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import it.com.pegasus.templates.common.EndpointTemplate;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

@Component

public class UserTemplate extends EndpointTemplate {

    public UserInfoResponse login (){
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(getCurrentUser());
        System.out.println("Send request to "+getBaseUrl()+"/signin");
        System.out.println(getBaseUrl());
        Response response = request.post(getBaseUrl()+"/api/auth/signin");
        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(UserInfoResponse.class);
    }

    public String loginAndGetToken (){
        return login().getJwtToken();
    }

    public Response logout (){
        throw new UnsupportedOperationException("not implemented");
    }
    @Override
    protected String getEndpoint() {
        return "users";
    }
}
