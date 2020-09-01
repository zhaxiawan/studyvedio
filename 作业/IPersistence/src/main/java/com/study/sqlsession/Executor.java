package com.study.sqlsession;

import com.study.pojo.Configruation;
import com.study.pojo.MapperedStatement;

import java.util.List;

public interface Executor {
    public <E> List<E> query(Configruation configuration, MapperedStatement mappedStatement, Object... params) throws Exception;
}
