package com.tribesquare;

public class LayoutData {
    private int id;
    private final String tableName;
    private final String txNo;
    private final String floorName;
    private final String userID;

    public LayoutData(int id, String tableName, String txNo, String floorName, String userID) {
        this.id = id;
        this.tableName = tableName;
        this.txNo = txNo;
        this.floorName = floorName;
        this.userID = userID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public String getTxNo() {
        return txNo;
    }


    public String getFloorName() {
        return floorName;
    }

    public String getUserID() {
        return userID;
    }

}
