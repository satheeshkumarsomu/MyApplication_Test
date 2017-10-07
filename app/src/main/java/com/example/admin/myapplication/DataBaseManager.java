package com.example.admin.myapplication;

/**
 * @author Satheeshkumar
 **/
public class DataBaseManager {

    public static final String DATABASE_NAME = "Sample.db";
    public static final String TABLE = "item";

    public static final String CREATE_TABLE = "create table if not exists " + TABLE + "(id integer primary key," +
            "name varchar," +
            "gender varchar)";
}
