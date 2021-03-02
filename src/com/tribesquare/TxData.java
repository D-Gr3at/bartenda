package com.tribesquare;

public class TxData {
    private int id;
    private String txNo;
    private String txType;
    private String txDate;
    private String txTime;
    private String responseTime;
    private String status;
    private String staff;

    public TxData(int id, String txNo, String txType, String txDate, String txTime, String responseTime, String status, String staff) {
        this.id = id;
        this.txNo = txNo;
        this.txType = txType;
        this.txDate = txDate;
        this.txTime = txTime;
        this.responseTime = responseTime;
        this.status = status;
        this.staff = staff;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTxNo() {
        return txNo;
    }

    public void setTxNo(String txNo) {
        this.txNo = txNo;
    }

    public String getTxType() {
        return txType;
    }

    public void setTxType(String txType) {
        this.txType = txType;
    }

    public String getTxDate() {
        return txDate;
    }

    public void setTxDate(String txDate) {
        this.txDate = txDate;
    }

    public String getTxTime() {
        return txTime;
    }

    public void setTxTime(String txTime) {
        this.txTime = txTime;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }


}
