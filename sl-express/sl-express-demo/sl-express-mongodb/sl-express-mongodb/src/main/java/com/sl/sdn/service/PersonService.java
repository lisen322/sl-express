package com.sl.sdn.service;


import com.sl.sdn.entity.Person;

import java.util.List;

public interface PersonService {

    /**
     * 新增数据
     *
     * @param person 数据
     */
    void savePerson(Person person);
    /**
     * 批量保存
     *
     * @param person 数据
     */
    void saveListPerson(List<Person> person);


    /**
     * 更新数据
     *
     * @param person 数据
     */
    void update(Person person);

    /**
     * 根据名字查询用户列表
     *
     * @param name 用户名字
     * @return 用户列表
     */
    List<Person> queryPersonListByName(String name,Integer age);

    /**
     * 分页查询用户列表
     *
     * @param page     页数
     * @param pageSize 页面大小
     * @return 用户列表
     */
    List<Person> queryPersonPageList(int page, int pageSize);

    /**
     * 根据id删除用户
     *
     * @param id 主键
     */
    void deleteById(String id);
}
