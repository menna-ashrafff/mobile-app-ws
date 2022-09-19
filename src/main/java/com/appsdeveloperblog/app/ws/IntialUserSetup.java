package com.appsdeveloperblog.app.ws;

import java.util.Arrays;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.appsdeveloperblog.app.ws.io.entity.AuthorityEntity;
import com.appsdeveloperblog.app.ws.io.entity.RoleEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repositories.AuthorityRepository;
import com.appsdeveloperblog.app.ws.io.repositories.RoleRepository;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.shared.Utils;

@Component
public class IntialUserSetup {
	
	@Autowired
	AuthorityRepository authorityRepository;
	
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	Utils utils;
	
	
	@Autowired
	UserRepository userRepository;

	
	@EventListener
	@Transactional
public void onApplicationEvent(ApplicationReadyEvent event) {
	System.out.println("Fromm Application Ready event .......!");
	AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
	AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
	AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");
	
	RoleEntity roleUser = createRole("ROLE_USER",Arrays.asList(readAuthority, writeAuthority));
	
	RoleEntity roleAdmin = createRole("ROLE_ADMIN",Arrays.asList(readAuthority, writeAuthority,deleteAuthority));
	
	if(roleAdmin  == null) return;
	
	UserEntity userAdmin = new UserEntity();
	
	userAdmin.setFirstName("Menna");
	userAdmin.setLastName("Ashraf");
	userAdmin.setEmail("Menna@Ashraf.com");
	userAdmin.setEncryptedPassword(bCryptPasswordEncoder.encode("123456478"));
	userAdmin.setUserId(utils.generateUserid(30));
	userAdmin.setEmailVerficationStatus(true);
	userAdmin.setRoles(Arrays.asList(roleAdmin));
	
	userRepository.save(userAdmin);
	
	
	;


	
	
}
	@Transactional
public AuthorityEntity createAuthority(String name) {
	
	AuthorityEntity  authority = authorityRepository.findByName(name);
	if(authority == null) {
		authority=new AuthorityEntity(name);
		authorityRepository.save(authority);
	}
	
	
	
	return authority;
}

	@Transactional
public RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
	
	RoleEntity  role = roleRepository.findByName(name);
	if(role == null) {
		role =new RoleEntity(name);
		role.setAuthorities(authorities);
		roleRepository.save(role);
	}
	
	
	
	return role;
}

}
