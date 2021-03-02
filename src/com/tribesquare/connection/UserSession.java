package com.tribesquare.connection;

import java.sql.SQLException;

public final class UserSession {

    private static UserSession userSession;
    public static String userID = null;


    public UserSession() { }

    public UserSession(String id) throws SQLException, ClassNotFoundException {
        userID = id;
    }

    public static String getUserID() {
        return userID;
    }

    public void setUserRole(String userRole) {
        this.userID = userRole;
    }


    public static UserSession getInstance(String role) throws SQLException, ClassNotFoundException {
        if(userSession == null) {
            userSession = new UserSession(role);
        }
        return userSession;
    }

    public static void cleanUserSession() {
        userID = "";// or null
    }
}
