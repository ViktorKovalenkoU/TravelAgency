package com.epam.finaltask.service;

import com.epam.finaltask.dto.UserDTO;
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

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDTO register(UserDTO userDTO) {
		log.debug("Received register request with UserDTO: {}", userDTO);
		User user;
		try {
			// Перетворення DTO в сутність
			user = userMapper.toUser(userDTO);
			log.debug("Mapped User: {}", user);

			// Генеруємо UUID для нового користувача
			user.setId(UUID.randomUUID());

			// Кодування пароля, якщо він не порожній
			if (user.getPassword() != null) {
				String rawPassword = user.getPassword();
				String encodedPassword = passwordEncoder.encode(rawPassword);
				user.setPassword(encodedPassword);
				log.debug("Encoded password for user {}: {}", user.getUsername(), encodedPassword);
			}

			// Збереження користувача в базі даних
			User savedUser = userRepository.save(user);
			log.debug("Saved User: {}", savedUser);

			// Перетворення сутності назад у DTO для відповіді
			return userMapper.toUserDTO(savedUser);
		} catch (Exception ex) {
			log.error("Exception during registration for UserDTO: {}", userDTO, ex);
			throw ex; // GlobalExceptionHandler має перехопити це виключення
		}
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