//package com.mobile.framework.rest;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
///**
// * Helps to create the Database for the rest request states.
// *
// * @author Jacob Zschunke
// *
// */
//class RequestDatabaseHelper extends SQLiteOpenHelper {
//
//    public static final String DB_NAME = "rest_requests";
//    public static final String TABLE_REST_STATE = "restStates";
//    private static final String SQL_CREATE_MAIN = "CREATE TABLE " + TABLE_REST_STATE + " (" + RestContract._ID + " INTEGER PRIMARY KEY, " + RestContract._URI
//            + " TEXT," + RestContract._STATE + " INTEGER," + RestContract._RESULT_CODE + " INTEGER," + RestContract._PAYLOAD + " BLOB,"
//            + RestContract._TIMESTAMP + " INTEGER" + ")";
//
//    RequestDatabaseHelper(Context context) {
//        super(context, DB_NAME, null, 1);
//    }
//
//    /* (non-Javadoc)
//     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
//     */
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(SQL_CREATE_MAIN);
//    }
//
//    /* (non-Javadoc)
//     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
//     */
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//    }
//}