package jp.pgw.develop.swallow.sample.dao.impl;

import java.util.List;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.container.annotation.tiger.AutoBindingType;
import org.seasar.framework.container.annotation.tiger.Component;
import org.seasar.framework.container.annotation.tiger.InstanceType;

import jp.pgw.develop.swallow.sample.dao.PersonDao;
import jp.pgw.develop.swallow.sample.entity.Person;

@Component(autoBinding = AutoBindingType.CONSTRUCTOR, instance = InstanceType.SINGLETON)
public class PersonDaoImpl implements PersonDao {

    protected final JdbcManager jdbcManager;

    public PersonDaoImpl(final JdbcManager jdbcManager) {
        this.jdbcManager = jdbcManager;
    }

    @Override
    public Person findOne(final Long id) {
        return jdbcManager.from(Person.class).id(id).getSingleResult();
    }

    @Override
    public List<Person> findAll() {
        return jdbcManager.from(Person.class).getResultList();
    }

    @Override
    public int insert(final Person person) {
        return jdbcManager.insert(person).execute();
    }

    @Override
    public int update(final Person person) {
        return jdbcManager.update(person).execute();
    }

    @Override
    public int delete(final Person person) {
        return jdbcManager.delete(person).execute();
    }

    @Override
    public int deleteAll(final List<Person> persons) {
        int total = 0;
        for (final int count : jdbcManager.deleteBatch(persons).execute()) {
            total += count;
        }
        return total;
    }

}
