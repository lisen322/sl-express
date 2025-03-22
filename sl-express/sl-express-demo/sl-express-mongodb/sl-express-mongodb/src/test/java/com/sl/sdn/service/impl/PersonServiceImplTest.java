package com.sl.sdn.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.sl.sdn.entity.Address;
import com.sl.sdn.entity.Person;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PersonServiceImplTest {

    @Resource
    private PersonServiceImpl personService;

    @Test
    void savePerson() {
        List<Person> personList = new ArrayList<>();
        for (int i = 0; i <20 ; i++) {
            Person person = Person.builder()
                    .id(new ObjectId("67dd0ba701742028a2d8b1a1"))
                    .name("张三2")
                    .age(RandomUtil.randomInt(0,100))
                    .address(new Address("北京", "北京", "100000"))
                    .location(new GeoJsonPoint(116.404, 39.915))
                    .build();
            personList.add(person);
            personService.savePerson(person);
        }

    }
    @Test
    void saveListPerson() {
        List<Person> personList = new ArrayList<>();
        for (int i = 0; i <20 ; i++) {
            Person person = Person.builder()
                    .name("李四"+i)
                    .age(RandomUtil.randomInt(0,100))
                    .address(new Address("北京", "北京", "100000"))
                    .location(new GeoJsonPoint(116.404, 39.915))
                    .build();
            personList.add(person);
        }
        personService.saveListPerson(personList);

    }

    @Test
    void update() {
        Person person = Person.builder()
                .id(new ObjectId("67dd0ba701742028a2d8b1a1"))
                .name("老王")
                .age(38).build();
        personService.update(person);
    }

    @Test
    void queryPersonListByName() {
        List<Person> personList = personService.queryPersonListByName("李四", 50);
        personList.forEach(System.out::println);
    }

    @Test
    void queryPersonPageList() {
        personService.queryPersonPageList(1,10).forEach(System.out::println);
    }

    @Test
    void deleteById() {
        personService.deleteById("67dd0ba701742028a2d8b1a1");
        System.out.println("测试中");
    }
}