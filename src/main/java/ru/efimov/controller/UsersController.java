package ru.efimov.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.efimov.model.Users;
import ru.efimov.repository.UsersRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8085")
@RestController
@RequestMapping("/api")
public class UsersController {
    @Autowired
    UsersRepository usersRepository;


    @GetMapping("/filterStreet")
    public ResponseEntity<List<Users>> getAllUsersWithStreet(@RequestParam(required = false) String street) {
        try {
            List<Users> users = new ArrayList<Users>();

            // Integer avgAge = 0;
            if (street == null)
                usersRepository.findAll().forEach(users::add);
            else{
                usersRepository.findByStreet(street).forEach(users::add);
            }


            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/filterCountryAndAge")
    public ResponseEntity<List<Users>> getAllUsersWithCountryAndAge(@RequestParam(required = false) String country,
                                                                    @RequestParam(required = false) String gender) {
        try {
            List<Users> users = new ArrayList<Users>();

            // Integer avgAge = 0;
            if (country == null || gender == null)
                usersRepository.findAll().forEach(users::add);
            else{
                usersRepository.findByCountryAndAge(country, gender).forEach(users::add);
            }


            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/filterCountry")
    public ResponseEntity<List<Users>> getAllUsersWithCountry(@RequestParam(required = false) String country) {
        try {
            List<Users> users = new ArrayList<Users>();

           // Integer avgAge = 0;
            if (country == null)
                usersRepository.findAll().forEach(users::add);
            else{
                usersRepository.findByCountryOrderByName(country).forEach(users::add);
            }


            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/averageAge")
    public ResponseEntity<Integer> getAverageAgeWhereName(@RequestParam(required = false) String name) {
        try {
            List<Users> users = new ArrayList<Users>();

            Integer avgAge = 0;
            if (name == null)
                usersRepository.findAll().forEach(users::add);
            else{
                avgAge = usersRepository.averageAgeFilterByName(name);
            }


            if (avgAge == 0) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(avgAge, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        try {

            List<Users> users = new ArrayList<Users>();
            usersRepository.findAll().forEach(users::add);

            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable("id") long id) {
        Optional<Users> usersData = usersRepository.findById(id);

        if (usersData.isPresent()) {
            return new ResponseEntity<>(usersData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<Users> createUser() {
        try {
            URL url = new URL("https://randomuser.me/api/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            StringBuilder builder = new StringBuilder();
            while ((output = br.readLine()) != null) {
                builder.append(output);
            }

            conn.disconnect();

            String stri = builder.toString();
            String[] spis = stri.split("\"");

            int idAge = 0;
            for (String m : spis){
                if (m.equals("age")){
                    break;
                }
                idAge++;
            }

            int idUsername = 0;
            for (String m : spis){
                if (m.equals("username")){
                    break;
                }
                idUsername++;
            }

            int idPassword = 0;
            for (String m : spis){
                if (m.equals("password")){
                    break;
                }
                idPassword++;
            }


            Users newUser = new Users(spis[5], spis[15], spis[19], spis[26].substring(1) + spis[29],
                    spis[33], spis[37], spis[41], spis[idUsername+2], spis[idPassword+2], spis[117],
                    Integer.parseInt(spis[idAge+1].substring(1, spis[idAge+1].length()-2)));


            Users _users = usersRepository
                    .save(newUser);
            return new ResponseEntity<>(_users, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable("id") long id, @RequestBody Users users) {
        Optional<Users> userData = usersRepository.findById(id);

        if (userData.isPresent()) {
            Users _users = userData.get();
            _users.setName(users.getName());
            _users.setGender(users.getGender());
            _users.setSurname(users.getSurname());
            _users.setAge(users.getAge());
            _users.setCity(users.getCity());
            _users.setCountry(users.getCountry());
            _users.setUsername(users.getUsername());
            _users.setPassword(users.getPassword());
            _users.setPhone(users.getPhone());
            _users.setStreet(users.getStreet());
            _users.setState(users.getState());



            return new ResponseEntity<>(usersRepository.save(_users), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) {
        try {
            usersRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/users")
    public ResponseEntity<HttpStatus> deleteAllUsers() {
        try {
            usersRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}

