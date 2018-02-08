package com.zhangqie.lockscreen.lock;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.zhangqie.lockscreen.R;


public class AppService extends Service {

	private AppReceiver mLockScreenReceiver;
	
	private IntentFilter mIntentFilter = new IntentFilter();
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 监听屏幕关闭和打开的广播必须动态注册
		mIntentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
		mIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		mIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
		mIntentFilter.addAction(Intent.ACTION_TIME_TICK);
		// 设置广播的优先级为最大
		mIntentFilter.setPriority(Integer.MAX_VALUE);
		if (null == mLockScreenReceiver) {
			mLockScreenReceiver = new AppReceiver();
			mIntentFilter.setPriority(Integer.MAX_VALUE);
			registerReceiver(mLockScreenReceiver, mIntentFilter);
			Toast.makeText(getApplicationContext(), "AppService", Toast.LENGTH_LONG).show();
			
			
		}
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    	builder.setTicker("APP正在运行");
    	builder.setAutoCancel(false);
    	builder.setContentTitle("APP正在运行");
    	builder.setContentText("您的收益正在累积");
    	builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
    	builder.setSmallIcon(R.mipmap.ic_launcher);
    	builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, LockScreenActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
    	Notification n = builder.build();
    	// 通知栏显示系统图标
		startForeground(0x111, n);
		
		Parser.killBackgroundProcess(this);
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		if (mLockScreenReceiver != null) {
			unregisterReceiver(mLockScreenReceiver);
			mLockScreenReceiver = null;
		}
		super.onDestroy();
		// 重启服务
		startService(new Intent(this, AppService.class));
		
		
	}

}
