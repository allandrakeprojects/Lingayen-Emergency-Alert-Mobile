package com.example.user.appalert;

public class const_Profile {

    public String status;
    public String email;
    public String bname;
    public String uid;
    public String address;
    public String name;
    public String gender;

    public const_Profile() {
    }

    public const_Profile(String bname, String uid, String address, String name, String gender) {
        this.bname = bname;
        this.uid = uid;
        this.address = address;
        this.name = name;
        this.gender = gender;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
