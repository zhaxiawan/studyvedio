package com.study.sqlsession;

import com.mysql.jdbc.StringUtils;
import com.study.pojo.Configruation;
import com.study.pojo.MapperedStatement;

import java.lang.reflect.*;
import java.util.List;

public class DefultSqlSession implements SqlSession {
    private Configruation configruation;

    public DefultSqlSession(Configruation configruation) {
        this.configruation = configruation;
    }

    @Override
    public <E> List<E> selectList(String statementid, Object... params) throws Exception {

        //将要去完成对simpleExecutor里的query方法的调用
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MapperedStatement mappedStatement = configruation.getMapperedStatementMap().get(statementid);
        List<Object> list = simpleExecutor.query(configruation, mappedStatement, params);

        return (List<E>) list;
    }

    @Override
    public <T> T selectOne(String statementid, Object... params) throws Exception {
        List<Object> objects = selectList(statementid, params);
        if(objects.size()==1){
            return (T) objects.get(0);
        }else {
            throw new RuntimeException("查询结果为空或者返回结果过多");
        }


    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        // 使用JDK动态代理来为Dao接口生成代理对象，并返回

        Object proxyInstance = Proxy.newProxyInstance(DefultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 底层都还是去执行JDBC代码 //根据不同情况，来调用selctList或者selectOne
                // 准备参数 1：statmentid :sql语句的唯一标识：namespace.id= 接口全限定名.方法名
                // 方法名：findAll
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();

                String statementId = className+"."+methodName;

                // 准备参数2：params:args
                // 获取被调用方法的返回值类型
                Type genericReturnType = method.getGenericReturnType();
                // 判断是否进行了 泛型类型参数化
                String typeName = genericReturnType.getTypeName();
                if (genericReturnType.getTypeName().equals("void")){
                    excutorOther(statementId, args);
                    return null;
                }else if(genericReturnType instanceof ParameterizedType){
                    List<Object> objects = selectList(statementId, args);
                    return objects;
                }

                return selectOne(statementId,args);

            }
        });

        return (T) proxyInstance;
    }
    public void excutorOther(String statementid,Object... params) throws Exception{
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MapperedStatement mappedStatement = configruation.getMapperedStatementMap().get(statementid);
      if (statementid.contains("save")){
          simpleExecutor.add(configruation, mappedStatement, params);
      }else if (statementid.contains("delete")){
          simpleExecutor.delete(configruation, mappedStatement, params);
      }else if (statementid.contains("update")){
          simpleExecutor.update(configruation, mappedStatement, params);
      }else {
          throw  new RuntimeException("暂不支持");
      }
    };
}
