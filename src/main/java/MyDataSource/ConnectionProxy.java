package MyDataSource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * 动态代理实现对数据库连接的代理
 */
public class ConnectionProxy implements InvocationHandler {

    /**
     * 定义一些方法名常量
     */
    private static final String CLOSE="close";
    /**
     *真正的连接
     */
    private Connection realConnection;
    /**
     * 代理连接
     */
    private Connection proxyConnection;
    /**
     *抽象数据源
     */
    private MyAbstractDataSource myAbstractDataSource;
    /**
     * 数据源
     */
    private MyDataSource myDataSource;
    /**
     * 通过构造方法
     * @param realConnection 真实连接
     * @param myDataSource 数据库连接池
     */
    public ConnectionProxy(Connection realConnection, MyDataSource myDataSource) {
        //初始化真实代理和数据源
        this.realConnection = realConnection;
        this.myDataSource=myDataSource;
        //初始化代理连接
        this.proxyConnection=(Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),new Class<?>[]{Connection.class},this);
    }

    /**
     * 当嗲用Connection对象里面的方法时，首先会被该invoke方法拦截
     * @param proxy 代理
     * @param method 方法
     * @param args 参数
     * @return 增强的代理对象
     * @throws Throwable throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //获取当前连接Connection所调用的方法
        String methodName=method.getName();
        if(CLOSE.equals(methodName)){
            //归还连接到连接池中
            myDataSource.returnConnection(this);
            return null;
        }

        return method.invoke(method,args);
    }



    public Connection getRealConnection() {
        return realConnection;
    }

    public void setRealConnection(Connection realConnection) {
        this.realConnection = realConnection;
    }

    public Connection getProxyConnection() {
        return proxyConnection;
    }

    public void setProxyConnection(Connection proxyConnection) {
        this.proxyConnection = proxyConnection;
    }

    public MyAbstractDataSource getMyAbstractDataSource() {
        return myAbstractDataSource;
    }

    public void setMyAbstractDataSource(MyAbstractDataSource myAbstractDataSource) {
        this.myAbstractDataSource = myAbstractDataSource;
    }

    public MyDataSource getMyDataSource() {
        return myDataSource;
    }

    public void setMyDataSource(MyDataSource myDataSource) {
        this.myDataSource = myDataSource;
    }
}
