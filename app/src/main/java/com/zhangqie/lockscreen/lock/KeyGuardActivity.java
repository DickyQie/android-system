package com.zhangqie.lockscreen.lock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.zhangqie.lockscreen.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 锁屏界面
 * 
 * @author zhangqie
 *
 */
public class KeyGuardActivity extends AppCompatActivity implements LockScreenView.OnTriggerListener {
	

	private LockScreenView mLockScreenView;
	private KeyGuardReceiver mKeyGuardReceiver;
	private TextView mTimeView;
	private TextView mDateView;
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

	private static final String[] DAY_OF_WEEK = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Parser.KEY_GUARD_INSTANCES.add(this);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);  
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		

		registerKeyGuardReceiver();//屏蔽Home
		
		setContentView(R.layout.key_guard);
		
		initViews();
		
		Parser.killBackgroundProcess(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// 设置该广告的左划收益和右划收益
		mLockScreenView.setTargetDrawablesAndTexts(
				R.mipmap.ic_lockscreen_trigger, R.mipmap.ic_lockscreen_unlock, "","");

	}

	
	private void initViews() {
		mLockScreenView = (LockScreenView) findViewById(R.id.lock_screen_pad);
		mTimeView = (TextView) findViewById(R.id.time);
		mDateView = (TextView) findViewById(R.id.date);
		mLockScreenView.setTargetDrawablesAndTexts(0, R.mipmap.ic_lockscreen_unlock, null, null);
		mLockScreenView.setOnTriggerListener(this);

		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		mTimeView.setText(TIME_FORMAT.format(date));
		String dateString = (calendar.get(Calendar.MONTH) + 1) + "月"
				+ calendar.get(Calendar.DAY_OF_MONTH) + "日 "
				+ DAY_OF_WEEK[calendar.get(Calendar.DAY_OF_WEEK) - 1];
		mDateView.setText(dateString);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 屏蔽所有实体按键
		return false;
	}
	
	// 4.0以上无法屏蔽Home键，所以没什么作用
	class KeyGuardReceiver extends BroadcastReceiver {

		static final String SYSTEM_REASON = "reason";
		static final String SYSTEM_HOME_KEY = "homekey";// home key
		static final String SYSTEM_RECENT_APPS = "recentapps";// long home key

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (reason != null) {
					if (reason.equals(SYSTEM_HOME_KEY)) {
						finish();
					} else if (reason.equals(SYSTEM_RECENT_APPS)) {
					}
				}
			} else if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED) || action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
				finish();
			}
		}
	}

	private void registerKeyGuardReceiver() {
		if (null == mKeyGuardReceiver) {
			mKeyGuardReceiver = new KeyGuardReceiver();
			registerReceiver(mKeyGuardReceiver, new IntentFilter());
		}
	}
	
	private void unregisterKeyGuardReceiver() {
		if (mKeyGuardReceiver != null) {
			unregisterReceiver(mKeyGuardReceiver);
		}
	}

	// 左划
	@Override
	public void onTriggerLeft() {
		Log.i("left","left");
		showToast("左划增加收益");
		finish();
	}

	// 右划
	@Override
	public void onTriggerRight() {
		Log.i("123","123");
		showToast("右划解锁添加收益");
		finish();
	}
	

	
	private void showToast(String str) {
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 网络中断时缓存收益信息
	 */
	private void saveProfitCache(String action, String phone, float profit, String advertiseId) {

	}



	@Override
	protected void onDestroy() {
		unregisterKeyGuardReceiver();
		super.onDestroy();
	}
}
