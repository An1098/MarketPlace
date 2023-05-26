package com.example.market.services;

import com.example.market.models.Person;
import com.example.market.repositories.PersonRepository;
import com.example.market.security.PersonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PersonDetailsService  implements UserDetailsService {
    //поле хранит в себе объект PersonRepository и внедрим его через конструктор
    private final PersonRepository personRepository;

    //конструктор
    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    // переопределяем метод интерфейса, позволяет найти пользователя по username
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Получаем пользователя из таблицы по логину с формы аутентификации
        Optional<Person> person = personRepository.findByLogin(username);

        //проверяем найден ли пользователь
        if(person.isEmpty()) {
            //выбрасываем исключение
            //данное исключение будет поймано Spring Security и сообщение будет выведено на страницу
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        //если найден, то мы его помещаем в соответствующее поле класса PersonDetails
        return new PersonDetails(person.get());
    }
}
