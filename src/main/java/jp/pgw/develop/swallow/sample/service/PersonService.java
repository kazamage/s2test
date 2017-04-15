package jp.pgw.develop.swallow.sample.service;

import java.util.List;

import jp.pgw.develop.swallow.sample.entity.Person;

public interface PersonService {

    Person findOne(Long id);

    List<Person> findAll();

    int insert(Person person);

    int update(Person person);

    int delete(Person person);

    int deleteAll(List<Person> persons);

}
