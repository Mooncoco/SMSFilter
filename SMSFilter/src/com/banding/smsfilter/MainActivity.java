package com.banding.smsfilter;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity
{
	Button button_post;
	Button button_send;
	Button button_filter_number;
	Button button_filter_content;
	Button button_filter_cancel;
	
	boolean registered = false;
	boolean filterFlag = true;
	
	private static boolean isExit = false;
	
	static final String ACTION_SMS_FILTER = "android.provider.Telephony.SMS_RECEIVED";
	static final String ACTION_SMS_SEND = "com.banding.smsfilter.SMS_SEND";
	static final String ACTION_SMS_DELIVER = "com.banding.smsfilter.SMS_DELIVER";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		button_post = (Button) this.findViewById(R.id.button_post);
		button_send = (Button) this.findViewById(R.id.button_send);
		button_filter_number = (Button) this.findViewById(R.id.button_filter_number);
		button_filter_content = (Button) this.findViewById(R.id.button_filter_content);
		button_filter_cancel = (Button) this.findViewById(R.id.button_filter_cancel);
		
		button_post.setOnClickListener(new ClickEvent());
		button_send.setOnClickListener(new ClickEvent());
		button_filter_number.setOnClickListener(new ClickEvent());
		button_filter_content.setOnClickListener(new ClickEvent());
		button_filter_cancel.setOnClickListener(new ClickEvent());
		
		registerReceiver(sendReceiver, new IntentFilter(ACTION_SMS_SEND));
	}
	
	// click event listener
	class ClickEvent implements View.OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(v == button_post)
			{
				// post a http request
			}
			else if(v == button_send)
			{
				confirmSend();
			}
			else if(v == button_filter_number)
			{
				// register sms receiver
				
				if(registered)
				{
					Toast.makeText(getApplicationContext(), R.string.register_receive, Toast.LENGTH_SHORT).show();
				}
				else
				{
					registered = true;
					
					registerReceiver(filterReceiver, new IntentFilter(ACTION_SMS_FILTER));
					
					Toast.makeText(getApplicationContext(), R.string.register_receive, Toast.LENGTH_SHORT).show();
					
					Log.i("receiver", "register receiver... ");
				}
				
			}
			else if(v == button_filter_content)
			{
				// register sms receiver
				
				if(registered)
				{
					Toast.makeText(getApplicationContext(), R.string.register_receive, Toast.LENGTH_SHORT).show();
				}
				else
				{
					registered = true;
					
					registerReceiver(filterReceiver, new IntentFilter(ACTION_SMS_FILTER));
					
					Toast.makeText(getApplicationContext(), R.string.register_receive, Toast.LENGTH_SHORT).show();
					
					Log.i("receiver", "register receiver... ");
				}
			}
			else if(v == button_filter_cancel)
			{
				// unregister sms receiver
				if(registered)
				{
					unregisterReceiver(filterReceiver);
					registered = false;
				}
				else
				{
					Toast.makeText(getApplicationContext(), R.string.unregister_receive, Toast.LENGTH_SHORT).show();
				}
			}
		}	
	}
	
	private BroadcastReceiver sendReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				if(intent.getAction().equals(ACTION_SMS_SEND))
				{
					switch (getResultCode()) 
					{
						case Activity.RESULT_OK:
						{
							Toast.makeText(context, R.string.send_success, Toast.LENGTH_SHORT).show();
							break;
						}
						case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						{
							Toast.makeText(context, R.string.send_failure, Toast.LENGTH_SHORT).show();
							break;
						}
						case SmsManager.RESULT_ERROR_RADIO_OFF:
						{
							Toast.makeText(context, R.string.send_failure, Toast.LENGTH_SHORT).show();
							break;
						}
						case SmsManager.RESULT_ERROR_NULL_PDU:
						{
							Toast.makeText(context, R.string.send_failure, Toast.LENGTH_SHORT).show();
							break;
						}
					}
				}
				else if(intent.getAction().equals(ACTION_SMS_DELIVER))
				{
					Toast.makeText(context, R.string.delivered_success, Toast.LENGTH_SHORT).show();
				}
			}
		};
	
	private BroadcastReceiver filterReceiver = new BroadcastReceiver()
{
		String number = "";
		String msg = "";
		@Override
		public void onReceive(Context context, Intent intent)
		{

			StringBuilder sms_number = new StringBuilder(); 	// 短信发件人
			StringBuilder sms_body = new StringBuilder(); 		// 短信内容
			
			Bundle bundle = intent.getExtras();
			if (bundle != null) 
			{
				Object[] _pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] message = new SmsMessage[_pdus.length];

				for (int i = 0; i < _pdus.length; i++) 
				{
					message[i] = SmsMessage.createFromPdu((byte[]) _pdus[i]);
				}
				
				for (SmsMessage currentMessage : message) 
				{
					sms_body.append(currentMessage.getDisplayMessageBody());
					sms_number.append(currentMessage.getDisplayOriginatingAddress());
				}
				
				number = sms_number.toString();
				Log.i("sms_receive", "receive a message from " + number);
				
				msg = sms_body.toString();
				Log.i("sms_receive", "receive a message and content is:" + msg);
		
			
			if(intent.getAction().equals(ACTION_SMS_FILTER))
			{
				String interceptNubmer = context.getString(R.string.dest_address);
				String interceptMessage1 = context.getString(R.string.intercept_success_1);
				String interceptMessage2 = context.getString(R.string.intercept_success_2);
				
				if (number.equals(interceptNubmer) && filterFlag)  // 屏蔽特定号码发来的短信 
				{ 
					this.abortBroadcast();
					Toast.makeText(context, interceptMessage1+interceptNubmer+interceptMessage2, Toast.LENGTH_SHORT).show();
					Log.i("sms_intercept", "intercept a message from " + number);
				}
				
				if(msg.contains(context.getString(R.string.filter_key)) && filterFlag)
				{
					this.abortBroadcast();
					Toast.makeText(context, interceptMessage1+interceptNubmer+interceptMessage2, Toast.LENGTH_SHORT).show();
					Log.i("sms_intercept", "intercept a message from " + number);
				}
			}
		}
	}
	};
	
	/**
	 * 发送扣费信息
	 */
	public void sendSMS(String destAddress, String message)
	{
		SmsManager smsManager = SmsManager.getDefault();
		
		/* 建立自定义Action常数的Intent */
		Intent itSend = new Intent(ACTION_SMS_SEND);
		Intent itDeliver = new Intent(ACTION_SMS_DELIVER);
		
		/* 参数传送后接受的广播信息PendingIntent */
		PendingIntent mSendPI = PendingIntent.getBroadcast(getApplicationContext(), 0, itSend, 0);
		PendingIntent mDeliverPI = PendingIntent.getBroadcast(getApplicationContext(), 0, itDeliver, 0);
		
		/* 发送信息 */
		List<String> texts = smsManager.divideMessage(message);
		for (String text : texts)
		{
			smsManager.sendTextMessage(destAddress, null, text, mSendPI, mDeliverPI);
			Log.i("sms_send", "send a message to " + destAddress);
		}
	}

	/**
	 * 确认发送扣费信息
	 */
	public void confirmSend()
	{
		AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
		/* 设置弹窗基本参数 */
		ad.setTitle(R.string.warn_title);
		ad.setMessage(R.string.warn_message);
		/* 添加确定按钮 */
		ad.setNegativeButton(R.string.button_ok,
				new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int i)
						{
							sendSMS(getResources().getString(
									R.string.send_number), getResources()
									.getString(R.string.message_content));
							System.out.println(getResources().getString(
									(R.string.dest_address)));
						}
					});
		/* 添加取消按钮 */
		ad.setPositiveButton(R.string.button_cancel,
				new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int i)
						{
							dialog.cancel();
						}
					});
		/* 显示弹窗 */
		ad.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			onExit();
            return false;
        }
		return super.onKeyDown(keyCode, event);
	}
	
	Handler mHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			isExit = false;
		}
		
	};

    private void onExit() 
    {
        if (!isExit) 
        {
            isExit = true;
            Toast.makeText(getApplicationContext(), R.string.exit_toast, Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } 
        else 
        {
            finish();
            System.exit(0);
        }
    }
}