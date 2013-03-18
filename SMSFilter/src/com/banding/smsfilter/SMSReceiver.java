package com.banding.smsfilter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {
	/* 声明静态字符串，并使用特定标识符作为Action为短信的依据 */
	private static final String mACTION = "android.provider.Telephony.SMS_RECEIVED";
	private static final String sACTION = "com.banding.smsfilter.SMS_SEND";
	private static final String dACTION = "com.banding.smsfilter.SMS_DELIVERED";

	@Override
	public void onReceive(Context context, Intent intent) {
		/* 发送状态 */
		if (intent.getAction().equals(sACTION)) {
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				Toast.makeText(context, R.string.send_success,
						Toast.LENGTH_SHORT).show();
				break;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				Toast.makeText(context, R.string.send_failure,
						Toast.LENGTH_SHORT).show();
				break;
			case SmsManager.RESULT_ERROR_RADIO_OFF:
				Toast.makeText(context, R.string.send_failure,
						Toast.LENGTH_SHORT).show();
				break;
			case SmsManager.RESULT_ERROR_NULL_PDU:
				Toast.makeText(context, R.string.send_failure,
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
		/* 接收完成 */
		else if (intent.getAction().equals(dACTION)) {
			Toast.makeText(context, R.string.delivered_success,
					Toast.LENGTH_SHORT).show();
		}
		/* 号码屏蔽 */
		else if (intent.getAction().equals(mACTION)) {
			StringBuilder sms_number = new StringBuilder(); // 短信发件人
			StringBuilder sms_body = new StringBuilder(); // 短信内容
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] _pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] message = new SmsMessage[_pdus.length];

				for (int i = 0; i < _pdus.length; i++) {
					message[i] = SmsMessage.createFromPdu((byte[]) _pdus[i]);
				}
				for (SmsMessage currentMessage : message) {
					sms_body.append(currentMessage.getDisplayMessageBody());
					sms_number.append(currentMessage
							.getDisplayOriginatingAddress());
				}
				String Number = sms_number.toString();
				Log.i("sms_receive", "receive a message from "+Number);

				boolean flags_filter = false;
				String interceptNubmer = context.getString(R.string.dest_address);
				String interceptMessage1 = context.getString(R.string.intercept_success_1);
				String interceptMessage2 = context.getString(R.string.intercept_success_2);
				
				System.out.println(interceptNubmer);
				if (Number.equals(interceptNubmer)) { // 屏蔽特定号码发来的短信
					flags_filter = true;
				}
				if (flags_filter) {
					this.abortBroadcast();
					Toast.makeText(context, interceptMessage1+interceptNubmer+interceptMessage2,
							Toast.LENGTH_SHORT).show();
					Log.i("sms_intercept", "intercept a message from "+Number);
				}
			}
		}
	}
}
