package com.park.commonasynctask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.park.commonasynctask.constants.Globals;

/**
 * 初期化する
 * Created by park on 2016/03/31.
 */
public class SplashActivity extends Activity {

    private Thread splashThread = new Thread(new SplashThread(SplashActivity.this));
    private TextView progressTextTv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        progressTextTv = (TextView)findViewById(R.id.progressText);
        progressTextTv.setText(R.string.splash_progress_start);

        splashThread.start();

    }


    class SplashThread implements Runnable {
        Context mContext = null;

        SplashThread(Context context) {
            mContext = context;
        }

        private void setProgressMessage(int message) {
            Message msg = progressHandler.obtainMessage();
            msg.obj = getString(message);
            progressHandler.sendMessage(msg);

        }

        @Override
        public void run() {
            Globals.getInstance(mContext);
            try {
                Thread.sleep(2000);
                setProgressMessage(R.string.splash_progress_update);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(2000);
                setProgressMessage(R.string.splash_progress_end);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Intent openMainActivity =  new Intent(SplashActivity.this, MainActivity.class);
            startActivity(openMainActivity);
            finish();

        }
    }


    public Handler progressHandler = new Handler(){
        public void handleMessage(Message msg){
            progressTextTv.setText(msg.obj.toString());
        }
    };
}
