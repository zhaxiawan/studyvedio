package com.study;

import com.study.dao.IUserDao;
import com.study.pojo.User;
import com.study.resource.Resources;
import com.study.sqlsession.SqlSession;
import com.study.sqlsession.SqlSessionFactory;
import com.study.sqlsession.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class IPersistenceTest {
        @Test
    public void add()throws Exception{
        InputStream inputStream = Resources.getResources("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = SqlSessionFactoryBuilder.build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        IUserDao iUserDao = sqlSession.getMapper(IUserDao.class);

        User user = new User();
        user.setId(3);
        user.setUsername("xiaoming");
        iUserDao.saveUser(user);
    }
    @Test
    public void update()throws Exception{
        InputStream inputStream = Resources.getResources("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = SqlSessionFactoryBuilder.build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        IUserDao iUserDao = sqlSession.getMapper(IUserDao.class);
        User user = new User();
        user.setId(3);


        user.setUsername("daming");
        iUserDao.updateUser(user);

    }
    @Test
    public void delete()throws Exception{
        InputStream inputStream = Resources.getResources("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = SqlSessionFactoryBuilder.build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        IUserDao iUserDao = sqlSession.getMapper(IUserDao.class);


        iUserDao.deleteUser(3);
    }
}
