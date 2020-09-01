package com.study.resource;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

public class Resources {
    public static InputStream getResources(String path){
        InputStream inputStream = Resources.class.getClassLoader().getResourceAsStream(path);
       return  inputStream;
    }
}
