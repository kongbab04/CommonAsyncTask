package com.park.commonasynctask.database;

import android.content.Context;

import com.park.commonasynctask.R;
import com.park.commonasynctask.common.BaseTask;
import com.park.commonasynctask.constants.Globals;
import com.park.commonasynctask.database.connection.DatabaseHelper;
import com.park.commonasynctask.exception.InitializeException;
import com.park.commonasynctask.util.LogUtil;


/**
 * Filename		: QueryExecutor.java
 * Function		:
 * Comment		: 非同期でQueryを実行する
 *
 * History		: 2016/03/31, su min park, develop
 *
 * @version 1.0
 * @author  su min park
 * @since   JDK 1.7
 */
public class QueryExecutor<T> extends BaseTask<Object, Void, T> {

    public enum WhichDatabase {
        MASTER
    }

    private Context mContext;
    private Query mQuery;
    private QueryExecutorCallback mCallback;
    private DatabaseHelper mDatabaseHelper;
    private WhichDatabase mWhichService;

    private Exception e = null;

    public QueryExecutor(Context context, WhichDatabase whichService, QueryExecutorCallback callback) {
        this(context, whichService, callback, R.string.common_loading);
    }

    public QueryExecutor(Context context, WhichDatabase whichService, QueryExecutorCallback callback, int message) {
        super(context, message);
        mContext = context;
        mWhichService = whichService;
        mCallback = callback;
    }


    public QueryExecutor setQuery(Query query) {
        this.mQuery = query;
        return this;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        LogUtil.d("before ");
        try {
            switch (mWhichService) {
                case MASTER:
                        mDatabaseHelper = new DatabaseHelper(mContext, Globals.getInstance().DB_MASTER_NAME, Globals.getInstance().DB_MASTER_VERSION);
                    break;
            }
        } catch (InitializeException e1) {
            LogUtil.e(e1);
        }

    }

    @Override
    protected T doInBackground(Object... params) {

        try {
            if (mQuery.getType() == Query.TYPE.SELECT) {
                LogUtil.d("run query--- ");
                Thread.sleep(3000);
            }
            else if (mQuery.getType() == Query.TYPE.DELETE) {
                // DELETE EXECUTOR CODE
            }
            else if (mQuery.getType() == Query.TYPE.INSERT) {
                // INSERT EXECUTOR CODE
            }
        }
        catch (Exception e) {
            this.e = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(T result) {
        super.onPostExecute(result);

        LogUtil.d("after");
        if (e == null) {
            if (mCallback != null) {
                mCallback.returnResult(result);
            }
            if (mDatabaseHelper != null) {
                mDatabaseHelper.close();
                mDatabaseHelper = null;
            }
        }
        else {

            // Something went wrong.
            // Do something with exception on
            e.printStackTrace();
        }
    }


    public static class Query {

        public enum TYPE {
            SELECT,
            DELETE,
            INSERT
        }
        String column;
        String orderBy;
        String tableFrom;
        String where;
        String[] bindWhere;
        boolean whereIn;
        TYPE type;

        public Query(TYPE type) {
            this.type = type;
        }

        public Query setSelectColumn(String column) {
            this.column = column;
            return this;
        }

        public Query setOrderByColumn(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public Query setSelectTable(String tableName) {
            this.tableFrom = tableName;
            return this;
        }

        public Query setSelectWhere(String where) {
            this.where = where;
            return this;
        }

        public Query setBindWhere(String... args) {
            bindWhere = args;
            return this;
        }

        public Query setWhereIn(boolean whereIn) {
            this.whereIn = whereIn;
            return this;
        }

        public TYPE getType() {
            return type;
        }
    }

    public interface QueryExecutorCallback<T> {
        void returnResult(T result);
    }
}
