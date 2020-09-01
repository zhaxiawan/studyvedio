package com.study.pojo;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class Configruation {
    private DataSource dataSource;
    Map<String,MapperedStatement> mapperedStatementMap=new HashMap<>();

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, MapperedStatement> getMapperedStatementMap() {
        return mapperedStatementMap;
    }

    public void setMapperedStatementMap(Map<String, MapperedStatement> mapperedStatementMap) {
        this.mapperedStatementMap = mapperedStatementMap;
    }
}
