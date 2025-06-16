package com.epam.finaltask.service;

import com.epam.finaltask.dto.SignUpRequestDTO;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.exception.ResourceAlreadyExistsException;
import com.epam.finaltask.exception.ResourceNotFoundException;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository  userRepository;
	private final UserMapper      userMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDTO register(SignUpRequestDTO dto) {
		log.debug("UserService.register() called with username='{}'", dto.getUsername());
		if (!dto.getPassword().equals(dto.getConfirmPassword())) {
			throw new IllegalArgumentException("Passwords do not match");
		}
		if (userRepository.existsByUsername(dto.getUsername())) {
			throw new ResourceAlreadyExistsException("Username already taken");
		}
		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new ResourceAlreadyExistsException("Email already taken");
		}
		if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
			throw new ResourceAlreadyExistsException("Phone number already taken");
		}

		User user = userMapper.toUser(dto);
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		user.setPhoneNumber(dto.getPhoneNumber());

		User saved = userRepository.save(user);
		log.debug("Saved user {} with phone {}", saved.getUsername(), saved.getPhoneNumber());

		return userMapper.toUserDTO(saved);
	}

	@Override
	public UserDTO getUserByUsername(String username) {
		User user = userRepository.findUserByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
		return userMapper.toUserDTO(user);
	}

	@Override
	public UserDTO updateUser(String username, UserDTO userDTO) {
		User existing = userRepository.findUserByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
		existing.setPhoneNumber(userDTO.getPhoneNumber());
		existing.setBalance(userDTO.getBalance());
		User updated = userRepository.save(existing);
		return userMapper.toUserDTO(updated);
	}

	@Override
	public UserDTO changeAccountStatus(UserDTO userDTO) {
		UUID id = UUID.fromString(String.valueOf(userDTO.getId()));
		User existing = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
		User toSave = userMapper.toUser(userDTO);
		toSave.setId(existing.getId());
		toSave.setActive(userDTO.isActive());
		User saved = userRepository.save(toSave);
		return userMapper.toUserDTO(saved);
	}

	@Override
	public UserDTO getUserById(UUID id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
		return userMapper.toUserDTO(user);
	}
}