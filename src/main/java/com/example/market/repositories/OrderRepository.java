package com.example.market.repositories;

import com.example.market.models.Order;
import com.example.market.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByPerson(Person person);

    // Поиск по номеру заказа
    @Query(value = "select * from orders where number LIKE %?1", nativeQuery = true)
    List<Order> findByNumber(String number);
}
