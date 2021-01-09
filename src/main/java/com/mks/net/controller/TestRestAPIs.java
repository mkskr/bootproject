package com.mks.net.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.mks.net.exeption.ResourceNotFoundException;
import com.mks.net.model.User;
import com.mks.net.repository.UserRepository;

@RestController
public class TestRestAPIs {
	
	@Autowired
	UserRepository userRepository;
	
    @GetMapping("/api/test/profiles")
	  public ResponseEntity<List<User>> profiles() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return new ResponseEntity<List<User>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    } 
	
	
	@GetMapping("/api/test/profiles/{id}")
	    public ResponseEntity<User> getProfile(@PathVariable("id") long id)  throws ResourceNotFoundException {
	         User user  = userRepository.findById(id)
	        .orElseThrow(() -> new ResourceNotFoundException("User not found for this id :: " + id));
            return ResponseEntity.ok().body(user);
	} 
	
}