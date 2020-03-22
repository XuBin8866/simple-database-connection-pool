package MyDataSource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public abstract class MyAbstractDataSource implements MyDataSourceInterface {

    private String url;
    private String driver;
    private String username;
    private String password;
    /**
     * 最大连接数
     */
    private int poolMaxActiveConnectionCount=20;
    /**
     * 空闲连接数
     */
    private int poolMaxIdleConnectionCount=8;
    /**
     * 获取连接的最大等待时间 单位：ms
     */
    private int poolTimeToWait=30000;

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(username,password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return doGetConnection(username,password);
    }
    private Connection doGetConnection(String username, String password) throws SQLException{
        return DriverManager.getConnection(url,username,password);
    }



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPoolMaxActiveConnectionCount() {
        return poolMaxActiveConnectionCount;
    }

    public void setPoolMaxActiveConnectionCount(int poolMaxActiveConnectionCount) {
        this.poolMaxActiveConnectionCount = poolMaxActiveConnectionCount;
    }

    public int getPoolMaxIdleConnectionCount() {
        return poolMaxIdleConnectionCount;
    }

    public void setPoolMaxIdleConnectionCount(int poolMaxIdleConnectionCount) {
        this.poolMaxIdleConnectionCount = poolMaxIdleConnectionCount;
    }

    public int getPoolTimeToWait() {
        return poolTimeToWait;
    }

    public void setPoolTimeToWait(int poolTimeToWait) {
        this.poolTimeToWait = poolTimeToWait;
    }


    /**
     * 以下可以实现，在这一步重写后，继承其的MyDataSource就不需要在重写以下方法，让具体的数据源代码更简洁
     */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
