package connectionpool1;

import java.sql.*;

public class JDBCTool {
    private  static ConnectionPool connectionPool=ConnectionPool.getInstance();
    //获取连接
    public static Connection getConnection(){
        return connectionPool.getConnection();

    }
    //关闭连接
    public static void closeConnection(Connection conn){
        if(conn!=null){
            ConnectionPool.getInstance().returnConnection(conn);
        }
    }
    public static void closeAll(Connection conn, Statement stat, ResultSet rs){
        closeResultSet(rs);
        closeStatement(stat);
        closeConnection(conn);
    }
    public static void closeAll(Connection conn, PreparedStatement ps, ResultSet rs){
        closeResultSet(rs);
        closePreparedStatement(ps);
        closeConnection(conn);
    }

    public static void closeStatement(Statement stat){
        if(stat!=null){
            try {
                stat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static void closePreparedStatement(PreparedStatement ps){
        if(ps!=null){
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static void closeResultSet(ResultSet rs){
        if(rs!=null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Connection conn=null;
        int count=0;
        for(int i=0;i<19;i++){
            conn=getConnection();
            count=ConnectionPool.getInstance().getCreatedCount();
            System.out.println("已创建连接数量："+count);
        }

        closeConnection(conn);
        System.out.println("已创建连接数量："+ConnectionPool.getInstance().getCreatedCount());


    }
}
