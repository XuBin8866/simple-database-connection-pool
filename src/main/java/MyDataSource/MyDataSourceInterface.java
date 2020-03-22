package MyDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * @author xxbb
 */
public interface MyDataSourceInterface extends DataSource {
    /**
     * 获取连接，
     * @return  返回其有参方法
     * @throws SQLException
     */
    @Override
    Connection getConnection() throws SQLException;

    /**
     * 获取连接，被其无参方法调用
     * @param username
     * @param password
     * @return  获取连接？
     * @throws SQLException
     */
    @Override
    Connection getConnection(String username, String password) throws SQLException;

}
