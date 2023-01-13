package com.codingworld.liquibasedemo.web;

import com.codingworld.liquibasedemo.configuration.AppProperties;
import com.codingworld.liquibasedemo.model.Address;
import com.codingworld.liquibasedemo.model.Company;
import com.codingworld.liquibasedemo.model.Geo;
import com.codingworld.liquibasedemo.repository.UserRepository;
import com.codingworld.liquibasedemo.model.Users;
import com.codingworld.liquibasedemo.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    Logger log = LoggerFactory.getLogger(UserController.class);


    private final UsersService userService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    private final AppProperties appProperties;

    public UserController(ObjectMapper objectMapper, UserRepository userRepository, UsersService userService, AppProperties appProperties) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.userService = userService;
        this.appProperties = appProperties;
    }

    @GetMapping("/populate")
    private ResponseEntity<Void> getUser() {


        RestTemplate restTemplate = new RestTemplate();

        Users user = restTemplate.getForObject(appProperties.getUri(), Users.class);
        log.debug("User: {}", user);
        log.debug("Userid:{}", user.getId());
        log.debug("Name:{}", user.getName());
        log.debug("Username{}", user.getUsername());
        log.debug("Email:{}", user.getEmail());

        Address address = user.getAddress();
        log.debug("Address:{}", address.getStreet(),
                address.getCity(), address.getZipcode());

        Geo geo = address.getGeo();
        log.debug("Geo Lat:{}", geo.getLat());
        log.debug("Geo Lng:{}", geo.getLng());

        Company company = user.getCompany();
        log.debug("Company:{}", company.getName(),
                company.getCatchPhrase(),
                company.getBs());

        ResponseEntity<Object[]> response = restTemplate.getForEntity(appProperties.getUri(), Object[].class);
        List<Users> users = Arrays.stream(response.getBody())
                .map(obj -> objectMapper.convertValue(obj, Users.class))
                .collect(Collectors.toList());
        System.out.println(users);
        userRepository.saveAll(users);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Users> addUser(@RequestBody Users user) {
        return new ResponseEntity<>(userRepository.save(user), HttpStatus.CREATED);

    }

    @GetMapping
    public List<Users> getAllUsers() {
        log.debug("GET request all users.");
        List<Users> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }


    @PutMapping
    public ResponseEntity<String> updateUser(@RequestBody Users user) {
        if (userRepository.existsById(user.getId())) {
            userRepository.save(user);
            return new ResponseEntity<>("updated", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("user not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}")
    public Users getUserById(@PathVariable("id") Integer id) {
        return userRepository.findById(id).stream().findFirst().get();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") Integer id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return new ResponseEntity<>("deleted", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("not found", HttpStatus.NOT_FOUND);
    }

}













