package com.example.spstream.repositories;

import com.example.spstream.entities.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;


public interface EventRepository extends CrudRepository<Event,String> {
}
