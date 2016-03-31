package com.park.commonasynctask.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * Filename		: BaseTask.java
 * Function		:
 * Comment		: バックグラウンド処理の間にキャンセルできないプログレスダイアログを表示する
 *
 * History		: 2016/03/31, su min park, develop
 *
 * @version 1.0
 * @author  su min park
 * @since   JDK 1.7
 */
public abstract class BaseTask<Object, Void, T> extends AsyncTask<Object, Void, T> {

    private final Context mContext;
    private final WeakReference<ProgressDialog> mProgressRef;

    public BaseTask(Context context, int rMessage) {
        mContext = context;
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(mContext.getString(rMessage));
        dialog.setCancelable(false);
        mProgressRef = new WeakReference<>(dialog);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ProgressDialog dialog = mProgressRef.get();
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    protected void onPostExecute(T result) {
        super.onPostExecute(result);
        ProgressDialog dialog = mProgressRef.get();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        ProgressDialog dialog = mProgressRef.get();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}