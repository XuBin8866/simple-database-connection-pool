package c3p0pool;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;

public final class ConnectionManager {
	/**
	 * 使用单例模式创建数据库连接池
	 */
	private static ConnectionManager instance;
	private static ComboPooledDataSource dataSource;
	
	private ConnectionManager() {
		dataSource=new ComboPooledDataSource();
		//防止通过反射进行实例化而破坏单例
		if(instance!=null) {
			throw new RuntimeException("Object has been instanced!!!");
		}
	}
	public static final ConnectionManager getInstance() {
		if(instance==null) {
			synchronized (ConnectionManager.class) {
				try {
					instance=new ConnectionManager();
				}catch (Exception e) {
					//
					e.printStackTrace();
				}
			}	
		}
		return instance;
	}
	public synchronized final Connection getConnection() {
		Connection conn=null;
		try {
			conn=dataSource.getConnection();
		}catch (Exception e) {
			//
			e.printStackTrace();
		}
		return conn;
	}
	public ComboPooledDataSource getDataSource(){
		return dataSource;
	}
}
