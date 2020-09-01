package com.study.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.study.pojo.Configruation;
import com.study.resource.Resources;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class XMLConfigBuilder {
private Configruation configruation;

    public XMLConfigBuilder() {
        this.configruation = new Configruation();
    }

    public Configruation parse(InputStream inputStream) throws Exception{
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(inputStream);

        Element rootElement = document.getRootElement();
        List<Element> list = rootElement.selectNodes("//property");
        Properties properties=new Properties();
        for (Element element : list) {
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.put(name,value);
        }
        ComboPooledDataSource dataSource=new ComboPooledDataSource();
        dataSource.setDriverClass(properties.getProperty("driverClass"));
        dataSource.setJdbcUrl(properties.getProperty("jdbcUrl"));
        dataSource.setUser(properties.getProperty("username"));
        dataSource.setPassword(properties.getProperty("password"));
        configruation.setDataSource(dataSource);
        //解析mapper
        List<Element> list2 = rootElement.selectNodes("//mapper");
        for (Element element : list2) {
            String mapperPath = element.attributeValue("resource");
            InputStream resourceAsSteam = Resources.getResources(mapperPath);
            XMLMapperBuilder xmlMapperBuilder= new XMLMapperBuilder(configruation);
            xmlMapperBuilder.parse(resourceAsSteam);
        }
        return configruation;
    }
}
