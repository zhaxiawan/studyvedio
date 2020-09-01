package com.study.sqlsession;

import com.study.pojo.Configruation;

public class DefultSqlSessionFactory implements  SqlSessionFactory{
    private Configruation configruation;

    public DefultSqlSessionFactory(Configruation configruation) {
        this.configruation = configruation;
    }

    public SqlSession openSession(){
   return   new DefultSqlSession(configruation);
    };
}
