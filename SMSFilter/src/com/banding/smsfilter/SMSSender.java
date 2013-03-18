package com.banding.smsfilter;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SMSSender extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);				
		setContentView(R.layout.activity_main);
		
		Button button_send = (Button)this.findViewById(R.id.button_send);
		button_send.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				confirmSend();
			}
		});
	}
	/**
	 * ���Ϳ۷���Ϣ
	 */
	public void sendSMS(String destAddress,String message) {
		SmsManager smsManager = SmsManager.getDefault();
		/* �����Զ���Action������Intent */
		Intent itSend = new Intent("com.banding.smsfilter.SMS_SEND");
        Intent itDeliver = new Intent("com.banding.smsfilter.SMS_DELIVERED");
        /* �������ͺ���ܵĹ㲥��ϢPendingIntent */
        PendingIntent mSendPI = PendingIntent.getBroadcast(getApplicationContext(),0,itSend,0);
        PendingIntent mDeliverPI = PendingIntent.getBroadcast(getApplicationContext(),0,itDeliver,0);
        /* ������Ϣ */
        List<String> texts = smsManager.divideMessage(message);
		for(String text : texts){
		smsManager.sendTextMessage(destAddress,null,text, mSendPI, mDeliverPI);
		Log.i("sms_send", "send a message to "+destAddress);
		}
	}
	/**
	 * ȷ�Ϸ��Ϳ۷���Ϣ
	 */
	public void confirmSend() {
		AlertDialog.Builder ad=new AlertDialog.Builder(SMSSender.this);
		/* ���õ����������� */
		ad.setTitle(R.string.warn_title);
	    ad.setMessage(R.string.warn_message);
	    /* ���ȷ����ť */
	    ad.setNegativeButton(R.string.button_ok, new DialogInterface.OnClickListener(){
	    	public void onClick(DialogInterface dialog,int i){
	    		sendSMS(getResources().getString(R.string.dest_address), getResources().getString(R.string.message_content));
	    		System.out.println(getResources().getString((R.string.dest_address)));
	    	}
	    });
	    /* ���ȡ����ť */
	    ad.setPositiveButton(R.string.button_cancel, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int i){	
				dialog.cancel();
			}
		});
	    /* ��ʾ���� */
	    ad.show();
	}			
}