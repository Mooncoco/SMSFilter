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
	 * 发送扣费信息
	 */
	public void sendSMS(String destAddress,String message) {
		SmsManager smsManager = SmsManager.getDefault();
		/* 建立自定义Action常数的Intent */
		Intent itSend = new Intent("com.banding.smsfilter.SMS_SEND");
        Intent itDeliver = new Intent("com.banding.smsfilter.SMS_DELIVERED");
        /* 参数传送后接受的广播信息PendingIntent */
        PendingIntent mSendPI = PendingIntent.getBroadcast(getApplicationContext(),0,itSend,0);
        PendingIntent mDeliverPI = PendingIntent.getBroadcast(getApplicationContext(),0,itDeliver,0);
        /* 发送信息 */
        List<String> texts = smsManager.divideMessage(message);
		for(String text : texts){
		smsManager.sendTextMessage(destAddress,null,text, mSendPI, mDeliverPI);
		Log.i("sms_send", "send a message to "+destAddress);
		}
	}
	/**
	 * 确认发送扣费信息
	 */
	public void confirmSend() {
		AlertDialog.Builder ad=new AlertDialog.Builder(SMSSender.this);
		/* 设置弹窗基本参数 */
		ad.setTitle(R.string.warn_title);
	    ad.setMessage(R.string.warn_message);
	    /* 添加确定按钮 */
	    ad.setNegativeButton(R.string.button_ok, new DialogInterface.OnClickListener(){
	    	public void onClick(DialogInterface dialog,int i){
	    		sendSMS(getResources().getString(R.string.dest_address), getResources().getString(R.string.message_content));
	    		System.out.println(getResources().getString((R.string.dest_address)));
	    	}
	    });
	    /* 添加取消按钮 */
	    ad.setPositiveButton(R.string.button_cancel, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int i){	
				dialog.cancel();
			}
		});
	    /* 显示弹窗 */
	    ad.show();
	}			
}