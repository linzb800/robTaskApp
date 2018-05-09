package com.linzb.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	public static String DATABASE_NAME = "udm_db";//数据库名称
	public static String TABLE_USER_COND = "t_user";
	public static String TABLE_BUYER = "t_buyer";
	
	private static final String TAG = "SQLiteTag"; 
	public static final int VERSION = 1;
	
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null,1);
	}
	
	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {

		String sql = "create table " + TABLE_USER_COND + "(id integer primary key AUTOINCREMENT,loginName varchar(20),udmName verchar(20),clients varchar(32),minPrice integer,maxPrice integer,titleFilter varchar(10240))";
		Log.i(TAG,"创建表：" + TABLE_USER_COND);
		db.execSQL(sql);
		Log.i(TAG,"创建表：" + TABLE_BUYER);
		String sql2 = "create table " + TABLE_BUYER + "(id integer primary key AUTOINCREMENT,userId integer,buyerId integer,sex varchar(8),name varchar(32),taobaoName varchar(32))";
		db.execSQL(sql2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG,"更新数据库版本");
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		
		Log.i(TAG,"找开数据库：" + db.getPath());
	}

}
