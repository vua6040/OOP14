package com.example.ghichu.components;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataLocalManager {
    private static final String PREF_FIRST_INSTALL = "PREF_FIRST_INSTALL";
    private static final String PREF_FIRST_USER = "PREF_FIRST_USER";
    private static final String PREF_LIST_USER_ACCOUNT_ID= "PREF_LIST_USER_ACCOUNT_ID";
    private static DataLocalManager instance;
    private MySharedPreferences mySharedPreferences;

    public static void init(Context context){
        instance = new DataLocalManager();
        instance.mySharedPreferences = new MySharedPreferences(context);
    }

    public static DataLocalManager getInstance(){
        if(instance==null){
            instance = new DataLocalManager();
        }
        return instance;
    }

    public static void setFirstInstalled(boolean isFirst){
        DataLocalManager.getInstance().mySharedPreferences.putBooleanValue(PREF_FIRST_INSTALL,isFirst);

    }

    public static boolean getFirstInstalled(){
        return DataLocalManager.getInstance().mySharedPreferences.getBooleanValue(PREF_FIRST_INSTALL);
    }

    public static void setFirstUser(String user){
        DataLocalManager.getInstance().mySharedPreferences.putUser(PREF_FIRST_USER,user);

    }

    public static String getFirstUser(){
        return DataLocalManager.getInstance().mySharedPreferences.getUser(PREF_FIRST_USER);
    }

    public static void setListUserAccountId(String userIds){
            List<String> lis = new ArrayList<>();

            String strJsonArray =DataLocalManager.getInstance().mySharedPreferences.getUserId(PREF_LIST_USER_ACCOUNT_ID);
            if(strJsonArray.length()>0 && !strJsonArray.isEmpty()) {
                String[] convertStringToArray = strJsonArray.substring(1,strJsonArray.length()-1).split(", ");
                for(String i:convertStringToArray){
                    lis.add(i.trim());
                }
            }
            if(!lis.contains(userIds) && Integer.parseInt(userIds)>0){
                lis.add(userIds);
            }
            DataLocalManager.getInstance().mySharedPreferences.putUserId(PREF_LIST_USER_ACCOUNT_ID,lis.toString());
    }

    public static List<String> getListUserAccountId(){
        List<String> lis = new ArrayList<>();

        String strJsonArray =DataLocalManager.getInstance().mySharedPreferences.getUserId(PREF_LIST_USER_ACCOUNT_ID);
        if(strJsonArray.length()>0 && !strJsonArray.isEmpty()){
            String[] convertStringToArray = strJsonArray.substring(1,strJsonArray.length()-1).split(", ");
            for(String i:convertStringToArray){
                lis.add(i.trim());
            }
        }
        return lis;
    }
}
