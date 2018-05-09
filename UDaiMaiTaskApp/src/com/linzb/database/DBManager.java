package com.linzb.database;

import java.util.ArrayList;
import java.util.List;

import com.linzb.model.Buyer;
import com.linzb.model.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
	private static DBManager dbManager;
	private Context context;

	private DBManager(Context context) {
		this.context = context;
	}

	public static DBManager getInstance(Context context) {
		if (null == dbManager) {
			dbManager = new DBManager(context);
		}
		return dbManager;
	}

	public void execSql(SQLiteDatabase db, String sql) {
		if (null != db && sql != null && !"".equals(sql)) {
			db.execSQL(sql);
		}
		db.close();
	}

	
	/**
	 * 是否道具登录
	 * @return
	 */
	public boolean isFirstLogin(String loginName){
		
		return false;
	}
	
	/**
	 * 获取用户配置
	 * @param db
	 * @param loginName
	 * @return
	 */
	public User queryUserCond(DBHelper db){
		System.out.println("查询：" + DBHelper.TABLE_USER_COND);
		Cursor cursor = db.getReadableDatabase().query(DBHelper.TABLE_USER_COND,null, null, null,null,null,null);
		while (cursor.moveToNext()) {
			User userCond = new User();
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String loginName = cursor.getString(cursor.getColumnIndex("loginName"));
			String udmName = cursor.getString(cursor.getColumnIndex("udmName"));
			String clients = cursor.getString(cursor.getColumnIndex("clients"));
			int minPrice = cursor.getInt(cursor.getColumnIndex("minPrice"));
			int maxPrice = cursor.getInt(cursor.getColumnIndex("maxPrice"));
			String titleFilters = cursor.getString(cursor.getColumnIndex("titleFilter"));
			
			userCond.setId(id);
			userCond.setLoginName(loginName);
			userCond.setUdmName(udmName);
			userCond.setMinPrice((short)minPrice);
			userCond.setMaxPrice((short)maxPrice);
			userCond.setClients(clients);
			userCond.setTitleFilters(titleFilters);
			
			return userCond;
		}
		return null;
	}
	
	public User insertUserCond(DBHelper db,User userCond){
		System.out.println("插入数据：" + DBHelper.TABLE_USER_COND);
		ContentValues cv = new ContentValues();
		cv.put("loginName", userCond.getLoginName());
		cv.put("udmName", userCond.getUdmName());
		cv.put("minPrice", userCond.getMinPrice());
		cv.put("maxPrice", userCond.getMaxPrice());
		cv.put("clients", userCond.getClients());
		cv.put("titleFilter", userCond.getTitleFilters());
		long id = db.getWritableDatabase().insert(DBHelper.TABLE_USER_COND, null, cv);
		System.out.println("insertId=" + id);
		if(id > 0){
			userCond.setId((int)id);
			return userCond;
		}
		return null;
	}
	
	public boolean updateUdmName(DBHelper db,User userCond){
		System.out.println("更新数据：" + DBHelper.TABLE_USER_COND + ",userId=" + userCond.getId());
		ContentValues cv = new ContentValues();
		cv.put("udmName", userCond.getUdmName());
		long id = db.getWritableDatabase().update(DBHelper.TABLE_USER_COND, cv, "id=?", new String[]{userCond.getId()+""});
		if(id == 1){
			return true;
		}
		return false;
	}
	
	public boolean updateUserCond(DBHelper db,User userCond){
		System.out.println("更新数据：" + DBHelper.TABLE_USER_COND + ",userId=" + userCond.getId());
		ContentValues cv = new ContentValues();
		cv.put("minPrice", userCond.getMinPrice());
		cv.put("maxPrice", userCond.getMaxPrice());
		cv.put("clients", userCond.getClients());
		cv.put("titleFilter", userCond.getTitleFilters());
		long id = db.getWritableDatabase().update(DBHelper.TABLE_USER_COND, cv, "id=?", new String[]{userCond.getId()+""});
		if(id == 1){
			return true;
		}
		return false;
	}
	
	
	public Buyer insertBuyer(DBHelper db,Buyer buyer){
		System.out.println("插入数据：" + DBHelper.TABLE_BUYER);
		
		if(buyer.getUserId() <= 0){
			return null;
		}
		
		ContentValues cv = new ContentValues();
		cv.put("userId", buyer.getUserId());
		cv.put("buyerId", buyer.getBuyerId());
		cv.put("sex", buyer.getSex());
		cv.put("name", buyer.getName());
		cv.put("taobaoName", buyer.getTaobaoName());
		long id = db.getWritableDatabase().insert(DBHelper.TABLE_BUYER, null, cv);
		System.out.println("insertId=" + id);
		if(id > 0){
			buyer.setId((int)id);
			return buyer;
		}
		return null;
	}
	
	public boolean updateBuyerBuyerId(DBHelper db,Buyer buyer){
		System.out.println("更新数据：" + DBHelper.TABLE_BUYER + ",id=" + buyer.getId());
		ContentValues cv = new ContentValues();
		cv.put("buyerId", buyer.getBuyerId());
		long id = db.getWritableDatabase().update(DBHelper.TABLE_BUYER, cv, "id=?", new String[]{buyer.getId()+""});
		if(id == 1){
			return true;
		}
		return false;
	}
	
	public boolean delBuyer(DBHelper db,int userId){
		System.out.println("删除数据：" + DBHelper.TABLE_BUYER + ",userId=" +userId);
		db.getWritableDatabase().delete(DBHelper.TABLE_BUYER,"id=?", new String[]{userId+""});
		return false;
	}
	
	public List<Buyer> queryBuyers(DBHelper db,int userId){
		System.out.println("查询：" + DBHelper.TABLE_BUYER + ",userId=" + userId);
		List<Buyer> list = new ArrayList<>();
		Cursor cursor = db.getReadableDatabase().query(DBHelper.TABLE_BUYER,null, "userId=?", new String[]{userId+""},null,null,null);
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int userIds = cursor.getInt(cursor.getColumnIndex("userId"));
			int buyerId = cursor.getInt(cursor.getColumnIndex("buyerId"));
			String sex = cursor.getString(cursor.getColumnIndex("sex"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String taobaoName = cursor.getString(cursor.getColumnIndex("taobaoName"));
			
			Buyer buyer = new Buyer(id, userIds, buyerId, sex, name, taobaoName);
			list.add(buyer);
		}
		return list;
	}
}
