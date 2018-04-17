package com.xtone.game87873.general.db;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.j256.ormlite.dao.Dao;
import com.xtone.game87873.section.entity.UserAccount;

public class UserAccountDao {
	private DatabaseHelper helper;
	private Dao<UserAccount, Integer> dao;
	
	@SuppressWarnings("unchecked")
	public UserAccountDao(Context cxt){
		if (dao==null) {
			helper = DatabaseHelper.getHelper(cxt);
			try {
				dao = helper.getDao(UserAccount.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Cursor queryUser(String[] columns, String selection, String[] selectionArgs, String orderBy){
		return helper.getReadableDatabase().query("tb_user_account", columns, selection, selectionArgs, null, null, orderBy);
	}
	
	public void addOrUpdate(UserAccount account){
		try {
			List<UserAccount> listTemp = dao.queryBuilder().where().eq("mobile", account.getMobile()).query();
			if (listTemp != null && listTemp.size() > 0 ) {
				dao.delete(listTemp);
			}
			dao.create(account);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getMobile(){
		try {
			List<UserAccount> listTemp = dao.queryForAll();
			if (listTemp.size()>0) {
				return listTemp.get(0).getMobile();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getPwd(){
		try {
			List<UserAccount> listTemp = dao.queryForAll();
			if (listTemp.size()>0) {
				return listTemp.get(0).getPwd();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void deleteAll(){
		try {
			dao.deleteBuilder().delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
