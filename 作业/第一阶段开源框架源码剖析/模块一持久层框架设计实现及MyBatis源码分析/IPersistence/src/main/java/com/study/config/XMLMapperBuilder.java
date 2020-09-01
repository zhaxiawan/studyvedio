package com.study.config;

import com.study.pojo.Configruation;
import com.study.pojo.MapperedStatement;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class XMLMapperBuilder {
    private Configruation configruation;

    public XMLMapperBuilder(Configruation configruation) {
        this.configruation = configruation;
    }

    public void parse(InputStream inputStream)throws  Exception{
        Document read = new SAXReader().read(inputStream);
        Element rootElement = read.getRootElement();
        Iterator iterator = rootElement.elementIterator();
        String namespace = rootElement.attributeValue("namespace");
        while (iterator.hasNext()){
            Element element = (Element)iterator.next();
            String id = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            String paramterType = element.attributeValue("parameterType");
            String sqlText = element.getTextTrim();
            MapperedStatement mappedStatement = new MapperedStatement();
            mappedStatement.setId(id);
            mappedStatement.setResultType(resultType);
            mappedStatement.setParamterType(paramterType);
            mappedStatement.setSqlText(sqlText);
            String key = namespace+"."+id;
            configruation.getMapperedStatementMap().put(key,mappedStatement);
        }
    };
}
