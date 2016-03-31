package com.park.commonasynctask.util;

import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import com.park.commonasynctask.constants.Globals;
import com.park.commonasynctask.exception.InitializeException;

import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Filename		: LogUtil.java
 * Function		:
 * Comment		: ログ出力に関するUtil
 * History		: 2016/03/30, su min park, develop
 *
 * @version 1.0
 * @author  su min park
 * @since   JDK 1.7
 */
public class LogUtil {
	
	// **********************************************************************
	// 定数
	// **********************************************************************
 
	private static final String TAG = "After";

	private static final int LOG_LEVEL = Log.DEBUG;

	// **********************************************************************
	// メンバ
	// **********************************************************************
	private static boolean mIsShowLog = true;
	private static boolean mIsRunWriteThread = false;
	private static Queue<String> logDataQueue = new ConcurrentLinkedQueue<String>();

	// **********************************************************************
	// パブリックメソッド
	// **********************************************************************
 
	public static void setShowLog(boolean isShowLog) {
		mIsShowLog = isShowLog;
	}
 
	public static void d() {
		outputLog(Log.DEBUG, null, null);
	}
 
	public static void d(String message) {
		outputLog(Log.DEBUG, message, null);
	}
 
	public static void d(String message, Throwable throwable) {
		outputLog(Log.DEBUG, message, throwable);
	}
	
	
	public static void i(String message) {
		outputLog(Log.INFO, message, null);
	}
	public static void i(String message, Throwable throwable) {
		outputLog(Log.INFO, message, throwable);
	}
	
	public static void w(String message) {
		outputLog(Log.WARN, message, null);
	}
	public static void w(String message, Throwable throwable) {
		outputLog(Log.WARN, message, throwable);
	}
	
	public static void e(String message, Throwable throwable) {
		outputLog(Log.ERROR, message, throwable);
	}
	public static void e(Throwable throwable) {
		outputLog(Log.ERROR, null, throwable);
	}
	// **********************************************************************
	// プライベートメソッド
	// **********************************************************************
	
	private static void outputLog(int type, String message, Throwable throwable) {
		if (!mIsShowLog) {
			// ログ出力フラグが立っていない場合は何もしません。
			return;
		}
 
		// ログのメッセージ部分にスタックトレース情報を付加します。
		if (message == null) {
			message = getStackTraceInfo();
		} else {
			message = getStackTraceInfo() + message;
		}
 
		// ログを出力!
		switch (type) {
			case Log.DEBUG:
				if (throwable == null) {
					Log.d(TAG, message);
				} else {
					Log.d(TAG, message, throwable);
				}
				break;
			case Log.INFO:
				if (throwable == null) {
					Log.i(TAG, message);
				} else {
					Log.i(TAG, message, throwable);
				}
				break;
			case Log.WARN:
				if (throwable == null) {
					Log.w(TAG, message);
				} else {
					Log.w(TAG, message, throwable);
				}
				break;
			case Log.ERROR:
				if (throwable == null) {
					Log.e(TAG, message);
				} else {
					Log.e(TAG, message, throwable);
				}
				break;
		}


		//ファイル出力はしない
		//if (type >= LOG_LEVEL) {
			addQueue(message, throwable);
			threadQueue();
		//}
	}
	
	/**
	 * Queueにログを追加する
	 * 処理に時間がかかる場合はＴｈｒｅａｄに修正
	 * @param message
	 * @param throwable
	 */
	private static void  addQueue(String message, Throwable throwable) {
		String eMessage = "";
		if (throwable != null) {
			try {
				eMessage = getStackTraceString(throwable);
			} catch (IOException ioe) { }
		}

		String logWriteTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS").toString();
		logDataQueue.offer(logWriteTime + "\t" + message +"\n"+ eMessage);
		
	}
	
	/**
	 * Queueに溜まったデータをＴｈｒｅａｄで処理する
	 */
	private static synchronized void threadQueue() {
		try {
			if (!mIsRunWriteThread) {
				mIsRunWriteThread = true;
				
				//UI制御はない。	
				(new Thread(new Runnable() {
					@Override
					public void run() {

						StringBuffer logBuffer = new StringBuffer();
						while (!logDataQueue.isEmpty()) {
							String log = logDataQueue.poll();
							if (log != null) {
								logBuffer.append(log);
							}
						}

						//残り write
						writeLogFile(logBuffer);
						mIsRunWriteThread = false;
					}
				})).start();
			}
		} catch (Exception e) {
			Log.e(TAG, "Log thread error" , e);
		}
	}
	
	/**
	 * 操作ログデータをファイルに書き込む
	 * @param logBuffer
	 */
	private static synchronized void writeLogFile(final StringBuffer logBuffer) {
		if (logBuffer == null || logBuffer.length() == 0) return ;

		try {

			try {
				String sPath = Globals.getInstance().LOG_PATH;
				//String sPath = Environment.getExternalStorageDirectory().getPath() + "/appName/log";

				String sLogName = "ll_" + DateTimeFormat.forPattern("yyyyMMdd").toString() + ".txt";

				File logFile = new File(sPath + File.separator + sLogName);

				Files.createParentDirs(logFile);

				CharSink sink = Files.asCharSink(logFile, Charsets.UTF_8);
				sink.write(logBuffer.toString());
			} catch (IOException e) {
				Log.e(TAG, "Log Write failed" , e);
			} catch (InitializeException e) {
				Log.e(TAG, "Globals not init" , e);
			}

		} finally {
			logBuffer.charAt(0);
		}
	}

	/**
	 * スタックトレースから呼び出し元の基本情報を取得。
	 * @return
     */
	private static String getStackTraceInfo() {
		// 現在のスタックトレースを取得。
		// 0:VM 1:スレッド 2:getStackTraceInfo() 3:outputLog() 4:logDebug()等 5:呼び出し元
		StackTraceElement element = Thread.currentThread().getStackTrace()[5];
 
		String fullName = element.getClassName();
		String className = fullName.substring(fullName.lastIndexOf(".") + 1);
		String methodName = element.getMethodName();
		int lineNumber = element.getLineNumber();
 
		return "<<" + className + "#" + methodName + ":" + lineNumber + ">> ";
	}
	
	/**
	 * Throwableのスタックトレース情報を返す。
	 * @param e
	 * @return
	 * @throws IOException
	 */
	private static String getStackTraceString(Throwable e) throws IOException {
		
		// エラーのスタックトレースを表示
		StringWriter sw = new StringWriter();
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			pw.flush();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
		
		return sw.toString();
	}

}
