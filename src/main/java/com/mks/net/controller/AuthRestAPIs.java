package com.mks.net.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mks.net.message.request.LoginForm;
import com.mks.net.message.request.SignUpForm;
import com.mks.net.message.response.JwtResponse;
import com.mks.net.model.Otp;
import com.mks.net.model.User;
import com.mks.net.repository.UserRepository;
import com.mks.net.security.jwt.JwtProvider;
import com.mks.net.service.OkHttpService;
import com.mks.net.service.OtpGenrator;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthRestAPIs {
	
	final String API = "5dd6166c-50d7-11eb-8153-0200cd936042";
	
    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwtToken(authentication);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpForm signUpRequest, HttpSession session) throws IOException {
    	
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<String>("Fail -> Username is already taken!",HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<String>("Fail -> Email is already in use!",HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = new User(
        		signUpRequest.getName(),
        		signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getMobileNumber(),
                signUpRequest.getDob(),
                signUpRequest.getAddress()
                );

          userRepository.save(user);
          String otp = OtpGenrator.generateOTP();
          session.setAttribute("userRecord", user);
          session.setAttribute("sessionOtp", otp);
          OkHttpService.sendToFactor(API, signUpRequest.getMobileNumber(), otp);
          return ResponseEntity.ok().body("OTP ["+ otp +"] Send to your registered mobile number!");
       // return ResponseEntity.ok().body(user.toString());
    }
    
    
    @PostMapping("/otp")
    public ResponseEntity<?> getOtp(@Valid @RequestBody Otp otp, HttpSession session) {
         
    	String otp1 = otp.getOtpNumber();
    	String otp2 = (String)session.getAttribute("sessionOtp");
    	if(otp1.equals(otp2)) {
    		User user = (User)session.getAttribute("userRecord");
    	    userRepository.save(user);
    	    session.removeAttribute("sessionOtp");
    	    session.removeAttribute("userRecord");
    	    return ResponseEntity.ok("User Sucessfully registered");
    	}else {
    		 return ResponseEntity.ok("Sorry some problem to register new user. Try Again");
    	
    	}
         
    }
    
    @GetMapping(value="/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){    
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok("Sucessfully Logout");
    }
    
  }