package com.shopcart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopcart.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
