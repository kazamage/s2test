package jp.pgw.develop.swallow.sample.dao;

import java.util.List;

import jp.pgw.develop.swallow.sample.entity.Person;

public interface PersonDao {

    Person findOne(Long id);

    List<Person> findAll();

    int insert(Person person);

    int update(Person person);

    int delete(Person person);

    int deleteAll(List<Person> persons);

}
