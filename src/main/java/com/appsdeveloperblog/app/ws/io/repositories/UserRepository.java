package com.appsdeveloperblog.app.ws.io.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.appsdeveloperblog.app.ws.io.entity.UserEntity;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
	
	 UserEntity findByEmail(String email);
	UserEntity findByUserId(String userId);
	
	@Query(value="select * from users u where u.EMAIL_VERFICATION_STATUS = true",
			countQuery = "select count(*) from users u where u.EMAIL_VERFICATION_STATUS = true",
			nativeQuery = true)
	Page<UserEntity> findAllUsersWithConfirmedEmailAddress(Pageable pagebaleRequest);

	
	@Query(value="select * from users u where u.first_name = ?1 ",nativeQuery=true)
	List<UserEntity> findUsersByFirstName(String firstName);
	
	
	@Query(value="select * from users u where u.last_name = :lastName ",nativeQuery=true)
	List<UserEntity> findUsersByLastName(@Param("lastName")String lastName);
	
	@Query(value="select * from users u where first_name LIKE %:keyword% or last_name LIKE %:keyword%",nativeQuery=true)
	List<UserEntity> findUsersByKeyword(@Param("keyword") String keyword);
	
	
	@Query(value="select first_name,last_name from Users u where first_name LIKE %:keyword% or last_name LIKE %:keyword%",nativeQuery=true)
	List<Object[]> findUserFirstNameAndLastNameByKeyword(@Param("keyword") String keyword);
	
	@Transactional
	@Modifying
	@Query(value="update users u set u.EMAIL_VERFICATION_STATUS=:emailVerificationStatus where u.user_id=:userId",nativeQuery=true)
	void updateUserEmailVerificationStatus(@Param("emailVerificationStatus") Boolean emailVerificationStatus,@Param("userId") String userId);
	
	@Query("select user from UserEntity user where user.userId =:userId")
	UserEntity findUserEntityByUserId(@Param("userId") String userId);
	
	@Query("select user.userId,user.firstName,user.lastName from UserEntity user where user.userId=:userId")
	List<Object[]> getUserEntityFullNameById(@Param("userId") String userId);
	
	@Transactional
	@Modifying
	@Query("update UserEntity u set u.emailVerficationStatus=:emailVerificationStatus where u.userId=:userId")
	void updateUserEntityEmailVerificationStatus(@Param("emailVerificationStatus") Boolean emailVerificationStatus,@Param("userId") String userId);
	
	
	
}
