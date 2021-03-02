package com.tribesquare;

public class UserData {
    private int id;
    private String phone, name, role, staffNumber;

    public UserData(int id, String phone, String name, String role, String staffNumber) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.role = role;
        this.staffNumber = staffNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }


}
