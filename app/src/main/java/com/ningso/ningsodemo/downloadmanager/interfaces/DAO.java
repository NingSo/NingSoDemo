package com.ningso.ningsodemo.downloadmanager.interfaces;

import android.database.sqlite.SQLiteDatabase;

import com.ningso.ningsodemo.db.DBProxy;
import com.ningso.ningsodemo.downloadmanager.entities.DLInfo;


/**
 * DAO抽象类
 * Abstract class of DAO.
 *
 * @author AigeStudio 2015-05-16
 */
public abstract class DAO {

    protected static SQLiteDatabase db = null;

    public DAO() {
        if (null == db) {
            db = DBProxy.getDatabase();
        }
    }

    public abstract void insertInfo(DLInfo info);

    public abstract void deleteInfo(String url);

    public abstract void updateInfo(DLInfo info);

    public abstract DLInfo queryInfo(String str);

}
