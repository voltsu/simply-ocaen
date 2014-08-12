package cn.simplyocean.fragment;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.simplyocean.activity.LoginActivity;
import cn.simplyocean.activity.R;
import cn.simplyocean.application.MyApplication;
import cn.simplyocean.utils.ConstantValues;

public class ProfileFragment extends BaseFragment {

	private static final String TAG = "ProfileFragment";
	private TextView mTitleTv;
	private RelativeLayout loginLayout;
	private RelativeLayout profileLayout;
	private Button goLogin;
	private static int REQUEST_CODE = 1; 
	private boolean FIRST_ENTER = true;
	private Handler mHandler;
	private Button goLogout;
	private TextView welcomeUsername;
	
	public static ProfileFragment newInstance() {
		ProfileFragment profileFragment = new ProfileFragment();
		return profileFragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {	
		View view = inflater.inflate(R.layout.fragment_profile, container, false);
		loginLayout = (RelativeLayout) view.findViewById(R.id.login_layout);
		profileLayout = (RelativeLayout) view.findViewById(R.id.profile_layout);
		goLogin = (Button) view.findViewById(R.id.go_login);
		goLogout = (Button) view.findViewById(R.id.go_logout);
		mTitleTv = (TextView) view.findViewById(R.id.title_tv);
		mTitleTv.setText(R.string.profile);
		welcomeUsername = (TextView) view.findViewById(R.id.welcomeUsername);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);	
	}

	@SuppressLint("HandlerLeak") @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences("simplyocean_userprofile", Context.MODE_PRIVATE);
		String savedUsername = sharedPreferences.getString("username", null);
		String savedPassword = sharedPreferences.getString("password", null);
		if(FIRST_ENTER & savedUsername != null & savedPassword != null){
			FIRST_ENTER = false;
			performLogin(savedUsername, savedPassword);
		}
		
		goLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivityForResult(intent, REQUEST_CODE);
			}
		});
		
		goLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				performLogout();
			}
		});
		
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					loginLayout.setVisibility(View.VISIBLE);
					profileLayout.setVisibility(View.GONE);
					break;
				case 1:
					loginLayout.setVisibility(View.GONE);
					profileLayout.setVisibility(View.VISIBLE);
					Bundle bundle = msg.getData();
					String getUsername = bundle.getString("username");
					welcomeUsername.setText("Welcome " + getUsername + "!");
					break;
				case -1:
					loginLayout.setVisibility(View.VISIBLE);
					profileLayout.setVisibility(View.GONE);
					break;
				case -2:
					SharedPreferences sharedPreferences = getActivity().getSharedPreferences("simplyocean_userprofile", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("username", null);
					editor.putString("password", null);
					editor.commit();
					loginLayout.setVisibility(View.VISIBLE);
					profileLayout.setVisibility(View.GONE);
					break;
	            }
	          super.handleMessage(msg);
		 }
		};
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public String getFragmentName() {
		return TAG;
	}

	private void performLogin(String savedUsername, String savedPassword) {
		final String username = savedUsername;
		final String password = savedUsername;
		new Thread(new Runnable(){
			@Override
			public void run() {
				String result = null;
				try {
					MyApplication app = (MyApplication) getActivity().getApplication();
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
					result = EntityUtils.toString(response.getEntity());	
					
			        Message msg = new Message();
			        Bundle bundle = new Bundle();
			        bundle.putString("username", username);
			        msg.setData(bundle);
			        msg.what = Integer.parseInt(result.toString());
			        mHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}).start();
	}
	
	private void performLogout() {
		Message msg = new Message();
		msg.what = Integer.valueOf(-2);
		mHandler.sendMessage(msg);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==REQUEST_CODE) {  
	        if (resultCode == 1) { 
	        	Bundle bundle = data.getExtras();
	        	Message msg = new Message();
	        	msg.setData(bundle);
		        msg.what = Integer.valueOf(1);
		        mHandler.sendMessage(msg);
	        }  
	    }  
	}

}
