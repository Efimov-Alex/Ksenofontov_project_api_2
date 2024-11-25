package ru.efimov;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.efimov.model.Users;
import ru.efimov.repository.UsersRepository;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UsersRepository usersRepository;

    @PostConstruct
    public void init() throws IOException {

        if(!usersRepository.findAll().isEmpty()) {
            log.info("Users already created");
            return;
        }

        ArrayList<Users> users = new ArrayList<>();

        for (int i=0; i < 100;i++){
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

            int idPhone = 0;
            for (String m : spis){
                if (m.equals("phone")){
                    break;
                }
                idPhone++;
            }


            users.add(new Users(spis[5], spis[15], spis[19], spis[26].substring(1) + spis[29],
            spis[33], spis[37], spis[41], spis[idUsername+2], spis[idPassword+2], spis[idPhone+2],
                    Integer.parseInt(spis[idAge+1].substring(1, spis[idAge+1].length()-2))));
        }



        log.info("Users created: {}",users);

        List<Users> usersEntities = usersRepository.saveAll(users);

        log.info("Users saved: {}",users);
    }
}



