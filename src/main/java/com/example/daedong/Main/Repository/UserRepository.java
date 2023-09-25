package com.example.daedong.Main.Repository;

import com.example.daedong.Dto.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findBySchoolEmail(String schoolEmail);
}
