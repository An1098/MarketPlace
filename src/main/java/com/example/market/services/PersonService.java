package com.example.market.services;

import com.example.market.models.Person;
import com.example.market.repositories.PersonRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    //внедряем репозиторий и PasswordEncoder (позволит хэшировать пароли) через конструктор
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public PersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //метод получает пользователя по логину
    //принимем объект (Person person), для того чтобы найти его по логину, в нашем репозитории есть метод findByLogin который ищет объект по логину и возвращает объект типа Optional<Person>
    // в нашем сервисе мы так же создадим объект типа Optional<Person>, обратимся к репозиторию, вызовем метод .findByLogin и на объекте person, который придет к нам с формы регистрации мы вызовем метод getLogin
    //возвращаем объект person_db если он есть, если его нет возвращаем null
    public Person findByLogin(Person person){
        Optional<Person> person_db = personRepository.findByLogin(person.getLogin());
        return person_db.orElse(null);
    }

    // метод позволит зарегистрировать пользователя (сохранить в БД)
    // в параметрах принимаем объект того пользователя, которого хотим регистрировать,
    @Transactional //потому что идет изменение состояния базы данных
    public  void register(Person person){
        person.setPassword(passwordEncoder.encode(person.getPassword()));//мы пользователю, который должен будет сохранен в БД, устанавливаем хэш, обращаемся к passwordEncoder и вызываем метод encode - он позволит из обычного пароля с помощью функции bcrypt преобразовать пароль в хэш, далее обращаемся к пользователю(person) и вызываем метод getPassword, таким образом у пользователя вместо обычного пароля установиться хэш
        person.setRole("ROLE_USER");// указываем что все пользователи по умолчанию будут иметь роль user
        personRepository.save(person);//обращаемя к personRepository и вызываем метод save, в него передаём объект person
    }



    //метод позволит получить список всех пользователей
    public List<Person> getAllPerson(){
        return personRepository.findAll();
    }

    //метод позволит получить пользователя по id
    public Person getPersonId(int id){
        Optional<Person> optionalPerson = personRepository.findById(id);
        return optionalPerson.orElse(null);
    }
    // Данный метод позволяет обновить данные пользователя
    @Transactional
    public void updatePerson(int id, Person person){
        person.setId(id);
        personRepository.save(person);
    }

    //метод позволит удалить пользователя по id
    @Transactional//так как меняет БД (уже не только чтение, а изменение БД)
    public void deletePerson(int id){
        personRepository.deleteById(id);
    }
}
