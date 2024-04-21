package com.pegasus.application.payload.response;

import lombok.Getter;

import java.util.List;

@Getter
public class UserInfoResponse {
	private Long id;
	private String email;

	private String firstname;

	private String lastname;

	private String jwtToken;

	private Float weight;

	private String refreshToken;
	private List<String> roles;

	public UserInfoResponse(Long id, String email, String jwtToken, String refreshToken,String firstname,String lastname, List<String> roles, Float weight) {
		this.id = id;
		this.email = email;
		this.roles = roles;
		this.jwtToken = jwtToken;
		this.firstname = firstname;
		this.lastname = lastname;
		this.refreshToken = refreshToken;
		this.weight = weight;
	}


	public void setId(Long id) {
		this.id = id;
	}

	public void setEmail(String email) {
		this.email = email;
	}


}
