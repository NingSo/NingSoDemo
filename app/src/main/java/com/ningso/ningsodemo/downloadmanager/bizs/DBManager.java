package com.ningso.ningsodemo.downloadmanager.bizs;

import android.database.sqlite.SQLiteDatabase;

import com.ningso.ningsodemo.downloadmanager.cons.PublicCons;
import com.ningso.ningsodemo.downloadmanager.daos.TaskDAO;
import com.ningso.ningsodemo.downloadmanager.daos.ThreadDAO;
import com.ningso.ningsodemo.downloadmanager.entities.TaskInfo;
import com.ningso.ningsodemo.downloadmanager.entities.ThreadInfo;

import java.util.List;


/**
 * 数据库管理器
 * 封装各种业务数据操作
 *
 * @author AigeStudio 2015-05-09
 */
public final class DBManager {

    private static DBManager sManager = null;

    private TaskDAO daoTask;
    private ThreadDAO daoThread;

    private DBManager() {
        daoTask = new TaskDAO();
        daoThread = new ThreadDAO();
    }

    /**
     * 获取数据库管理器单例对象
     *
     * @return 数据库管理器单例对象
     */
    public static DBManager getInstance() {
        if (null == sManager) {
            sManager = new DBManager();
        }
        return sManager;
    }

    /**
     * 插入一条下载任务数据信息
     *
     * @param info 下载任务对象
     */
    public synchronized void insertTaskInfo(TaskInfo info) {
        daoTask.insertInfo(info);
    }

    /**
     * 根据下载地址删除一条下载任务数据信息
     *
     * @param url 下载地址
     */
    public synchronized void deleteTaskInfo(String url) {
        daoTask.deleteInfo(url);
    }

    /**
     * 更新一条下载任务数据信息
     *
     * @param info 下载任务对象
     */
    public synchronized void updateTaskInfo(TaskInfo info) {
        daoTask.updateInfo(info);
    }

    /**
     * 根据下载地址查询一条下载任务数据信息
     *
     * @param url 下载地址
     * @return 下载任务对象
     */
    public synchronized TaskInfo queryTaskInfoByUrl(String url) {
        return (TaskInfo) daoTask.queryInfo(url);
    }

    /**
     * 插入一条线程数据信息
     *
     * @param info 线程对象
     */
    public synchronized void insertThreadInfo(ThreadInfo info) {
        daoThread.insertInfo(info);
    }

    /**
     * 根据线程ID删除一条线程数据信息
     *
     * @param id 线程ID
     */
    public synchronized void deleteThreadInfoById(String id) {
        daoThread.deleteInfo(id);
    }

    /**
     * 根据下载地址删除所有线程数据信息
     *
     * @param url 下载地址
     */
    public synchronized void deleteThreadInfos(String url) {
        daoThread.deleteInfo(url);
    }

    /**
     * 更新一条线程数据信息
     *
     * @param info 线程对象
     */
    public synchronized void updateThreadInfo(ThreadInfo info) {
        daoThread.updateInfo(info);
    }

    /**
     * 根据线程ID查询一条线程数据信息
     *
     * @param id 线程ID
     * @return 线程对象
     */
    public synchronized ThreadInfo queryThreadInfoById(String id) {
        return (ThreadInfo) daoThread.queryInfo(id);
    }

    /**
     * 根据下载地址查询所有线程数据信息
     *
     * @param url 下载地址
     * @return 所有该地址下对应的线程信息
     */
    public synchronized List<ThreadInfo> queryThreadInfos(String url) {
        return daoThread.queryInfos(url);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 14) {
            db.execSQL(PublicCons.DBCons.TB_TASK_SQL_UPGRADE);
            db.execSQL(PublicCons.DBCons.TB_THREAD_SQL_UPGRADE);
            db.execSQL(PublicCons.DBCons.TB_TASK_SQL_CREATE);
            db.execSQL(PublicCons.DBCons.TB_THREAD_SQL_CREATE);
        }
    }
}
