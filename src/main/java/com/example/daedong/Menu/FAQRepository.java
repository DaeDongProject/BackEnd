package com.example.daedong.Menu;

import com.example.daedong.Dto.FAQs;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FAQRepository extends MongoRepository<FAQs, String> {
}
