package com.example.market.repositories;

import com.example.market.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByLogin(String login);

    @Query(value = "select * from person where role LIKE %?1", nativeQuery = true)
    List<Person> findByRole(String role);
}
