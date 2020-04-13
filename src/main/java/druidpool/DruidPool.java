package druidpool;

import com.alibaba.druid.pool.DruidDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author xxbb
 */
public class DruidPool {

    /**
     * 双重检测锁构建单例连接池
     */
    private static volatile DruidPool druidPool;
    public static DruidDataSource ds;

    private DruidPool() {
        //防止反射破坏单例
        if (druidPool != null) {
            throw new RuntimeException("Object has been instanced!!!");
        }
        //初始化连接池
        ds = new DruidDataSource();
        init();


    }

    public static DruidPool getInstance() {
        if (druidPool == null) {
            synchronized (DruidPool.class) {
                if (druidPool == null) {
                    druidPool = new DruidPool();
                }
            }
        }
        return druidPool;
    }

    /**
     * 初始化连接参数
     */
    private void init() {
        Properties pro = new Properties();
        InputStream is = DruidPool.class.getClassLoader().getResourceAsStream("druid.properties");
        try {
            pro.load(is);
            ds = new DruidDataSource();
            //可以不设置driverClass，他会自动识别
            ds.setUrl(pro.getProperty("jdbcUrl"));
            ds.setUsername(pro.getProperty("username"));
            ds.setPassword(pro.getProperty("password"));
            ds.setInitialSize(Integer.parseInt(pro.getProperty("initialSize")));
            ds.setMaxActive(Integer.parseInt(pro.getProperty("maxActive")));
            ds.setMinIdle(Integer.parseInt(pro.getProperty("minIdle")));
            ds.setMaxWait(Integer.parseInt(pro.getProperty("maxWait")));

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("创建连接池失败：" + e.getMessage());
        }
    }

    public Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取连接失败：" + e.getMessage());
        }
    }
    public DruidDataSource getDruidDataSource(){
        return ds;
    }

}
