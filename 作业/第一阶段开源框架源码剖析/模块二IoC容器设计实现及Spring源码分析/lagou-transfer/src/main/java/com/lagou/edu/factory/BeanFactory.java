package com.lagou.edu.factory;

import com.lagou.edu.annotation.Autowired;
import com.lagou.edu.annotation.Repository;
import com.lagou.edu.annotation.Service;
import com.lagou.edu.annotation.Transactional;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @author 应癫
 *
 * 工厂类，生产对象（使用反射技术）
 */
public class BeanFactory {
    /**
     * 任务一：读取解析xml，通过反射技术实例化对象并且存储待用（map集合）
     * 任务二：对外提供获取实例对象的接口（根据id获取）
     */
    private static Map<String,Object> map = new HashMap<>();  // 存储对象
    private static Set<Class<?>> classes = new HashSet<>();   //存储扫描包下的所有字节码文件
    public static Set<String> methodSet = new HashSet<>();  //存储方法
    /**
     *     1.原本解析xml的数据来源改为注解获取
     *     2.对于通过注解开启了事务的类或接口或方法，通过是否是接口进行判断，从ProxyFactory获取其对应的代理类型的对应代理对象，
     *       并重置BeanFactory中map中的对象，具体执行的时候可以加上一下判断
     *          （！）BeanFactory添加一个methodMap
     *           (!!)代理方法执行的时候添加判断（判断改方法是否是需要添加事务注解，若是，则增强，若不是，则执行原方法）
     *     3.注入依赖也通过类型判断来获取注入的对象值，因为目前注入的对象都是BeanFactory里的单例对象
     *
     *
     */


