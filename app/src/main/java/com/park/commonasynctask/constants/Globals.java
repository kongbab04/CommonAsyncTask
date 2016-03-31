package com.park.commonasynctask.constants;

import android.content.Context;
import android.util.Log;

import com.park.commonasynctask.exception.InitializeException;
import com.park.commonasynctask.exception.PlatformException;
import com.park.commonasynctask.util.LogUtil;

import java.io.File;

/**
 * Filename		: Globals.java
 * Function		: 
 * Comment		: プロジェクトの全般的な設定情報が記載されている
 * History		: 2016/03/30, su min park, develop
 *
 * @version 1.0
 * @author  su min park
 * @since   JDK 1.7
 */
public class Globals {

	//LogUtilではなくLogを利用
	private static final String TAG = Globals.class.getSimpleName();

	private static Globals instance = null;

	//データベース
	public final String DB_MASTER_NAME;
	public final int DB_MASTER_VERSION;

	//経路関連
	public final String LOG_PATH;

	/**
	 * Constructor
	 */
	private Globals(Context context) {
		DB_MASTER_NAME = "master.db";
		DB_MASTER_VERSION = 1;

		//PATH
		LOG_PATH = context.getFilesDir().getPath() + File.separator + "logs";


	}

	/**
	 * Singleton Pattern
	 * @return
	 * @throws InitializeException
     */
	public static Globals getInstance() throws InitializeException {
		if (instance == null) {
			throw new InitializeException("Globals is not init. call");
		}
		return instance;
	}


	/**
	 * Globals
	 * @param context
	 * @return
	 * @throws PlatformException
     */
	public static Globals getInstance(Context context) {
		if (instance == null) {
			instance = new Globals(context);
			Log.i(TAG, "");
			LogUtil.i("Create Globals instance");
		}
		return instance;
	}






}
