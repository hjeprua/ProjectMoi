package com.nrolove.jdbc;

import java.sql.Connection;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class DBService {

    public static String DRIVER = "com.mysql.jdbc.Driver";
    public static String URL = "jdbc:#0://#1:#2/#3";
    public static String DB_HOST = "localhost";
    public static int DB_PORT = 3306;
    public static String DB_NAME = "love";
    public static String DB_USER = "root";
    public static String DB_PASSWORD = "";
    public static int MAX_CONN = 2;

    private static DBService i;
    public static String dbName;

    private ConnPool connPool;

    public static DBService gI() {
        if (i == null) {
            i = new DBService();
        }
        return i;
    }

    private DBService() {
        this.connPool = ConnPool.gI();
    }

    public Connection getConnection() throws Exception {
//        return this.connPool.getConnection();
        return DBHika.getConnection();
    }

    public void release(Connection con) {
//        this.connPool.free(con);
    }

    public int currentActive() {
        return -1;
    }

    public int currentIdle() {
        return -1;
    }

}
