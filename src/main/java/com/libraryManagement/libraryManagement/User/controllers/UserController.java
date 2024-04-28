package com.libraryManagement.libraryManagement.User.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.libraryManagement.libraryManagement.User.model.request.AuthenticationRequestModel;
import com.libraryManagement.libraryManagement.User.model.response.AuthenticationResModel;
import com.libraryManagement.libraryManagement.User.serviceImpl.UserServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {
	@Autowired
	 UserServiceImpl userService;
	
	  @PostMapping("/register")
	  public ResponseEntity<AuthenticationResModel> register(
	      @RequestBody AuthenticationRequestModel request
	  ) {
	    return ResponseEntity.ok(userService.register(request));
	  }
	  @PostMapping("/authenticate")
	  public ResponseEntity<AuthenticationResModel> authenticate(
	      @RequestBody AuthenticationRequestModel request
	  ) {
	    return ResponseEntity.ok(userService.authenticate(request));
	  }

}
