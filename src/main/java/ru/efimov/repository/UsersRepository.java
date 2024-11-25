package ru.efimov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.efimov.model.Users;

import java.util.List;

public interface UsersRepository  extends JpaRepository<Users, Long> {
    @Query("SELECT AVG(u.age) FROM Users AS u WHERE u.name = :name")
    Integer averageAgeFilterByName(String name);

    @Query("SELECT u FROM Users AS u WHERE u.country = :country ORDER BY u.name")
    List<Users> findByCountryOrderByName(String country);

    @Query("SELECT u FROM Users AS u WHERE u.country = :country and u.age > 18 and u.gender = :gender")
    List<Users> findByCountryAndAge(String country, String gender);

    @Query("SELECT u FROM Users AS u WHERE u.street LIKE '%' || :street || '%'")
    List<Users> findByStreet(String street);

    /*
     List<Ages> findByNameContaining(String name);

    List<Ages> findByAgeGreaterThan(Integer age);


    List<Ages> findByOrderByAge();

    List<Ages> findByNameStartingWith(String name);
     */

}

