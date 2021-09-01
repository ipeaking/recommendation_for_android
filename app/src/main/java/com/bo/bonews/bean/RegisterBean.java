package com.bo.bonews.bean;

public class RegisterBean {
    private String username;
    private String nick;
    private String password;
    private int age;
    private String gender;
    private String city;

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getNickName() {
        return nick;
    }

    public void setNickName(String nickName) {
        this.nick = nickName;
    }

    public String getPwd() {
        return password;
    }

    public void setPwd(String pwd) {
        this.password = pwd;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return city;
    }

    public void setLocation(String location) {
        this.city = location;
    }
}
