package com.epam.finaltask.service;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.exception.ResourceNotFoundException;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository,
						   UserMapper userMapper,
						   PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserDTO register(UserDTO userDTO) {
		User user = userMapper.toUser(userDTO);
		user.setId(UUID.randomUUID());
		if (user.getPassword() != null) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		User savedUser = userRepository.save(user);
		return userMapper.toUserDTO(savedUser);
	}

	@Override
	public UserDTO updateUser(String username, UserDTO userDTO) {
		User existingUser = userRepository.findUserByUsername(username)
				.orElseThrow(() ->
						new ResourceNotFoundException("User not found: " + username));
		existingUser.setPhoneNumber(userDTO.getPhoneNumber());
		existingUser.setBalance(userDTO.getBalance() != null
				? java.math.BigDecimal.valueOf(userDTO.getBalance())
				: null);
		User updatedUser = userRepository.save(existingUser);
		return userMapper.toUserDTO(updatedUser);
	}

	@Override
	public UserDTO getUserByUsername(String username) {
		User user = userRepository.findUserByUsername(username)
				.orElseThrow(() ->
						new ResourceNotFoundException("User not found: " + username));
		return userMapper.toUserDTO(user);
	}

	@Override
	public UserDTO changeAccountStatus(UserDTO userDTO) {
		UUID userId = UUID.fromString(userDTO.getId());
		User existingUser = userRepository.findById(userId)
				.orElseThrow(() ->
						new ResourceNotFoundException("User not found with id: " + userDTO.getId()));
		User mappedUser = userMapper.toUser(userDTO);
		mappedUser.setId(existingUser.getId());
		mappedUser.setActive(userDTO.isActive());
		User updatedUser = userRepository.save(mappedUser);
		return userMapper.toUserDTO(updatedUser);
	}

	@Override
	public UserDTO getUserById(UUID id) {
		User user = userRepository.findById(id)
				.orElseThrow(() ->
						new ResourceNotFoundException("User not found with id: " + id));
		return userMapper.toUserDTO(user);
	}
}