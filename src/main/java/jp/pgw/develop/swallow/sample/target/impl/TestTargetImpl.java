package jp.pgw.develop.swallow.sample.target.impl;

import java.util.List;

import org.seasar.framework.container.annotation.tiger.AutoBindingType;
import org.seasar.framework.container.annotation.tiger.Component;
import org.seasar.framework.container.annotation.tiger.InstanceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.pgw.develop.swallow.sample.entity.Person;
import jp.pgw.develop.swallow.sample.service.PersonService;
import jp.pgw.develop.swallow.sample.target.TestTarget;

@Component(autoBinding = AutoBindingType.CONSTRUCTOR, instance = InstanceType.SINGLETON)
public class TestTargetImpl implements TestTarget {

    protected static final Logger log = LoggerFactory.getLogger(TestTargetImpl.class);

    protected final PersonService personService;

    public TestTargetImpl(final PersonService personService) {
        this.personService = personService;
    }

    @Override
    public void exec() {
        final Person person = new Person();
        person.setFirstName("ほげ");
        person.setLastName("もげ");
        person.setAge(20);
        personService.insert(person);
        final List<Person> persons = personService.findAll();
        for (final Person p : persons) {
            log.info(p.toString());
        }
        log.info("delete count: {}.", personService.deleteAll(persons));
    }

}
