package com.park.commonasynctask.database.connection;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.park.commonasynctask.constants.Globals;
import com.park.commonasynctask.exception.InitializeException;
import com.park.commonasynctask.util.LogUtil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Filename		: DatabaseHelper.java
 * Function		: 
 * Comment		: データベースへの接続を管理する
 * 				  データベースの操作をするクラスは必ずこのクラスを継承すること。
 * History		: 2016/03/30, su min park, develop
 *
 * @version 1.0
 * @author  su min park
 * @since   JDK 1.7
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private final String databaseNm;
	protected SQLiteDatabase db = null;
	
	public DatabaseHelper(Context context, String databaseNm, int dbVersion) {
		super(context, databaseNm, null, dbVersion);
		this.databaseNm = databaseNm;

		//データベースオープン
		open();
	}
	
	
	
	/*************************************
	 * オーバーライト
	 *************************************/
	@Override
	public void onCreate(SQLiteDatabase db) {
		LogUtil.i("Database on Create ");

		try {
			if (Globals.getInstance().DB_MASTER_NAME.equals(databaseNm)) {
                LogUtil.i("Database - > masterSchemaCreate ");
                masterSchemaCreate(db);
            } else {
                LogUtil.i("Database - > Create call " + databaseNm);
            }
		} catch (InitializeException e) {
			LogUtil.e(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		LogUtil.d(databaseNm + " Database version upgrade [oldVersion:" + oldVersion + ", newVersion:" + newVersion + "]");

		/******************************
		 * データベースの構造を変更する
		 ******************************/
		//TODO: Version アップグレード時にデータベースを更新する。

		/*
		// 例)
        if (oldVersion < newVersion && oldVersion < 2) {
			try {
				LogUtil.i("Database - > onUpgrade add test COLUMN add oldVersion:" + oldVersion + ", newVersion:" + newVersion);
				String sql = "ALTER TABLE test ADD COLUMN aa text";
				db.execSQL(sql);
			} catch (SQLiteException se) {
				LogUtil.i("Database - > onUpgrade error " + se.getMessage());
			}
        }
        */

	}
	
	/**
	 * アプリ―共通用のテーブル作成
	 * アプリを新しく設置する際に呼ばれる
	 */
	private void masterSchemaCreate(SQLiteDatabase db) {
		String sql = 
			"CREATE TABLE test " +
			"(" +
				"test tinyint(1) NOT NULL, " +
				"test_id int(11) NOT NULL, " +
				"PRIMARY KEY (test_id) " +
			")";
		db.execSQL(sql);
		
	}

	/**
	 * <pre>
	 * データベースへの接続するAdapter
	 * </pre>
	 * 
	 * @throws SQLiteException
	 */
	public void open() throws SQLiteException {
		//LogUtil.d("Database Open");
		if(db == null || !db.isOpen()) {
			db = getWritableDatabase();
		}
	}


	/**
	 * データベースへの接続を切断するAdapter
	 */
	public synchronized void close() {
		if(db != null && db.isOpen()) {
			//LogUtil.d("Database close [isOpen:" + db.isOpen() + "]");
            db.close();
			db = null;
		}
		super.close();
	}

	
	/**
	 * データベースを更新するAdapter bind
	 * 
	 * @param sql 検索SQL
	 * @param bindSql bind Sql
	 * @throws SQLException SQLエラーの場合
	 */
	public void execSQL(String sql, String ...bindSql) throws SQLException {
		db.execSQL(sql, bindSql);
	}
	
	/**
	 * <pre>
	 * データベースを検索するAdapter bind
	 * 例）
	 * sql：select name, age from person where age >= ?;"
	 * bindSql: new String[]{"25"}
	 * </pre>
	 * @param sql 検索SQL
	 * @param bindSql bind Sql
	 * @return ArrayList<Map<String, String>>
	 * @throws SQLException SQLエラーの場合
	 */
	public ArrayList<Map<String, String>> rawQuery(String sql, String ...bindSql) throws SQLException {
		return cursorToArrayList(db.rawQuery(sql, bindSql));
	}



	/**
	 * <pre>
	 * 検索結果CursorをArrayList形式に変換します。
	 * 変換後CursorはCloseする。
	 * </pre>
	 * @param cursor
	 * @return ArrayList<Map<String, String>>
	 */
	public ArrayList<Map<String, String>> cursorToArrayList(Cursor cursor) {
		ArrayList<Map<String, String>> dataList = null;
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					dataList = new ArrayList<Map<String, String>>();
					do {
						Map<String, String> recordMap = new ConcurrentHashMap<>();
						
						String columnNames[] = cursor.getColumnNames();
						for(int i = 0; i < columnNames.length; i ++){
							if (cursor.getType(i) == Cursor.FIELD_TYPE_BLOB) {
								recordMap.put(columnNames[i], new String(cursor.getBlob(i), "UTF-8"));
							} else if (cursor.getType(i) == Cursor.FIELD_TYPE_FLOAT) {
								recordMap.put(columnNames[i], Float.toString(cursor.getFloat(i)));
							} else if (cursor.getType(i) == Cursor.FIELD_TYPE_INTEGER) {
								recordMap.put(columnNames[i], Long.toString(cursor.getLong(i)));
							} else if (cursor.getType(i) == Cursor.FIELD_TYPE_STRING) {
								recordMap.put(columnNames[i], cursor.getString(i));
							}
							
							//LogUtil.d("columnNames[i]:" + columnNames[i] + ", recordMap.get(columnNames[i]):"  + recordMap.get(columnNames[i]));
						}
						dataList.add(recordMap);
						
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				LogUtil.e(e);
			} finally {
				cursor.close();
			}
		}
		
		return dataList;
	}
	
	
	/************************
	 * Transacition関連
	 ************************/


}
