package com.appsdeveloperblog.app.ws.service.impl;

import java.util.ArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.Utils;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;

@Service
public class UserServiceImpl implements UserService {

	@Autowired 
	UserRepository userRepository;
	
	@Autowired
	 Utils utils;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	@Override
	public UserDto createUser(UserDto user) {
		//checking if the user is unique
		
		if(userRepository.findByEmail(user.getEmail()) != null) throw new RuntimeException("user already exists") ;
		//copy user dto to userentity
		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);
		
		// encrypt user password by using encode method in bcryptpasswordEncoder
		
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		// generating public secure userid 
		String publicUserid = utils.generateUserid(30);
		userEntity.setUserId(publicUserid);
		
		//use userrepository to save user into the database
		userRepository.save(userEntity);
		
		// return dto to be converted to userest as a response to user
		
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
		
		return returnValue;
	}
	
	@Override
	public  UserDto getUser(String email) {
		
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(userEntity == null) throw new UsernameNotFoundException(email) ;
		UserDto returnvalue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnvalue);
		return returnvalue;
	}
	

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(email);
		if(userEntity == null) throw new UsernameNotFoundException(email) ;
		return new User(userEntity.getEmail(),userEntity.getEncryptedPassword(),new ArrayList<>());
	}

	@Override
	public UserDto getUserByUserId(String userId) {

		UserEntity userEntity = userRepository.findByUserId(userId);
		if(userEntity == null) throw new UsernameNotFoundException(userId) ;
		UserDto returnvalue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnvalue);
		

		
		
		return returnvalue;
	}

	@Override
	public UserDto updateUser(String userId, UserDto user) {
		UserDto returnvalue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		if(userEntity == null) throw new UserServiceException("") ;
		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());
		UserEntity updatesUserEntity = userRepository.save(userEntity);
		BeanUtils.copyProperties(updatesUserEntity, returnvalue);

		return returnvalue;
	}



}
