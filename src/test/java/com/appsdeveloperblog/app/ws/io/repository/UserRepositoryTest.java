package com.appsdeveloperblog.app.ws.io.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.appsdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;



@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class UserRepositoryTest {
	
	
	boolean recordsCreated = false;
	
	@Autowired
	UserRepository userRepository;
	

	@BeforeAll
	
	void setUp() throws Exception {
		
	createRecords();
	}

	@Test
	void testGetVerifedUsers() {
		
		Pageable pageableRequest = PageRequest.of(0, 2);
		Page<UserEntity> pages = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);
		assertNotNull(pages);
	}
	
	@Test void testFindUserByFirstName(){
		
		String firstName="Sergey";
		List<UserEntity> users = userRepository.findUsersByFirstName(firstName);
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		System.out.println("number of users in first name:" + users.size());

		


		
		}
	
	@Test
	void testFindUserByLastName(){
		
		String lastName="Kar";
		List<UserEntity> users = userRepository.findUsersByLastName(lastName);
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		System.out.println("number of users on lastname :" + users.size());

		
	}

	@Test void testUsersByKeyWord(){
		
		String keyword="ge";
		List<UserEntity> users = userRepository.findUsersByKeyword(keyword);
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		System.out.println("number of users in keyword:" + users.size());
		}


	@Test void testfindUserFirstNameAndLastNameByKeyword(){
		
		String keyword="ge";
		List<Object[]> users = userRepository.findUserFirstNameAndLastNameByKeyword(keyword);
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		System.out.println("number of users in keyword:" + users.size());
		}
	
	@Test void testUpdateEmailVericationStatus() {
		
		boolean newEmailVerificationStatus = true;
		userRepository.updateUserEmailVerificationStatus(newEmailVerificationStatus, "1a2b3c");
		
		UserEntity storedUserDetails = userRepository.findByUserId("1a2b3c");
		
		boolean storedEmailVerificationStatus = storedUserDetails.getEmailVerficationStatus();
		
		assertTrue(storedEmailVerificationStatus == newEmailVerificationStatus);
		
	}
	
	@Test
	void testFindUserEntityByUserId() {
		
		String userid  = "1a2b3c";
		UserEntity user = userRepository.findUserEntityByUserId(userid);
		assertNotNull(user);
		assertTrue(user.getUserId().equals(userid));
	}
	
	@Test
	void testGetUserFullNameById() {
		String userid  = "1a2b3c";
		List<Object[]> records =  userRepository.getUserEntityFullNameById(userid);
		assertNotNull(records);
		assertTrue(records.size() == 1);
		System.out.println("number of users in getuserFullName:" + records.size());
		
        Object[] userDetails = records.get(0);

        
        String firstName = String.valueOf(userDetails[0]);
        String lastName = String.valueOf(userDetails[1]);

        assertNotNull(firstName);
        assertNotNull(lastName);
	}
	
	@Test
	void updateUserEntityEmailVerificationStatus() {
		
		
		boolean newEmailVerificationStatus = false;
		userRepository.updateUserEntityEmailVerificationStatus(newEmailVerificationStatus, "1a2b3c");
		
		UserEntity storedUserDetails = userRepository.findByUserId("1a2b3c");
		
		boolean storedEmailVerificationStatus = storedUserDetails.getEmailVerficationStatus();
		
		assertTrue(storedEmailVerificationStatus == newEmailVerificationStatus);
	}





void createRecords() {
	// Prepare User Entity
    UserEntity userEntity = new UserEntity();
    userEntity.setFirstName("Sergey");
    userEntity.setLastName("Kar");
    userEntity.setUserId("1a2b3c");
    userEntity.setEncryptedPassword("xxx");
    userEntity.setEmail("test@test.com");
    userEntity.setEmailVerficationStatus(true);	     
    // Prepare User Addresses
    AddressEntity addressEntity = new AddressEntity();
    addressEntity.setType("shipping");
    addressEntity.setAddressId("ahgyt74hfy");
    addressEntity.setCity("Vancouver");
    addressEntity.setCountry("Canada");
    addressEntity.setPostalCode("ABCCDA");
    addressEntity.setStreetName("123 Street Address");

    List<AddressEntity> addresses = new ArrayList<>();
    addresses.add(addressEntity);
    
    userEntity.setAddresses(addresses);
    
    userRepository.save(userEntity);
    
    
    
    
		// Prepare User Entity
    UserEntity userEntity2 = new UserEntity();
    userEntity2.setFirstName("Sergey");
    userEntity2.setLastName("Kar");
    userEntity2.setUserId("1a2b3cd");
    userEntity2.setEncryptedPassword("xxx");
    userEntity2.setEmail("test@test.com");
    userEntity2.setEmailVerficationStatus(true);
    
    // Prepare User Addresses
    AddressEntity addressEntity2 = new AddressEntity();
    addressEntity2.setType("shipping");
    addressEntity2.setAddressId("ahgyt74hfywwww");
    addressEntity2.setCity("Vancouver");
    addressEntity2.setCountry("Canada");
    addressEntity2.setPostalCode("ABCCDA");
    addressEntity2.setStreetName("123 Street Address");

    List<AddressEntity> addresses2 = new ArrayList<>();
    addresses2.add(addressEntity2);
    
    userEntity2.setAddresses(addresses2);
    
    userRepository.save(userEntity2);
    
     recordsCreated = true;

	
}



}