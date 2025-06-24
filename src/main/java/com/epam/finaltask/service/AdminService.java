package com.epam.finaltask.service;

import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepo;

    @Transactional
    public void blockUser(UUID userId) {
        userRepo.findById(userId)
                .ifPresent(u -> u.setLocked(true));
    }


    @Transactional
    public void unblockUser(UUID userId) {
        userRepo.findById(userId)
                .ifPresent(u -> u.setLocked(false));
    }

    public List<User> findAllUsers() {
        return userRepo.findAll();
    }
}
