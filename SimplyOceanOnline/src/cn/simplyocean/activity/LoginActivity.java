package cn.simplyocean.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.simplyocean.application.MyApplication;
import cn.simplyocean.utils.ConstantValues;

public class LoginActivity extends Activity implements View.OnClickListener{
	private TextView mTitleTv;
	private EditText loginUsername;
	private EditText loginPassword;
	private Button loginSubmit;
	private ImageView goBack;
	private Button goRegistry;
	private boolean FIRST_ENTER = true;
	private LinearLayout loginLayout;
	private Handler mHandler;
	private ProgressDialog loginningDialog;

	@SuppressLint("HandlerLeak") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_login);
		mTitleTv = (TextView) findViewById(R.id.title_tv);
		mTitleTv.setText(R.string.login);
		
		loginLayout = (LinearLayout) findViewById(R.id.login_layout);
		loginUsername = (EditText) findViewById(R.id.login_username);
		loginPassword = (EditText) findViewById(R.id.login_password);
		loginSubmit = (Button) findViewById(R.id.login_submit);
		goBack = (ImageView) findViewById(R.id.back_img);
		goRegistry = (Button) findViewById(R.id.go_registry);
		
		goBack.setVisibility(View.VISIBLE);
		goBack.setOnClickListener(this);
		goRegistry.setOnClickListener(this);
		loginSubmit.setOnClickListener(this);		
		
		SharedPreferences sharedPreferences = getSharedPreferences("simplyocean_userprofile", Context.MODE_PRIVATE);
		String savedUsername = sharedPreferences.getString("username", null);
		if(FIRST_ENTER && savedUsername != null){
			FIRST_ENTER = false;
			loginUsername.setText(savedUsername.trim());
		}
		
		
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				Toast toast = null;
				SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences("simplyocean_userprofile", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				switch (msg.what) {
				case 0: //服务端返回用户不存在
					editor.putString("username", null);
					editor.putString("password", null);
					editor.commit();
					toast = Toast.makeText(getApplicationContext(), "用户名不存在！", Toast.LENGTH_SHORT);
					break;
				case -1: //服务端返回密码错误
					editor.putString("password", null);
					editor.commit();
					toast = Toast.makeText(getApplicationContext(), "密码错误！", Toast.LENGTH_SHORT);
					break;
				case 1: //服务端返回登录成功
					final String username = loginUsername.getText().toString().trim();
					final String password = loginPassword.getText().toString().trim();
					editor.putString("username", username);
					editor.putString("password", password);
					editor.commit();
					toast = Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_SHORT);
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
			        bundle.putString("username", username);
			        intent.putExtras(bundle);
		            setResult(1, intent);  
		            finish(); 
					break;
				case -2: //客户端登出成功
					editor.putString("username", null);
					editor.putString("password", null);
					editor.commit();
					toast = Toast.makeText(getApplicationContext(), "登出成功！", Toast.LENGTH_SHORT);
					loginLayout.setVisibility(View.VISIBLE);
					break;
				case 2: //网络超时
					toast = Toast.makeText(getApplicationContext(), "网络连接超时，请检查网络后再试！", Toast.LENGTH_SHORT);
					break;
				case 3: //服务器超时
					toast = Toast.makeText(getApplicationContext(), "服务器连接超时，请稍后再试！", Toast.LENGTH_SHORT);
					break;
				case 4: //获取数据超时
					toast = Toast.makeText(getApplicationContext(), "获取数据超时，请重试！", Toast.LENGTH_SHORT);
					break;
				case 5: //获取数据失败
					toast = Toast.makeText(getApplicationContext(), "数据请求失败，请重试！", Toast.LENGTH_SHORT);
					break;
	            }
				loginningDialog.dismiss();
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
	          super.handleMessage(msg);
		 }
		};
		
	}
	
	private void performLogin() {
		final String username = loginUsername.getText().toString().trim();
		final String password = loginPassword.getText().toString().trim();
	
		new Thread(new Runnable(){
			@Override
			public void run() {
				String result = null;
				Message msg = new Message();
				try {
					
					MyApplication app = (MyApplication) getApplication();
					HttpClient client = app.getHttpClient();
					HttpPost post = new HttpPost();
					List<NameValuePair> paramList = new ArrayList<NameValuePair>();
					BasicNameValuePair param = null;
					param = new BasicNameValuePair("username", username);
					paramList.add(param);
					param = new BasicNameValuePair("password", password);
					paramList.add(param); 
					post.setEntity(new UrlEncodedFormEntity(paramList, HTTP.UTF_8));
					post.setURI(new URI(ConstantValues.SERVERLET_URL + "user/userlogin/"));
					HttpResponse response = client.execute(post);
					if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
		                msg.what = 5;
		                mHandler.sendMessage(msg);
		                Log.e("MyApp", "请求失败");
		            } else {
						result = EntityUtils.toString(response.getEntity());							        
				        msg.what = Integer.parseInt(result.toString());
				        mHandler.sendMessage(msg);
			        }
				} catch(ConnectionPoolTimeoutException e) {
	                msg.what = 2;
	                mHandler.sendMessage(msg);
					Log.e("MyApp", "网络连接超时");
				} catch (ConnectTimeoutException e) {
	                msg.what = 3;
	                mHandler.sendMessage(msg);
					Log.e("MyApp", "服务器连接超时");
				} catch (SocketTimeoutException e) {
	                msg.what = 4;
	                mHandler.sendMessage(msg);
					Log.e("MyApp", "获取数据超时");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.back_img:
			this.finish();
			break;
		case R.id.login_submit:
			loginningDialog = ProgressDialog.show(LoginActivity.this, "" , "正在登录中，请稍候 ... ", true);
			performLogin();
			break;
		case R.id.go_registry:
			Intent intent = new Intent(LoginActivity.this, RegistryActivity.class);
			startActivity(intent);
			break;
		}
	}
}
