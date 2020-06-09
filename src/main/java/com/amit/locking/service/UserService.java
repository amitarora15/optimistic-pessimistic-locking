package com.amit.locking.service;

import com.amit.locking.entity.User;
import com.amit.locking.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    public User getUser(Long id){
        return userRepo.findById(id).get();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Optional<User> getUser(String name){
        return userRepo.findByName(name);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User saveUser(User user){
        return userRepo.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public User saveUserInNewTransaction(User user){
        return userRepo.save(user);
    }

}
