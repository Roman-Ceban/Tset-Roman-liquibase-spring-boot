package com.codingworld.liquibasedemo.userController;

import com.codingworld.liquibasedemo.configuration.AppProperties;
import com.codingworld.liquibasedemo.model.Address;
import com.codingworld.liquibasedemo.model.Company;
import com.codingworld.liquibasedemo.model.Geo;
import com.codingworld.liquibasedemo.repository.UserRepository;
import com.codingworld.liquibasedemo.model.Users;
import com.codingworld.liquibasedemo.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

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

    @ResponseBody
    private String getUser() {


        RestTemplate restTemplate = new RestTemplate();

        Users user = restTemplate.getForObject(appProperties.getUri(), Users.class);
        System.out.println("User: " + user);
        System.out.println("Userid: " + user.getId());
        System.out.println("Name: " + user.getName());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());

        Address address = user.getAddress();
        System.out.println("Address: "
                + address.getStreet() + ", "
                + address.getCity() + ", "
                + address.getZipcode()
        );

        Geo geo = address.getGeo();
        System.out.println("Geo Lat: "
                + geo.getLat() + ", Geo Lng: "
                + geo.getLng()
        );

        Company company = user.getCompany();
        System.out.println("Company: "
                + company.getName() + ", "
                + company.getCatchPhrase() + ", "
                + company.getBs()
        );

        ResponseEntity<Object[]> response = restTemplate.getForEntity(appProperties.getUri(), Object[].class);

        List<Users> users = Arrays.stream(response.getBody())
                .map(obj -> objectMapper.convertValue(obj, Users.class))
                .collect(Collectors.toList());
        System.out.println(users);
        userRepository.saveAll(users);
        return "User detail page.";
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody Users user) {
        return new ResponseEntity<>(userRepository.save(user).getId(), HttpStatus.CREATED);

    }

    @GetMapping
    public List<Users> getAllUsers() {
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













