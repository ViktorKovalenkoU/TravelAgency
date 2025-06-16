package com.epam.finaltask.service;

import com.epam.finaltask.dto.SignUpRequestDTO;
import com.epam.finaltask.dto.UserDTO;

import java.util.UUID;

public interface UserService {
    UserDTO register(SignUpRequestDTO dto);
    UserDTO getUserByUsername(String username);
    UserDTO updateUser(String username, UserDTO userDTO);
    UserDTO changeAccountStatus(UserDTO userDTO);
    UserDTO getUserById(UUID id);
}
