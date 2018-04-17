package com.xtone.game87873.general.db;

import java.sql.SQLException;
import java.util.List;


import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.lidroid.xutils.http.HttpHandler;
import com.xtone.game87873.general.download.DownloadInfo;

public class AppDownloadDao {
    private Dao<DownloadInfo, Integer> appDao;
    private DatabaseHelper helper;

    public AppDownloadDao(Context context) {
        try {
            helper = DatabaseHelper.getHelper(context);
            appDao = helper.getDao(DownloadInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加一个
     *
     * @throws SQLException
     */
    public void addOrUpdate(DownloadInfo appInfo) {
        /*//事务操作
        TransactionManager.callInTransaction(helper.getConnectionSource(),
				new Callable<Void>()
				{

					@Override
					public Void call() throws Exception
					{
						return null;
					}
				});*/
        try {
            appDao.createOrUpdate(appInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public DownloadInfo get(int id) {
        try {
            return appDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过包名查询
     *
     * @return
     */
    public List<DownloadInfo> queryByPackage(String packageName) {
        try {
            return appDao.queryBuilder().
                    where().
                    eq("apkPackageName", packageName).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean delete(DownloadInfo info) {
        try {
            appDao.delete(info);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 单一条件不等查询
     *
     * @param columnName
     * @param value
     * @return
     */
    public List<DownloadInfo> queryByWhereNoState(String columnName, HttpHandler.State value) {
        try {
            return appDao.queryBuilder().where().ne(columnName, value).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 单一条件相等查询
     *
     * @param columnName
     * @param value
     * @return
     */
    public List<DownloadInfo> queryByWhereEqState(String columnName, HttpHandler.State value) {
        try {
            return appDao.queryBuilder().where().eq(columnName, value).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<DownloadInfo> queryAll() {
        try {
            return appDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
