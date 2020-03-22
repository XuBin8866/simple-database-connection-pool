package druidpool;

import java.sql.*;

public class JDBCTool {
    private static DruidPool dp=DruidPool.getInstance();

    /**
     * 获取连接对象
     * @return
     */
    public static Connection getConnection(){
        return  dp.getConnection();
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
    public static void closeConnection(Connection conn){
        if(conn!=null){
            //使用druid连接池获取的连接可以直接关闭
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
        for(int i=0;i<21;i++){
            conn=getConnection();

            System.out.println(dp.getDruidDataSource().getActiveCount());
        }
        closeConnection(conn);
        System.out.println("--------------");
        System.out.println(dp.getDruidDataSource().getActiveCount());
        System.out.println(dp.getDruidDataSource().getPoolingCount());


    }
}
