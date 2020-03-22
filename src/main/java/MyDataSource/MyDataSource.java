package MyDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据源连接池
 * @author xxbb
 */
public class MyDataSource extends MyAbstractDataSource {
    /**
     * 空闲连接池
     */
    private final List<ConnectionProxy> idleConnections=new ArrayList<>();
    /**
     * 激活连接池
     */
    private final List<ConnectionProxy> activeConnections=new ArrayList<>();

    /**
     * 监视器对象，用来完成同步操作
     */
    private final Object monitor=new Object();

    /**
     * 覆盖父类的方法
     * @return 代理连接对象
     * @throws SQLException
     */
    @Override
    public Connection getConnection() throws SQLException {
        ConnectionProxy connectionProxy=getConnectionProxy(super.getUsername(),super.getPassword());

        return connectionProxy.getProxyConnection();
    }

    /**
     *  获取连接代理
     * @param username
     * @param password
     * @return
     */
    private ConnectionProxy getConnectionProxy(String username,String password){
        ConnectionProxy connectionProxy=null;
        boolean wait=false;

        while(connectionProxy==null){
            synchronized (monitor){
                //如果有空闲连接则直接获取
                if(!idleConnections.isEmpty()){
                    connectionProxy=idleConnections.remove(0);
                }else {
                    //没有空闲连接则创建一个新的连接
                    if(activeConnections.size()<super.getPoolMaxActiveConnectionCount()){
                        try {
                            connectionProxy=new ConnectionProxy(super.getConnection(),this);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    //否则不能创建新的连接，需要等待，poolTimeToWait ms
                }
            }
            if(!wait){
                wait=true;
            }

            if(connectionProxy==null){
                try {
                    monitor.wait(super.getPoolTimeToWait());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //如果等待被线程打断就退出
                    break;
                }
            }
        }
        if(connectionProxy!=null){
            //获取到了线程代理
            activeConnections.add(connectionProxy);
        }
        //返回连接对象
        return connectionProxy;


    }

    /**
     * 归还连接
     * @param connectionProxy
     */
    public synchronized void returnConnection(ConnectionProxy connectionProxy){
        //关闭连接 把激活状态的连接变成空闲状态的连接
        activeConnections.remove(connectionProxy);
        if(idleConnections.size()<super.getPoolMaxIdleConnectionCount()){
            idleConnections.add(connectionProxy);
        }
        //通知正在等待获取连接的线程
        notifyAll();
    }

}
