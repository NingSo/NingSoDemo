package com.ningso.ningsodemo.downloadmanager.daos;

import android.database.Cursor;

import com.ningso.ningsodemo.downloadmanager.cons.PublicCons;
import com.ningso.ningsodemo.downloadmanager.entities.DLInfo;
import com.ningso.ningsodemo.downloadmanager.entities.TaskInfo;
import com.ningso.ningsodemo.downloadmanager.interfaces.DAO;

import java.io.File;


/**
 * 下载任务的DAO实现
 * DAO for download task.
 *
 * @author AigeStudio 2015-05-09
 * @author AigeStudio 2015-05-29
 *         根据域名重定向问题进行逻辑修改
 */
public class TaskDAO extends DAO {

	public TaskDAO() {
		super();
	}

	@Override
	public void insertInfo(DLInfo info) {
		TaskInfo i = (TaskInfo) info;
		db.execSQL("INSERT INTO " + PublicCons.DBCons.TB_TASK + "(" +
						PublicCons.DBCons.TB_TASK_URL_BASE + ", " +
						PublicCons.DBCons.TB_TASK_URL_REAL + ", " +
						PublicCons.DBCons.TB_TASK_FILE_PATH + ", " +
						PublicCons.DBCons.TB_TASK_PROGRESS + ", " +
						PublicCons.DBCons.TB_TASK_FILE_LENGTH + ") values (?,?,?,?,?)",
				new Object[]{i.baseUrl, i.realUrl, i.dlLocalFile.getAbsolutePath(), i.progress,
						i.length});
	}

	@Override
	public void deleteInfo(String url) {
		db.execSQL("DELETE FROM " + PublicCons.DBCons.TB_TASK + " WHERE " +
				PublicCons.DBCons.TB_TASK_URL_BASE + "=?", new String[]{url});
	}

	@Override
	public void updateInfo(DLInfo info) {
		TaskInfo i = (TaskInfo) info;
		db.execSQL("UPDATE " + PublicCons.DBCons.TB_TASK + " SET " +
				PublicCons.DBCons.TB_TASK_PROGRESS + "=? WHERE " +
				PublicCons.DBCons.TB_TASK_URL_BASE + "=?", new Object[]{i.progress, i.baseUrl});
	}

	@Override
	public DLInfo queryInfo(String url) {
		TaskInfo info = null;
		Cursor c = db.rawQuery("SELECT " +
				PublicCons.DBCons.TB_TASK_URL_BASE + ", " +
				PublicCons.DBCons.TB_TASK_URL_REAL + ", " +
				PublicCons.DBCons.TB_TASK_FILE_PATH + ", " +
				PublicCons.DBCons.TB_TASK_PROGRESS + ", " +
				PublicCons.DBCons.TB_TASK_FILE_LENGTH + " FROM " +
				PublicCons.DBCons.TB_TASK + " WHERE " +
				PublicCons.DBCons.TB_TASK_URL_BASE + "=?", new String[]{url});
		if (c.moveToFirst()) {
			info = new TaskInfo(new File(c.getString(2)), c.getString(0), c.getString(1),
					c.getInt(3), c.getInt(4));
		}
		c.close();
		return info;
	}
}
