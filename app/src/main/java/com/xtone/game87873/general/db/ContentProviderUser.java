package com.xtone.game87873.general.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/***
 * @author huangzx
 * 账户共享
 * 
 * **/
public class ContentProviderUser extends ContentProvider {

	//常量UriMatcher.No_match表示不匹配任何路径的返回
	private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	private static final int USERS = 1;
	private static final int USER =2;
	static{
		//如果match()方法匹配content//:com.xtone.ContentProvider.ContentProviderUser/user路径，返回匹配码为1
		MATCHER.addURI("com.xtone.ContentProvider.ContentProviderUser", "user", USERS);
		//如果match()方法匹配content//:com.xtone.ContentProvider.ContentProviderUser/user/xxx路径，返回匹配码为2
		MATCHER.addURI("com.xtone.ContentProvider.ContentProviderUser", "user/#", USER);
	}
	
	@Override
	public int delete(Uri uri, String arg1, String[] arg2) {
//		UserAccountDao dao = new UserAccountDao(getContext());
		switch (MATCHER.match(uri)) {
		case USERS:
			
			break;

		case USER:
			break;
		}
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		
		return null;
	}

	@Override
	public boolean onCreate() {
		
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArgs,
			String orderBy) {
		UserAccountDao dao = new UserAccountDao(getContext());
//		System.out.println("------------dao="+dao);
		switch (MATCHER.match(uri)) {
		case USERS:
//			System.out.println("------------query="+dao.queryUser(columns, selection, selectionArgs, orderBy));
			return dao.queryUser(columns, selection, selectionArgs, orderBy);

		case USER:
			long id = ContentUris.parseId(uri);
			String where = "id="+id;
			if (selection != null && !"".equals(selection)) {
				where = selection+ " and "+where;
			}
			return dao.queryUser(columns, where, selectionArgs, orderBy);
			
		default:
			throw new IllegalArgumentException("Unknown Uri:"+uri.toString());
		}
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		
		return 0;
	}

}
