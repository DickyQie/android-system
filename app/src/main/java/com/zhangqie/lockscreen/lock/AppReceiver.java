package com.zhangqie.lockscreen.lock;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;


/**
 * 全局广播接收器。用于接收关闭屏幕的广播、来电广播等。
 * 
 * @author zhangqie
 *
 */
public class AppReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String intentAction = intent.getAction();
		
		if (Intent.ACTION_SCREEN_OFF.equals(intentAction)) { // 接收到关闭屏幕的广播
			
				if (Parser.sPhoneCallState == TelephonyManager.CALL_STATE_IDLE) { // 手机状态为未来电的空闲状态

					// 判断锁屏界面是否已存在，如果已存在就先finish，防止多个锁屏出现
					if (!Parser.KEY_GUARD_INSTANCES.isEmpty()) {
						for (Activity activity : Parser.KEY_GUARD_INSTANCES) {
							activity.finish();
						}
					}
					// 启动锁屏
					Intent i = new Intent(context, KeyGuardActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				}
		} else {
			
			Parser.killBackgroundProcess(context);
		}
		
	}
	
}
