package com.codingworld.liquibasedemo.repository;

import com.codingworld.liquibasedemo.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Integer> {

}
