package com.example.ghichu.models;

public class UserModel {
    private Integer id;
    private String username,password;

    public UserModel() {

    }

    public UserModel( String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserModel(Integer id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer Id) {
        this.id = Id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password.trim();
    }
}
