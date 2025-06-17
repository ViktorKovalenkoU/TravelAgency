package com.epam.finaltask.service;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.SignUpRequestDTO;
import java.util.UUID;

public interface UserService {
    UserDTO register(SignUpRequestDTO dto);
    UserDTO getUserByUsername(String username);
    UserDTO updateUser(String username, UserDTO userDTO);
    UserDTO changeAccountStatus(UserDTO userDTO);
    UserDTO getUserById(UUID id);

    void increaseFailedAttempts(String username);
    void resetFailedAttempts(String username);
    boolean isUserLocked(String username);
}