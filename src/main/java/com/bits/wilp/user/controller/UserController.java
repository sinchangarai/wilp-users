package com.bits.wilp.user.controller;

import com.bits.wilp.user.entity.AuthRequest; 
import com.bits.wilp.user.entity.UserInfo; 
import com.bits.wilp.user.service.JwtService; 
import com.bits.wilp.user.service.UserInfoService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.security.authentication.AuthenticationManager; 
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; 
import org.springframework.security.core.Authentication; 
import org.springframework.security.core.userdetails.UsernameNotFoundException; 
import org.springframework.web.bind.annotation.*; 

@RestController
@RequestMapping("/auth") 
public class UserController { 

	@Autowired
	private UserInfoService userService; 

	@Autowired
	private JwtService jwtService; 

	@Autowired
	private AuthenticationManager authenticationManager; 

	@PostMapping("/signup") 
	public ResponseEntity<?> createNewUser(@RequestBody UserInfo userInfo) { 
		try {
			userService.createUser(userInfo);
			return new ResponseEntity<UserInfo>(userInfo, HttpStatus.OK);
		} catch(Exception ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
		}
	} 


	@PostMapping("/signin") 
	public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) { 
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())); 
		if (authentication.isAuthenticated()) { 
			return jwtService.generateToken(authRequest.getUsername()); 
		} else { 
			throw new UsernameNotFoundException("invalid user request !"); 
		} 
	}


	@GetMapping("/token-expiry") 
	public ResponseEntity<?> validateTokenExpiry(HttpServletRequest request) {
		boolean isTokenExpired = true;
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) { 
			String token = authHeader.substring(7);
			if(!jwtService.isTokenExpired(token)) {
				isTokenExpired = false;
			}
		}
		return new ResponseEntity<>(isTokenExpired, HttpStatus.OK);
	}


	@GetMapping("/users") 
	@PreAuthorize("hasAuthority('ROLE_ADMIN')") 
	public ResponseEntity<?> getAllUsers() { 
		List<UserInfo> users = userService.getAllusers();
		return new ResponseEntity<>(users, users.size() > 0 ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}


	@GetMapping("/users/{id}") 
	@PreAuthorize("hasAuthority('ROLE_USER') OR hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<?> getSingleProduct(@PathVariable("id") Integer id) {
        try{
            UserInfo user = userService.getSingleUser(id);
            return new ResponseEntity<UserInfo>(user, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    } 

} 
