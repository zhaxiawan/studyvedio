package com.study.sqlsession;

import java.util.List;

public interface SqlSession {

    //查询所有
    public <E> List<E> selectList(String statementid, Object... params) throws Exception;

    //根据条件查询单个
    public <T> T selectOne(String statementid,Object... params) throws Exception;

    //根据条件进行用户相关操作
    public void excutorOther(String statementid,Object... params) throws Exception;



    //为Dao接口生成代理实现类
    public <T> T getMapper(Class<?> mapperClass);

}
