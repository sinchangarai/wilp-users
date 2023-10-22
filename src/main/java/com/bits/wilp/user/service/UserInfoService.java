package com.bits.wilp.user.service;

import com.bits.wilp.user.entity.UserInfo;
import com.bits.wilp.user.entity.UserInfoDetails;
import com.bits.wilp.user.repository.UserInfoRepository; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.security.core.userdetails.UserDetails; 
import org.springframework.security.core.userdetails.UserDetailsService; 
import org.springframework.security.core.userdetails.UsernameNotFoundException; 
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional; 

@Service
public class UserInfoService implements UserDetailsService { 

	@Autowired
	private UserInfoRepository repository; 

	@Autowired
	private PasswordEncoder encoder; 

	
	public List<UserInfo> getAllusers() {
        List<UserInfo> allUsers = repository.findAll();
        if(allUsers.size() > 0)
            return allUsers;
        else
            return new ArrayList<UserInfo>();
    }

 
    public UserInfo getSingleUser(Integer id) {
        Optional<UserInfo> userOptional = repository.findById(id);
        if(userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
    }

	public void createUser(UserInfo userInfo) { 
		Optional<UserInfo> userOptional = repository.findByName(userInfo.getName());
		if(!userOptional.isPresent()) {
			userInfo.setPassword(encoder.encode(userInfo.getPassword())); 
			repository.save(userInfo); 
		} else {
			throw new IllegalArgumentException("Username: " + userInfo.getName() + " already exists");
		}
	}
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { 

		Optional<UserInfo> userDetail = repository.findByName(username); 

		// Converting userDetail to UserDetails 
		return userDetail.map(UserInfoDetails::new) 
				.orElseThrow(() -> new UsernameNotFoundException("User not found " + username)); 
	}

} 
