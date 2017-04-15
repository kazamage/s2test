package jp.pgw.develop.swallow.sample.service.impl;

import java.util.List;

import org.seasar.framework.container.annotation.tiger.AutoBindingType;
import org.seasar.framework.container.annotation.tiger.Component;
import org.seasar.framework.container.annotation.tiger.InstanceType;

import jp.pgw.develop.swallow.sample.dao.PersonDao;
import jp.pgw.develop.swallow.sample.entity.Person;
import jp.pgw.develop.swallow.sample.service.PersonService;

@Component(autoBinding = AutoBindingType.CONSTRUCTOR, instance = InstanceType.SINGLETON)
public class PersonServiceImpl implements PersonService {

    protected final PersonDao personDao;

    public PersonServiceImpl(final PersonDao personDao) {
        this.personDao = personDao;
    }

    @Override
    public Person findOne(final Long id) {
        return personDao.findOne(id);
    }

    @Override
    public List<Person> findAll() {
        return personDao.findAll();
    }

    @Override
    public int insert(final Person person) {
        return personDao.insert(person);
    }

    @Override
    public int update(final Person person) {
        return personDao.update(person);
    }

    @Override
    public int delete(final Person person) {
        return personDao.delete(person);
    }

    @Override
    public int deleteAll(final List<Person> persons) {
        return personDao.deleteAll(persons);
    }

}
