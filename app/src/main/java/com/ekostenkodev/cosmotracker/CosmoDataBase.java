package com.ekostenkodev.cosmotracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pojo.CosmoObject;

/**
 * Created by Егор on 23.01.2018.
 */

public class CosmoDataBase {

    public String name;

    public CosmoDataBase(Context context) {
        this.name =  context.getDatabasePath("CosmoTrackerDB.db").getAbsolutePath() + "CosmoTrackerDB.db";;
    }


    public static CosmoObject getCosmoObject(Context context, int id){

        DatabaseHelper oh = new DatabaseHelper(context, "CosmoTrackerDB.db");
        SQLiteDatabase db = oh.openDataBase();
        Cursor cur = null;
        String strSQL = String.format("SELECT * FROM CosmoObjects WHERE _id = "+id);

        cur = db.rawQuery(strSQL, null);

        CosmoObject cosmo;


        cur.moveToFirst();

        cosmo = new CosmoObject(cur);

        if(cur != null){
            cur.close();
            cur = null;
        }
        if(db != null){
            db.close();
            db = null;
        } oh = null;

        return cosmo;
    }

    public static int getSize(Context context, QueryConstructor.queryType queryType){

        DatabaseHelper oh = new DatabaseHelper(context, "CosmoTrackerDB.db");
        SQLiteDatabase db = oh.openDataBase();
        Cursor cur = null;
        String strSQL = null;
        switch (queryType){
            case all:
                strSQL = String.format("SELECT COUNT(*) FROM CosmoObjects");
                break;
            case subs:
                strSQL = String.format("SELECT COUNT(*) FROM Subscriptions");
            break;

        }


        cur = db.rawQuery(strSQL, null);




        cur.moveToFirst();

        int size = cur.getInt(0);

        if(cur != null){
            cur.close();
            cur = null;
        }
        if(db != null){
            db.close();
            db = null;
        } oh = null;

        return size;
    }



    public static ArrayList<CosmoObject> getData(Context context, String query ){

        ArrayList<CosmoObject> list = new ArrayList<>();

        DatabaseHelper oh = new DatabaseHelper(context, "CosmoTrackerDB.db");
        SQLiteDatabase db = oh.openDataBase();
        Cursor cur = null;
        String strSQL = String.format(query);

        cur = db.rawQuery(strSQL, null);
        //cur = oh.getAllData(table);
        cur.moveToFirst();

        while(!cur.isAfterLast()) {

            CosmoObject c = new CosmoObject(cur);
            list.add(c);

            cur.moveToNext();
        }
        if(cur != null){
            cur.close();
            cur = null;
        }
        if(db != null){
            db.close();
            db = null;
        } oh = null;

        return list;
    }
}
