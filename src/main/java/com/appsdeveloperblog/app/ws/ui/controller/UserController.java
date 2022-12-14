package com.appsdeveloperblog.app.ws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import org.springframework.beans.BeanUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.AddressesRest;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessages;
import com.appsdeveloperblog.app.ws.ui.model.response.OperationStatusModel;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationStatus;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;

@RestController
@RequestMapping("users") //http://localhost:8080/users

public class UserController {
	@Autowired
	UserService userService;
	
	
	@Autowired
	AddressService addressService;
	
	@Autowired
	AddressService addressesService;
	@GetMapping(path="/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
public UserRest getUser(@PathVariable String id)
{
	
		UserRest returnvalue = new UserRest();
		UserDto userDto = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDto, returnvalue);
	return returnvalue;
	
}
	@PostMapping(
			consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception
{
		UserRest returnValue = new UserRest();
		if(userDetails.getFirstName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
	
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		
		
		UserDto createdUser = userService.createUser(userDto);
		returnValue = modelMapper.map(createdUser, UserRest.class);
	return returnValue;
	
}
	@PutMapping(path="/{id}",
			consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
public UserRest updateUser(@PathVariable String id,@RequestBody UserDetailsRequestModel userDetails)
{
		UserRest returnValue = new UserRest();
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);
		UserDto updatedUser = userService.updateUser(id,userDto);
		BeanUtils.copyProperties(updatedUser, returnValue);

	
	return returnValue;
	
}
	
	@DeleteMapping(path="/{id}",
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
public OperationStatusModel deleteUser(@PathVariable String id )
{
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		userService.deleteUser(id);
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		
	return returnValue;
	
}
	
	@GetMapping()
	public List<UserRest> getUsers(@RequestParam (value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "2") int limit){
		 
		List<UserRest> returnValue = new ArrayList<>();
		List<UserDto> users = userService.getUsers(page,limit);
		
		for(UserDto userDto : users) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(userDto, userModel);
			returnValue.add(userModel);
		}
		
		return returnValue;
		
	}
	

	//http://localhost:8080/users/dfasfasdf/addresses
	@GetMapping(path="/{id}/addresses", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id)
{

		List<AddressesRest> returnValue =  new ArrayList<>();
		List<AddressDTO> addressesDTO = addressesService.getAddresses(id);
		if(addressesDTO != null && !addressesDTO.isEmpty()) {
			Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
			returnValue = new ModelMapper().map(addressesDTO, listType);
		}
		
		
		for(AddressesRest addressRest : returnValue) {
			
			
			Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId())).withSelfRel();
			
		addressRest.add(selfLink);
		}
		
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("addresses");
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(id)).withSelfRel();

		
	return CollectionModel.of(returnValue,userLink,selfLink);
	
}
	
	//http://localhost:8080/users/dfasfasdf/addresses/addressidghsadbaf
	
	@GetMapping(path="/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
public EntityModel<AddressesRest> getUserAddress(@PathVariable String addressId, @PathVariable String userId)
{

		AddressesRest returnValue =  new AddressesRest();
		AddressDTO addressesDTO = addressesService.getAddress(addressId);
		ModelMapper modelMapper = new ModelMapper();
		returnValue = modelMapper.map(addressesDTO,AddressesRest.class);
		
		//http://localhost:8080/users/<userid>/addresses/<addressid>
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).slash("addresses").slash(addressId).withRel("addresses");
		
		//http://localhost:8080/users/
		Link userAddressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
		
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(addressId,userId)).withSelfRel();
		
		//returnValue.add(userLink);
		//returnValue.add(userAddressesLink);
		//returnValue.add(selfLink);

		
	return EntityModel.of(returnValue,Arrays.asList(userLink,userAddressesLink,selfLink));
	
}
}
