package com.example.spstream.repositories;

import com.example.spstream.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,String> {
}
