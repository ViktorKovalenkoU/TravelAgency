package com.epam.finaltask.service;

import java.util.UUID;

import com.epam.finaltask.dto.UserDTO;
import org.springframework.stereotype.Service;

public interface UserService {
    UserDTO register(UserDTO userDTO);

    UserDTO updateUser(String username, UserDTO userDTO);

    UserDTO getUserByUsername(String username);
    UserDTO changeAccountStatus(UserDTO userDTO);
    UserDTO getUserById(UUID id);
}
