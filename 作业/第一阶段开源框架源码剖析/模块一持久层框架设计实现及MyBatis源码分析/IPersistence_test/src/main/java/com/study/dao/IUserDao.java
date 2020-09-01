package com.study.dao;

import com.study.pojo.User;

import java.util.List;

public interface IUserDao {

    //查询所有用户
    public List<User> findAll() throws Exception;


    //根据条件进行用户查询
    public User findByCondition(User user) throws Exception;

    //根据条件进行用户添加
    public void saveUser(User user) throws Exception;

    //根据条件进行用户修改
    public void updateUser(User user) throws Exception;

    //根据条件进行用户删除
    public void deleteUser(Integer id) throws Exception;
}
