package com.example.spstream.services;

import com.example.spstream.entities.EUser;
import com.example.spstream.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    public EUser getUserById(String id){
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("no user with id %s", id)));
    }

    public boolean exists(String id){
        return userRepository.existsById(id);
    }

}
