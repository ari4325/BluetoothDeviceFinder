package com.bdf.bluetoothdevicefinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListDevice {
    static Context context;
    static SharedPreferences sharedPreferences;
    static List<String>dateTime = new ArrayList<>();
    static List<String> deviceName = new ArrayList<>();
    static List<String> strongestConnection = new ArrayList<>();
    static List<String> names = new ArrayList<>();
    static List<String> date = new ArrayList<>();
    static List<String> searchName = new ArrayList<>();
    static List<String> searchDate = new ArrayList<>();
    static int currentsize = 0;
    static int newsize = 0;
    static List<String> top = new ArrayList<>();
    static List<String> mac = new ArrayList<>();
    static boolean updateSize(){
        if(newsize>currentsize){
            currentsize = newsize;
            return true;
        }
        return false;
    }
    static void setSearchName(String s){
        searchName.add(0, s);
    }
    static String getSearchName(int i){
        return searchName.get(i);
    }
    static List<String> getDates(){
        return date;
    }
    static void save(){
        Gson gson = new Gson();
        String macJson = gson.toJson(mac);
        String topJson = gson.toJson(top);
        String searchDateJson = gson.toJson(searchDate);
        String dateTimeJson = gson.toJson(dateTime);
        String deviceNameJson = gson.toJson(deviceName);
        String strongestConnectionJson = gson.toJson(strongestConnection);
        String namesJson = gson.toJson(names);
        String dateJsom = gson.toJson(date);
        String searchNameJson = gson.toJson(searchName);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("dateTime",dateTimeJson);
        editor.putString("deviceName", deviceNameJson);
        editor.putString("strongestConnection", strongestConnectionJson);
        editor.putString("names", namesJson);
        editor.putString("date", dateJsom);
        editor.putString("searchName", searchNameJson);
        editor.putString("searchDate", searchDateJson);
        editor.putString("top", topJson);
        editor.putString("mac", macJson);
        editor.commit();
    }
    static void setContext(Context c){
        context = c;
        sharedPreferences = context.getSharedPreferences("Lists", Context.MODE_PRIVATE);
    }
    static void load(){
        Gson gson = new Gson();
        deviceName.clear();
        dateTime.clear();
        strongestConnection.clear();
        names.clear();
        date.clear();
        searchName.clear();
        searchDate.clear();
        top.clear();
        mac.clear();
        String macJson = sharedPreferences.getString("mac", "");
        String searchDateJson = sharedPreferences.getString("searchDate", "");
        String searchNameJson = sharedPreferences.getString("searchName", "");
        String deviceNameJson = sharedPreferences.getString("deviceName", "");
        String dateTimeJson = sharedPreferences.getString("dateTime", "");
        String topJson = sharedPreferences.getString("top", "");
        String strongestConnectionJson = sharedPreferences.getString("strongestConnection", "");
        String namesJson = sharedPreferences.getString("names", "");
        String dateJson = sharedPreferences.getString("date", "");
        Type type = new TypeToken<List<String>>() {
        }.getType();
        Type type2 = new TypeToken<ArrayList<String>>() {
        }.getType();
        List<String> arrPackageData7 = gson.fromJson(searchDateJson, type);
        List<String> arrPackageData = gson.fromJson(deviceNameJson, type);
        List<String> arrPackageData2 = gson.fromJson(dateTimeJson, type);
        List<String> arrPackageData3 = gson.fromJson(strongestConnectionJson, type);
        List<String> arrPackageData4 = gson.fromJson(namesJson, type2);
        List<String> arrPackageDate5 = gson.fromJson(dateJson, type);
        List<String> arrPackageData6 = gson.fromJson(searchNameJson, type);
        List<String> arrPackageData8 = gson.fromJson(topJson, type);
        List<String> arrPackageData9 = gson.fromJson(macJson, type);
        if(arrPackageData == null || arrPackageData2 == null || arrPackageData3==null || arrPackageDate5 == null || arrPackageData6 == null || arrPackageData7 == null || arrPackageData8 == null || arrPackageData9 == null){

        }
        else {
            deviceName.addAll(arrPackageData);
            dateTime.addAll(arrPackageData2);
            strongestConnection.addAll(arrPackageData3);
            searchName.addAll(arrPackageData6);
            currentsize = deviceName.size();
            searchDate.addAll(arrPackageData7);
            newsize = deviceName.size();
            date.addAll(arrPackageDate5);
            top.addAll(arrPackageData8);
            mac.addAll(arrPackageData9);
        }
        if(arrPackageData4 == null){

        }
        else{
            names.addAll(arrPackageData4);
        }
    }
    static void setSearchDate(String s){
        searchDate.add(s);
    }
    static void setSearchDateAtPos(String s, int i){
        searchDate.set(i, s);
    }
    static int isPresentAtPosSearchDate(String s){
        for(int i = 0; i<searchDate.size(); i++){
            if(s.equals(searchDate.get(i)))
                return i;
        }
        return -1;
    }
    static List<String> getSearchDate(){return searchDate;}
    static void setTop(String s){
        top.add(s);
    }
    static void setTopAtPos(String s, int i){
        top.set(i , s);
    }
    static String getTop(int i){
        return top.get(i);
    }
    static void setMac(String s){
        mac.add(s);
    }
    static void setMacAtPos(String s, int i){
        mac.set(i , s);
    }
    static String getMac(int i){
        return mac.get(i);
    }
    static List<String> getDeviceName(){
        return deviceName;
    }
    static void setStrongestConnection(String s){
        strongestConnection.add(s);
    }
    static void setStrongestConnectionAtPos(String s, int i){
        strongestConnection.set(i, s);
    }
    static void setDateTimeAtPost(String s, int i){dateTime.set(i, s);}
    static String getStrongestConnectionAtPos(int i){
        return strongestConnection.get(i);
    }
    static String getDeviceNameAtPosition(int i){
        return deviceName.get(i);
    }
    static void setDate(String s){
        date.add(s);
    }
    static String getDate(int i){
        return date.get(i);
    }
    static List<String> getTop(){
        return top;
    }
    static int isPresentAtPosMac(String s){
        for (int i = 0; i<mac.size(); i++){
            if(s.equals(mac.get(i)))
                return i;
        }
        return -1;
    }
    static List<String> getMac(){
        return mac;
    }
    static void addDevice(String s){
        deviceName.add(s);
        newsize++;
    }
    static boolean isPresent(String s){
        for(int i = 0; i<deviceName.size(); i++){
            if(deviceName.get(i).equals(s)){
                return true;
            }
        }
        return false;
    }

    static int isPresentAtPos(String s){
        for(int i = 0; i<deviceName.size(); i++){
            if(deviceName.get(i).equals(s)){
                return i;
            }
        }
        return -1;
    }
    static void addDataTime(String s){
        dateTime.add(s);
    }
    static List<String> getDateTime(){
        return dateTime;
    }
    static String getDateTimeAtPosition(int i){
        return dateTime.get(i);
    }
    static void addName(String s){
        names.add(0, s);
    }
    static String getName(int i){
        return names.get(i);
    }
    static List<String> getNames(){
        return names;
    }
    static int getNamesSize() {
        return names.size();
    }
}
