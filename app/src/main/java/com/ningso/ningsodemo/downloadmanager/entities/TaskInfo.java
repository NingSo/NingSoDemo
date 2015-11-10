package com.ningso.ningsodemo.downloadmanager.entities;

import java.io.File;
import java.io.Serializable;

/**
 * 任务实体类
 * Task entity.
 *
 * @author AigeStudio 2015-05-16
 * @author AigeStudio 2015-05-29
 *         修改构造方法
 */
public class TaskInfo extends DLInfo implements Serializable {
	public int progress, length;

	/**
	 * @param dlLocalFile 本地文件
	 * @param baseUrl     基础URL
	 * @param realUrl     实际URL，防止重定向
	 * @param progress    下载进度
	 * @param length      文件总长度
	 */
	public TaskInfo(File dlLocalFile, String baseUrl, String realUrl, int progress, int length) {
		super(dlLocalFile, baseUrl, realUrl);
		this.progress = progress;
		this.length = length;
	}
}