    static {
        // 任务一：读取解析xml，通过反射技术实例化对象并且存储待用（map集合）
        // 加载xml
        InputStream resourceAsStream = BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");
        // 解析xml
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            List<Element> beanList = rootElement.selectNodes("//component-scan");
            for (int i = 0; i < beanList.size(); i++) {
                Element element =  beanList.get(i);
                // 处理每个bean元素，获取到该元素的id 和 class 属性
                String base = element.attributeValue("base-package");
                //获取包下所有class
                GetAllClass(base);


            }
            //遍历calss,进行属性传值
            for (String key:map.keySet() ) {
                Object parent = map.get(key);
                Class<?> parentClass = parent.getClass();
                doAddFieldsClass(parentClass,parent);
                map.put(key,parent);
            }
            //进行代理设置
            for (String key:map.keySet() ) {
                Object parent = map.get(key);
                Class<?> parentClass = parent.getClass();
                parent= doAddProxyClass(parentClass,parent);
                map.put(key,parent);
            }

        } catch (DocumentException e) {
            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    // 任务二：对外提供获取实例对象的接口（根据id获取）
    public static  Object getBean(String id) {
        return map.get(id);
    }
    public static void GetAllClass(String packageName)throws Exception{
        Enumeration<URL> dirs = null;

        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageName.replace(".", "/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (dirs.hasMoreElements()){
            //获取物理路径
            String filePath = dirs.nextElement().getFile();
            addPathToClasses(filePath,packageName);
        }
    }

    public static void addPathToClasses(String classPath, String rootPackageName) throws Exception{
        File file = new File(classPath);
        if (!file.exists() && !file.isDirectory())
            return;

        if (file.isDirectory()) {
            File[] list = file.listFiles();
            //如果是文件夹就需要在包名后面添加文件名
            for (File path :
                    list) {
                if (path.isDirectory())
                    addPathToClasses(path.getAbsolutePath(), rootPackageName+"."+path.getName());
                else
                    addPathToClasses(path.getAbsolutePath(), rootPackageName);
            }
        } else {
            if (file.getName().endsWith(".class")){
                String className = file.getName().substring(0,
                        file.getName().length() - 6);
                try {
                    String classPath2=rootPackageName + '.'+ className;

                    Class<?> aClass = Class.forName(classPath2);
                    doAddClass(aClass);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }
    }
    public static Object doAddProxyClass(Class<?> aClass, Object parent) throws Exception {
         boolean isTransactional=false;
         boolean isInterface=false;
        if (aClass.isAnnotationPresent(Transactional.class)){
            isTransactional=true;
        }
        if (!isTransactional){
            if (aClass.isInterface()){
                String packageName = aClass.getPackageName();
                List<Class> classes = getClasses(packageName);
                for (int j = 0; j < classes.size(); j++) {
                    String packageName1 = classes.get(j).getPackageName();
                    if (!packageName.equals(packageName1)){
                        aClass=classes.get(j);
                    }
                }

            }else {
                Class<?>[] interfaces = aClass.getInterfaces();
                if (aClass.isAnnotationPresent(Transactional.class)){
                    isTransactional=true;
                    isInterface=true;
                }
            }
            Method[] methods = aClass.getMethods();
            for(int i = 0; i < methods.length; i++){
                if (methods[i].isAnnotationPresent(Transactional.class)){
                    isTransactional=true;

                }
            }

        }
        if (isTransactional){
            ProxyFactory proxyFactory = (ProxyFactory)map.get("proxyFactory");
            if (isInterface){
                parent=proxyFactory.getCglibProxy(parent);
            }else {
                parent=proxyFactory.getJdkProxy(parent);
            }
        }

        return parent;
    }
    public static void doAddFieldsClass(Class<?> aClass, Object parent) throws Exception{

        Field[] fields = aClass.getDeclaredFields();
        if (fields==null){
            return;
        }
        for (int i = 0; i <fields.length ; i++) {
            Field field = fields[i];
            if(field.isAnnotationPresent(Autowired.class)){
                field.setAccessible(true);
                Class<?> type = field.getType();
                String id2="";
                String name = type.getSimpleName();
                if (type.isAnnotationPresent(Service.class)){

                    Service annotation = type.getAnnotation(Service.class);
                    id2= annotation.id();
                }else if (type.isAnnotationPresent(Repository.class)){

                    Repository annotation = type.getAnnotation(Repository.class);
                    id2 = annotation.id();
                }
                if (type.isInterface()){
                    String packageName = type.getPackageName();
                    List<Class> classes = getClasses(packageName);
                    for (int j = 0; j < classes.size(); j++) {
                        String packageName1 = classes.get(j).getPackageName();
                        if (!packageName.equals(packageName1)){
                            type=classes.get(j);
                        }
                    }

                }
                if (type.isAnnotationPresent(Service.class)){

                    Service annotation = type.getAnnotation(Service.class);
                    id2= annotation.id();
                }else if (type.isAnnotationPresent(Repository.class)){

                    Repository annotation = type.getAnnotation(Repository.class);
                    id2 = annotation.id();
                }
               if (!"".equals(id2)){
                   Method[] methods = parent.getClass().getMethods();
                   for (int j = 0; j < methods.length; j++) {
                       Method method = methods[j];
                       if(method.getName().equalsIgnoreCase("set" + name)) {  // 该方法就是 setAccountDao(AccountDao accountDao)
                           method.invoke(parent,map.get(id2));
                       }
                   }
               }
            }

        }

    }

    public static void doAddClass(Class<?> aClass) throws Exception{
        String classPath=aClass.getName();
        boolean isTransactional=false;
        boolean isNeed=false;
        String id="";
        if (aClass.isAnnotationPresent(Service.class)){
            isNeed=true;
            Service annotation = aClass.getAnnotation(Service.class);
            id= annotation.id();

            if (aClass.isAnnotationPresent(Transactional.class)){
                isTransactional=true;
            }
        }else if (aClass.isAnnotationPresent(Repository.class)){
            isNeed=true;
            Repository annotation = aClass.getAnnotation(Repository.class);
            id = annotation.id();
            if (aClass.isAnnotationPresent(Transactional.class)){
                isTransactional=true;
            }

        }
        if (isNeed){
            if (aClass.isInterface()){
                String packageName = aClass.getPackageName();
                List<Class> classes = getClasses(packageName);
                for (int j = 0; j < classes.size(); j++) {
                    String packageName1 = classes.get(j).getPackageName();
                    if (!packageName.equals(packageName1)){
                        aClass=classes.get(j);
                    }
                }

            }
            Object o=aClass.newInstance();
            if (isTransactional) {
                Method[] methods = aClass.getMethods();
                for(int i = 0; i < methods.length; i++){
                    methodSet.add(classPath+methods[i].getName());
                }

            }else {
                Method[] methods = aClass.getMethods();
                for(int i = 0; i < methods.length; i++){
                    if (methods[i].isAnnotationPresent(Transactional.class)){
                        methodSet.add(classPath+"."+methods[i].getName());

                    }
                }
            }

            map.put(id,o);
        }
    }
    /**
     *
     * @Description: 根据包名获得该包以及子包下的所有类不查找jar包中的
     * @param packageName 包名
     * @return List<Class>    包下所有类
     */
    private static List<Class> getClasses(String packageName) throws ClassNotFoundException,IOException{
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace(".", "/");
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while(resources.hasMoreElements()){
            URL resource = resources.nextElement();
            String newPath = resource.getFile().replace("%20", " ");
            dirs.add(new File(newPath));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for(File directory:dirs){
            classes.addAll(findClass(directory, packageName));
        }
        return classes;
    }

    private static  List<Class> findClass(File directory, String packageName)
            throws ClassNotFoundException{
        List<Class> classes = new ArrayList<Class>();
        if(!directory.exists()){
            return classes;
        }
        File[] files = directory.listFiles();
        for(File file:files){
            if(file.isDirectory()){
                assert !file.getName().contains(".");
                classes.addAll(findClass(file, packageName+"."+file.getName()));
            }else if(file.getName().endsWith(".class")){
                classes.add(Class.forName(packageName+"."+file.getName().substring(0,file.getName().length()-6)));
            }
        }
        return classes;
    }

}
