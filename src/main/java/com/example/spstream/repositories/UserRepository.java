package com.example.spstream.repositories;

import com.example.spstream.entities.EUser;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<EUser,String> {
}
