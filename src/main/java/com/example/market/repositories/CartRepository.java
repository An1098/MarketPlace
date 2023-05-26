package com.example.market.repositories;

import com.example.market.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByPersonId(int id);

    //удаление из корзины
    //принимаем id и по нему удаляем
    //запрос в БД "удалить из таблицы product_cart где product_id равен первому принимаемому параметру в рамках данного метода
    @Modifying
    @Query(value = "delete from product_cart where product_id=?1", nativeQuery = true)
    void deleteCartByProductId(int id);
}
