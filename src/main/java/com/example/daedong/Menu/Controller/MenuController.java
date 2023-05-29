package com.example.daedong.Menu.Controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/daedong/menu")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MenuController {

}
