package connectionpool1;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;

public class ConnectionPool {

    /**
     * 数据库连接属性
     */
    private static String driverClass;
    private static String url;
    private static String username;
    private static String password;
    /**
     * 初始连接数量
     */
    private static int initCount=5;
    /**
     * 最小连接数量
     */
    private static int minCount=5;
    /**
     * 最大连接数量
     */
    private static int maxCount=20;
    /**
     * 已创建的连接数量
     */
    private static int createdCount;
    /**
     * 连接数增长步长
     */
    private static int increasingCount=2;
    /**
     * 用于获取连接和归还连接的同步锁对象
     */
    private static final Object MONITOR = new Object();
    /**
     * 存储连接的集合
     */
    LinkedList<Connection> conns=new LinkedList<>();
    /*
      属性初始化
     */
    static{
        Properties properties=new Properties();
        InputStream in=ConnectionPool.class.getClassLoader().getResourceAsStream("db.properties");
        try {
            properties.load(in);
            driverClass=properties.getProperty("jdbc.driverClass");
            url=properties.getProperty("jdbc.url");
            username=properties.getProperty("jdbc.username");
            password=properties.getProperty("jdbc.password");
            //以下属性如果在properties文件中没有设置则使用默认值
            try{
                initCount=Integer.parseInt(properties.getProperty("jdbc.initCount"));
            }catch (Exception e){
                System.out.println("initCount使用默认值："+initCount);
            }
            try {
                minCount=Integer.parseInt(properties.getProperty("jdbc.minCount"));
            }catch (Exception e){
                System.out.println("minCount使用默认值："+minCount);
            }
            try {
                maxCount=Integer.parseInt(properties.getProperty("jdbc.maxCount"));
            }catch (Exception e){
                System.out.println("maxCount使用默认值："+maxCount);
            }
            try {
                increasingCount=Integer.parseInt(properties.getProperty("jdbc.increasingCount"));
            }catch (Exception e){
                System.out.println("increasingCount使用默认值："+increasingCount);
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 连接池对象
     */
    private static volatile ConnectionPool instance;
    private ConnectionPool(){

        //防止反射破坏单例
        if(instance!=null){
            throw new RuntimeException("Object has been instanced!!!");
        }
        //初始化连接池
        init();
    }
    public static ConnectionPool getInstance(){
        //双重检测锁
        if(null==instance){
            synchronized (ConnectionPool.class){
                if(null==instance){
                    instance=new ConnectionPool();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化连接池
     */
    private void init(){
        //循环给集合中添加初始化连接
        for(int i=0;i<initCount;i++){
            boolean flag=conns.add(createConnection());
            if(flag){
                createdCount++;
            }
        }
        System.out.println("ConnectionPool1初始化------>连接池对象："+this);
        System.out.println("ConnectionPool1初始化------>连接池可用连接数量："+createdCount);
    }

    /**
     * 构建数据库连接对象
     * @return 连接
     */
    private Connection createConnection(){
        try{
            Class.forName(driverClass);
            return DriverManager.getConnection(url,username,password);

        }catch (Exception e){
            throw new RuntimeException("数据库连接创建失败："+e.getMessage());
        }
    }

    /**
     * 连接自动增长
     */
    private synchronized void autoAdd(){
        //增长步长默认为2
        if(createdCount==maxCount){
            throw new RuntimeException("连接池中连接已达最大数量,无法再次创建连接");
        }
        //临界时判断增长个数
        for(int i=0;i<increasingCount;i++){
            if(createdCount==maxCount){
                break;
            }
            conns.add(createConnection());
            createdCount++;
        }

    }
    /**
     * 自动减少连接
     */
    private synchronized void autoReduce(){
        if(createdCount>minCount&&conns.size()>0){
            //关闭池中空闲连接
            try {
                conns.removeFirst().close();
                createdCount--;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取池中连接
     * @return 连接
     */
    public Connection getConnection() {
        //判断池中是否还有连接
        synchronized (MONITOR){
            if (conns.size() > 0) {
                System.out.println(Thread.currentThread().getName()+"--->获取到连接："+conns.getFirst()+"  已创建连接数量："+createdCount+"  空闲连接数"+(conns.size()-1));
                return conns.removeFirst();
            }
            //如果没有空连接，则调用自动增长方法
            if (createdCount < maxCount) {
                autoAdd();
                return getConnection();
            }
            //如果连接池连接数量达到上限,则等待连接归还
            System.out.println(Thread.currentThread().getName() + "--->连接池中连接已用尽，请等待连接归还");
            try {
                MONITOR.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getConnection();
        }

    }



    /**
     * 归还连接
     * @param conn 连接
     */

    public void returnConnection(Connection conn) {

        synchronized (MONITOR) {
            System.out.println(Thread.currentThread().getName() + "--->准备归还数据库连接" + conn);
            conns.add(conn);
            MONITOR.notify();
            autoReduce();
        }
    }
    /**
     * 返回可用连接数量
     * @return 可用连接数量
     */
    public int getCreatedCount(){
        return createdCount;
    }

    public static void main(String[] args) {
        ConnectionPool.getInstance().init();
    }

}

