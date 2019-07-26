package com.example.user.appalert;

import java.util.Date;

public class const_Transaction {

    public String id;
    public String u_id;
    public String c_id;
    public String msg;
    public String u_name;
    public String emerg;
    public Long time;
    public Long negative_time;
    public String read;

    public const_Transaction() {
    }

    public Long getNegative_time() {
        return negative_time;
    }

    public void setNegative_time(Long negative_time) {
        this.negative_time = negative_time;
    }

    public const_Transaction(String id, String u_id, String c_id, String msg, String u_name, String emerg, Long time, String read, Long negative_time) {
        this.id = id;
        this.u_id = u_id;
        this.c_id = c_id;
        this.msg = msg;
        this.u_name = u_name;
        this.emerg = emerg;
        this.time = time;
        this.read = read;
        this.negative_time = negative_time;

    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getEmerg() {
        return emerg;
    }

    public void setEmerg(String emerg) {
        this.emerg = emerg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getU_name() {
        return u_name;
    }

    public void setU_name(String u_name) {
        this.u_name = u_name;
    }
}
