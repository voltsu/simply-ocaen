 package cn.simplyocean.activity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cn.simplyocean.application.MyApplication;
import cn.simplyocean.jsonmodel.SmsMessage;
import cn.simplyocean.utils.ConstantValues;
import cn.simplyocean.utils.InputValidation;

import com.google.gson.Gson;

public class RegistryActivity extends Activity implements View.OnClickListener{
	private ImageView goBack;
	private Button getVerifyCode;
	private EditText regPhone;
	private Button regNow;
	private EditText regVerifyCode;
	private CheckBox agreedCheck;
	private EditText regPassword;
	
	private String verifyCode;
	private Handler mHandler;
	private int time = 10;
	private Handler sendButtonHandler;
	private Handler counterHandler;
	private Thread oneSecondThread;
	private HandlerThread mHandlerThread;
	
	@SuppressLint("HandlerLeak") @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_registry);
		goBack = (ImageView)this.findViewById(R.id.back_img);
		getVerifyCode = (Button) this.findViewById(R.id.get_verifycode);
		regPhone = (EditText)this.findViewById(R.id.reg_phone);
		regNow = (Button)this.findViewById(R.id.reg_now);
		regVerifyCode = (EditText) this.findViewById(R.id.reg_verifycode);
		agreedCheck = (CheckBox) this.findViewById(R.id.agree);
		regPassword = (EditText) this.findViewById(R.id.reg_password);
		
		goBack.setVisibility(View.VISIBLE);
		goBack.setOnClickListener(this);
		getVerifyCode.setOnClickListener(this);
		regNow.setOnClickListener(this);
		
		String phoneNumber = getPhoneNumber();
		if(!phoneNumber.isEmpty()){
			if(InputValidation.isMobileNO(phoneNumber)){
				regPhone.setText(phoneNumber);
			}
		}
		
        mHandlerThread = new HandlerThread("count", 5);
        mHandlerThread.start();
        sendButtonHandler = new Handler(mHandlerThread.getLooper());
		
        oneSecondThread = new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					time--;
					Thread.sleep(1000);	
					Message msg = new Message();  
                    msg.arg1 = time;
					counterHandler.sendMessage(msg);
					Log.e("count", Integer.toString(time));			
				}catch(Exception e){			
				}
			}	
		});
        
        counterHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if (time > 0) {
					getVerifyCode.setText(msg.arg1 + "秒后重试");
					sendButtonHandler.post(oneSecondThread);
				}else{
					getVerifyCode.setText("重新获取验证码");
					getVerifyCode.setEnabled(true);
					regPhone.setEnabled(true);	
				}
				super.handleMessage(msg);
			}
		};
		
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				boolean phoneExist = msg.getData().getBoolean("phoneExist");
				if (phoneExist){
					getVerifyCode.setEnabled(true);
					regPhone.setEnabled(true);
					Toast.makeText(getApplicationContext(), "您输入的手机号码已被注册！", Toast.LENGTH_SHORT).show();
				}else{
					time = 10;
			        sendButtonHandler.post(oneSecondThread);			
					Toast.makeText(getApplicationContext(), "验证码已发送，请注意查收！", Toast.LENGTH_SHORT).show();
					new Thread(new Runnable(){
						@Override
						public void run() {
							sendSms(regPhone.getText().toString().trim());						
						}
					}).start();
				}
				super.handleMessage(msg);
			}
		};
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.back_img:
			this.finish();
			break;
		case R.id.get_verifycode:	
			getVerifyCode.setEnabled(false);
			regPhone.setEnabled(false);	
			new Thread(new Runnable(){
				@Override
				public void run() {
					boolean phoneExist = checkPhoneExist(regPhone.getText().toString().trim());	
					Bundle bundle = new Bundle();
					bundle.putBoolean("phoneExist", phoneExist);
					Message msg = new Message();
					msg.setData(bundle);
					mHandler.sendMessage(msg);
				}			
			}).start();
			break;
		case R.id.reg_now:
			if(agreedCheck.isChecked()){
				if(InputValidation.isPasswordStandard(regPassword.getText().toString().trim())){
					if(InputValidation.isMobileNO(regPhone.getText().toString().trim())){
						if(regVerifyCode.getText().toString().trim().equals(verifyCode)){					
							verifyCode = null;
							registerUser();
							Toast.makeText(getApplicationContext(), "注册中！", Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(getApplicationContext(), "验证码输入错误！", Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(getApplicationContext(), "请验证您的手机号码！", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "密码只能为包含字母、数字、下划线的6至18位组合！", Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(getApplicationContext(), "请先同意使用和隐私条款！", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
	
	private boolean checkPhoneExist(String phone){
		String result = "";
		try {	
			MyApplication app = (MyApplication)this.getApplication();
			HttpClient client = app.getHttpClient();
			HttpGet get = new HttpGet();
			get.setURI(new URI(ConstantValues.SERVERLET_URL + "user/checkUserExist?username=" + phone));
			HttpResponse response = client.execute(get);
			result = EntityUtils.toString(response.getEntity());
			Log.e("MyApp", result);
			if(Integer.valueOf(result.trim()) == 1){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return true;
	}
	
	private void sendSms(String phone){
		String result = "";
		verifyCode = Integer.toString((int)(Math.random()*900000 + 100000));
		Log.e("SMS---->>>", verifyCode);		
		try {	
			MyApplication app = (MyApplication) getApplication();
			HttpClient client = app.getHttpClient();
			HttpPost post = new HttpPost();
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();
			BasicNameValuePair param = null;
			param = new BasicNameValuePair("phone", phone);
			paramList.add(param);
			param = new BasicNameValuePair("verifyCode", verifyCode);
			paramList.add(param); 
			post.setEntity(new UrlEncodedFormEntity(paramList, HTTP.UTF_8));
			post.setURI(new URI(ConstantValues.SERVERLET_URL + "sms/sendSms"));
			HttpResponse response = client.execute(post);
			if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.e("MyApp", "请求失败");
            } else {
				result = EntityUtils.toString(response.getEntity());
				Gson gson = new Gson();
				SmsMessage sms = new SmsMessage();
				sms = gson.fromJson(result, new SmsMessage().getClass());
				Log.e("SMS---->>>", sms.toString());
	        }
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	private void registerUser(){
		
	}
		
	private String getPhoneNumber(){
		TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getLine1Number();
		Log.e("Number--->>>>", imei);
		return imei;
	}
}
