package com.sl.sdn.service.impl;

import cn.hutool.core.collection.ListUtil;
import com.sl.sdn.entity.Person;
import com.sl.sdn.service.PersonService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
/**
 * @author Administrator
 */
@Service
public class PersonServiceImpl implements PersonService {
    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 保存或全量覆盖
     * @param person
     */
    @Override
    public void savePerson(Person person) {
        // 保存或全量覆盖
        mongoTemplate.save(person);
    }

    /**
     * 批量保存
     * @param person
     */
    @Override
    public void saveListPerson(List<Person> person) {
        mongoTemplate.insertAll(person);
    }

    /**
     * 更新
     * @param person
     */
    @Override
    public void update(Person person) {
        //构建查询条件
        Query query = Query.query(Criteria.where("id").is(person.getId()));

        //构建指定修改对象数据
        Update update = new Update();
        update.set("age", person.getAge());
        update.set("name", person.getName());
        //更新 1.条件  2.修改对象 3.修改哪个类
        mongoTemplate.updateFirst(query, update, Person.class);
    }

    /**
     * 根据模糊姓名和年龄查询
     * @param name
     * @param age
     * @return
     */
    @Override
    public List<Person> queryPersonListByName(String name,Integer age) {
//        Query query = Query.query(Criteria.where("name").is(name).and("age").gt(age));
        Query query = Query.query(Criteria.where("name").regex(".*"+name+".*").and("age").gt(age));

        List<Person> personList = mongoTemplate.find(query, Person.class);
        if (!personList.isEmpty()) {
            return personList;
        }
        return ListUtil.empty();
    }

    /**
     * 分页查询
     * @param page     页数
     * @param pageSize 页面大小
     * @return
     */

    @Override
    public List<Person> queryPersonPageList(int page, int pageSize) {
        Query query = new Query();
//        query.limit().skip();
        query.with(PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.ASC,"age")));
        return mongoTemplate.find(query, Person.class);
//        return List.of();
    }
    /**
     * 根据id删除
     * @param id
     */
    @Override
    public void deleteById(String id) {
        mongoTemplate.remove(Query.query(Criteria.where("id").is(id)) ,Person.class);
    }
}
