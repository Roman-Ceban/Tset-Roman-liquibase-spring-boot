package com.codingworld.liquibasedemo.userController;

import com.codingworld.liquibasedemo.model.Address;
import com.codingworld.liquibasedemo.model.Company;
import com.codingworld.liquibasedemo.model.Geo;
import com.codingworld.liquibasedemo.repository.UserRepository;
import com.codingworld.liquibasedemo.model.Users;
import com.codingworld.liquibasedemo.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UsersService userService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    public UserController(ObjectMapper objectMapper, UserRepository userRepository, UsersService userService) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @ResponseBody
    private String getUser() {
        String uri = "https://jsonplaceholder.typicode.com/users/1";
        RestTemplate restTemplate = new RestTemplate();

        Users user = restTemplate.getForObject(uri, Users.class);
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

        ResponseEntity<Object[]> response = restTemplate.getForEntity("https://jsonplaceholder.typicode.com/users", Object[].class);

        List<Users> users = Arrays.stream(response.getBody())
                .map(obj -> objectMapper.convertValue(obj, Users.class))
                .collect(Collectors.toList());
        System.out.println(users);
        userRepository.saveAll(users);
        return "User detail page.";
    }

    @GetMapping()
    public ResponseEntity findAll() {
        List<Users> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.CREATED).body(users);
    }

    @GetMapping("/populate")
    public void populateFromUrl() {
        getUser();
    }

    @PostMapping("/user-create")
    public Users createUser(@RequestBody Users user){
        userService.saveUser(user);
        return userService.saveUser(user);
    }

    @GetMapping("user-delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id){
        userService.deleteById(id);
        return "redirect:/users";
    }

    @GetMapping("/user-update/{id}")
    public String updateUserForm(@PathVariable("id") Integer id, Model model){
        Users user = userService.findById(id);
        model.addAttribute("user", user);
        return "user-update";
    }

    @PostMapping("/user-update")
    public String updateUser(Users user){
        userService.saveUser(user);
        return "redirect:/users";
    }

    @DeleteMapping("/user/{id}")
    public String delete(@PathVariable("id") Integer id){
        userRepository.deleteById(id);
        return "deleted";
    }
}















