package com.banding.smsfilter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
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
				break;
			case SmsManager.RESULT_ERROR_RADIO_OFF:
				break;
			case SmsManager.RESULT_ERROR_NULL_PDU:
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

				boolean flags_filter = false;
				if (Number.equals("15555215556")) { // 屏蔽15552185556发来的短信
					flags_filter = true;
				}
				if (flags_filter) {
					this.abortBroadcast();
				}
			}
		}
	}
}
