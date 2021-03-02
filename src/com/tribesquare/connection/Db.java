package com.tribesquare.connection;

import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Db {

    public static Connection connection;
    private static Statement statement;
    private static boolean connected = false;
    private String table;

    public Db(String table) throws SQLException, ClassNotFoundException {
        setTable(table);
    }

    private void initialiseDatabase() throws SQLException, ClassNotFoundException {
        if (!connected) {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:caller.db");
            statement = connection.createStatement();
            connected = true;
        }

        ResultSet hasTable = checkTable();
        hasTable.next();
        if (hasTable.getRow() == 0) {
            createTable();
        }

    }

    public void autoCommit() throws SQLException {
        connection.setAutoCommit(false);
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }

    public void setTable(String table) throws SQLException, ClassNotFoundException {
        this.table = table;
        initialiseDatabase();
    }

    private void createTable() throws SQLException {
        switch(table) {
            case "users":
                createUsersTable();
                break;
            case "port_config":
                createPortConfigTable();
                break;
            case "layout_config":
                createLayoutConfigTable();
                break;
            case "system_config":
                createSystemConfigTable();
                break;
            case "layouts":
                createLayoutsTable();
                break;
            case "floors":
                createFloorsTable();
                break;
            case "assignments":
                createAssignTable();
                break;
            case "tx":
                createTxTable();
                break;
            case "tx_display":
                createTxDisplayTable();
                break;
            case "permissions":
                createPermissionsTable();
                break;

        }

    }

    private ResultSet checkTable() throws SQLException {
        ResultSet checkTable;
        checkTable = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table' and name='" + table + "' ");
        return checkTable;
    }

    private void createUsersTable() throws SQLException {
        statement.execute("CREATE table users (id INTEGER PRIMARY KEY, name text, role text, phone text, staff_no text, status text, password text)");
        var res = statement.executeUpdate("INSERT INTO users " +
                "(id, name, role, phone, staff_no, status, password) " +
                " VALUES (null, 'Admin', 'Manager', '', '0001', 'active', '123456')");
    }

    private void createPortConfigTable() throws SQLException {
        statement.execute("CREATE table port_config (id INTEGER PRIMARY KEY, port text, baud text, data_bit text, stop text, parity text, flux text)");
        statement.executeUpdate("INSERT INTO port_config (id, port, baud, data_bit, stop, parity, flux) VALUES (null, '','9600', '8', '1', '0', '0')");
    }

    private void createLayoutConfigTable() throws SQLException {
        statement.execute("CREATE table layout_config (" +
                "id INTEGER PRIMARY KEY, " +
                "customer_name text, " +
                "record_count text, " +
                "first_background text, " +
                "first_message text, " +
                "first_threshold text, " +
                "second_background text, " +
                "second_message text, " +
                "second_threshold text, " +
                "final_background text, " +
                "final_message text, " +
                "final_threshold text, " +
                "no_answer_background text, " +
                "no_answer_message text, " +
                "attended_background text, " +
                "attended_message text," +
                "active_background text," +
                "active_message text," +
                "active_threshold text, " +
                "inactive_background text," +
                "inactive_message text" +
                ")"
        );

        statement.executeUpdate("INSERT INTO layout_config (" +
                "id, customer_name, record_count, first_background, first_message, first_threshold, " +
                "second_background, second_message, second_threshold, final_background, final_message, final_threshold, no_answer_background, " +
                "no_answer_message, attended_background, attended_message, active_background, active_message, active_threshold, inactive_background, inactive_message" +
                ") " +
                " VALUES (" +
                "null, '', '25', '0xffffffff', '0x000000ff', '01:00', '0xffffffff', '0x000000ff', '01:00', '0xffffffff', " +
                "'0x000000ff', '01:00', '0xffffffff', '0x000000ff', '0xffffffff', '0x000000ff', '0xffffffff', '0x000000ff', '01:00', '0xffffffff', '0x000000ff' " +
                ")"
        );
    }

    private void createSystemConfigTable() throws SQLException {
        statement.execute("CREATE table system_config (id INTEGER PRIMARY KEY, autorun text, topside text, taskbar_icon text, pop_up text, " +
                "stop_counting text, first_warning_tone text, first_warning_repeat text, second_warning_tone text, second_warning_repeat text, final_warning_tone text, final_warning_repeat text )");
        statement.executeUpdate("INSERT INTO system_config " +
                "(id, autorun, topside, taskbar_icon, pop_up, stop_counting, first_warning_repeat, second_warning_repeat, final_warning_repeat, first_warning_tone, second_warning_tone, final_warning_tone) " +
                " VALUES (null, '0', '0', '0', '0', '0', '0', '0', '0', 'src/com/tribesquare/tones/beep.wav', 'src/com/tribesquare/tones/beep.wav', 'src/com/tribesquare/tones/beep.wav')");
    }

    private void createLayoutsTable() throws SQLException {
        statement.execute("CREATE table layouts (id INTEGER PRIMARY KEY, " +
                "table_name text NOT NULL UNIQUE, " +
                "tx_no text NOT NULL UNIQUE, " +
                "floor text NOT NULL, " +
                "FOREIGN KEY (table_name) REFERENCES floors (floor_name) ON UPDATE CASCADE ON DELETE CASCADE " +
                ") ");
    }

    private void createFloorsTable() throws SQLException {
        statement.execute("CREATE table floors (id INTEGER PRIMARY KEY, floor_name text NOT NULL UNIQUE) ");
    }

    private void createAssignTable() throws SQLException {
        statement.execute("CREATE table assignments " +
                "(id INTEGER PRIMARY KEY, user_id text NOT NULL, table_id text NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users (name) ON UPDATE RESTRICT ON DELETE RESTRICT " +
                "FOREIGN KEY (table_id) REFERENCES layouts (table_name) ON UPDATE CASCADE ON DELETE CASCADE )"
        );
    }

    private void createTxTable() throws SQLException {
        statement.execute("CREATE table tx (id INTEGER PRIMARY KEY, tx_no text, tx_type text, tx_date text, tx_time text, response_time text, tx_state text DEFAULT 'active', " +
                "FOREIGN KEY (tx_no) REFERENCES layouts (tx_no) )"
        );
    }

    private void createTxDisplayTable() throws SQLException {
        statement.execute("CREATE table tx_display (id INTEGER PRIMARY KEY, tx_no text, tx_type text, tx_date text, tx_time text, response_time text, tx_state text DEFAULT 'active', " +
                "FOREIGN KEY (tx_no) REFERENCES layouts (tx_no) )"
        );
    }

    private void createPermissionsTable() throws SQLException {
        statement.execute("CREATE table permissions (" +
                "id INTEGER PRIMARY KEY, user_id INTEGER NOT NULL UNIQUE, config_settings text, " +
                "layout_settings text, staff_settings text, analytics_settings text, " +
                "FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE )"
        );
        statement.executeUpdate("INSERT INTO permissions " +
                "(id, user_id, config_settings, layout_settings, staff_settings, analytics_settings) " +
                " VALUES (null, '1', 'true', 'true', 'true', 'true')");
    }

    public ResultSet get() throws SQLException {
        return statement.executeQuery("SELECT * FROM " + table);
    }

    public ResultSet get(String filterStatement) throws SQLException {
        return statement.executeQuery("SELECT * FROM " + table + " " + filterStatement);
    }

    public ResultSet get(String table_, String filterStatement) throws SQLException {
        return statement.executeQuery("SELECT * FROM " + table_ + " " + filterStatement );
    }

    public ResultSet getCustom(String filterStatement) throws SQLException {
        return statement.executeQuery("SELECT " + filterStatement);
    }

    public ResultSet count(String filterStatement) throws SQLException {
        return statement.executeQuery("SELECT *, COUNT(*) as count FROM " + table + " " + filterStatement);
    }

    public int insert(HashMap<String, Object> data) throws SQLException {
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        for (String i: data.keySet()) {
            key.append("'").append(i).append("'".concat(", "));
            value.append("'").append(data.get(i)).append("'".concat(", "));
        }

        String sql = "INSERT INTO " + table + "('id', " + key.toString().replaceFirst(", $", "") + ") VALUES (null, " + value.toString().replaceFirst(", $", "") + ") ";

        return statement.executeUpdate(sql);
    }

    public int insert(HashMap<String, Object> data, String table_) throws SQLException {
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        for (String i: data.keySet()) {
            key.append("'").append(i).append("'".concat(", "));
            value.append("'").append(data.get(i)).append("'".concat(", "));
        }

        String sql = "INSERT INTO " + table_ + "('id', " + key.toString().replaceFirst(", $", "") + ") VALUES (null, " + value.toString().replaceFirst(", $", "") + ") ";

        return statement.executeUpdate(sql);
    }

    public ResultSet insertReturnKey(HashMap<String, Object> data) throws SQLException {
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();

        for (String i: data.keySet()) {
            key.append("'").append(i).append("'".concat(", "));
            value.append("'").append(data.get(i)).append("'".concat(", "));
        }

        String sql = "INSERT INTO " + table + "('id', " + key.toString().replaceFirst(", $", "") + ") VALUES (null, " + value.toString().replaceFirst(", $", "") + ") ";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        statement.executeUpdate();

        return statement.getGeneratedKeys();

    }

    public int delete(ArrayList<Integer> data) throws SQLException {
        StringBuilder dataString = new StringBuilder();

        for (int i: data) {
            dataString.append(i).append("".concat(", "));
        }

        String sql = "DELETE FROM " + table + " WHERE id IN (" + dataString.toString().replaceFirst(", $", "") + ")";

        return statement.executeUpdate(sql);
    }

    public int update(HashMap<String, Object> data, int id) throws SQLException {
        StringBuilder value = new StringBuilder();
        for (String i: data.keySet()) {
            value.append(i).append("='").append(data.get(i)).append("'".concat(", "));
        }

        String sql = "UPDATE " + table + " SET " + value.toString().replaceFirst(", $", "") + " WHERE id = " + id;

        return statement.executeUpdate(sql);
    }


    public int update(HashMap<String, Object> data, String column, Object id_) throws SQLException {
        StringBuilder value = new StringBuilder();
        for (String i: data.keySet()) {
            value.append(i).append("='").append(data.get(i)).append("'".concat(", "));
        }

        String sql = "UPDATE " + table + " SET " + value.toString().replaceFirst(", $", "") + " WHERE " + column + " = '" + id_ + "' ";

        return statement.executeUpdate(sql);
    }

    public int update(HashMap<String, Object> data, String column, Object id_, String table_) throws SQLException {
        StringBuilder value = new StringBuilder();
        for (String i: data.keySet()) {
            value.append(i).append("='").append(data.get(i)).append("'".concat(", "));
        }

        String sql = "UPDATE " + table_ + " SET " + value.toString().replaceFirst(", $", "") + " WHERE " + column + " = '" + id_ + "' ";

        return statement.executeUpdate(sql);
    }

    public void close(ResultSet resultSet) {
//        DbUtils.closeQuietly(resultSet);
//        DbUtils.closeQuietly(statement);
//        DbUtils.closeQuietly(connection);
    }

}
