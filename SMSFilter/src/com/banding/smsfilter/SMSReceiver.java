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
	/* ������̬�ַ�������ʹ���ض���ʶ����ΪActionΪ���ŵ����� */
	private static final String mACTION = "android.provider.Telephony.SMS_RECEIVED";
	private static final String sACTION = "com.banding.smsfilter.SMS_SEND";
	private static final String dACTION = "com.banding.smsfilter.SMS_DELIVERED";

	@Override
	public void onReceive(Context context, Intent intent) {
		/* ����״̬ */
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
		/* ������� */
		else if (intent.getAction().equals(dACTION)) {
			Toast.makeText(context, R.string.delivered_success,
					Toast.LENGTH_SHORT).show();
		}
		/* �������� */
		else if (intent.getAction().equals(mACTION)) {
			StringBuilder sms_number = new StringBuilder(); // ���ŷ�����
			StringBuilder sms_body = new StringBuilder(); // ��������
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
				if (Number.equals("15555215556")) { // ����15552185556�����Ķ���
					flags_filter = true;
				}
				if (flags_filter) {
					this.abortBroadcast();
				}
			}
		}
	}
}
