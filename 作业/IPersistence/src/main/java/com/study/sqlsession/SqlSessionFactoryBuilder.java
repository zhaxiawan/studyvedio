package com.study.sqlsession;

import com.study.config.XMLConfigBuilder;
import com.study.pojo.Configruation;

import java.io.InputStream;

public class SqlSessionFactoryBuilder {
    public static SqlSessionFactory build(InputStream inputStream) throws  Exception{
       //解析xml
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();
        Configruation configruation = xmlConfigBuilder.parse(inputStream);

        //生成SqlSessionFactory
        return new DefultSqlSessionFactory(configruation);
    }
}
