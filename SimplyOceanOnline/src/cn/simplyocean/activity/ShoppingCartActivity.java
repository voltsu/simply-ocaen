package cn.simplyocean.activity;

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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.simplyocean.adapter.ShoppingCartAdapter;
import cn.simplyocean.application.MyApplication;
import cn.simplyocean.asyncimageloader.FileUtils;
import cn.simplyocean.entities.ShoppingCartTable;
import cn.simplyocean.utils.ConstantValues;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class ShoppingCartActivity extends Activity {
	private ListView mListView;
	private ShoppingCartAdapter mShoppingCartAdapter;
	private List<ShoppingCartTable> productList = new ArrayList<ShoppingCartTable>();
	private TextView emptyCart;
	private Button shopNow;
	private RelativeLayout bottomLayout;
	private TextView total;
	private TextView mTitleTv;
	private Button goPayment;
	private Handler mHandler;
	private DbUtils db;
	private Context mContext;
	
	@SuppressLint("HandlerLeak") @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_shoppingcart);
		mContext = this;
		mTitleTv = (TextView) findViewById(R.id.title_tv);
		mTitleTv.setText(R.string.shopping_cart);
		emptyCart = (TextView)this.findViewById(R.id.empty_cart);
		shopNow = (Button)this.findViewById(R.id.goShopNow);
		bottomLayout = (RelativeLayout)this.findViewById(R.id.bottomgrid);
		mListView = (ListView) this.findViewById(R.id.produst_list);
		total = (TextView)this.findViewById(R.id.total_amount);
		goPayment = (Button)this.findViewById(R.id.goPayment);
		
		db = DbUtils.create(this);
		db.configAllowTransaction(true);
		db.configDebug(true);

		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0: //用户登录失败
					goLoginActivity();
					break;
				case 1: //用户已经登录
					new AlertDialog.Builder(mContext)
					.setTitle("")
					.setMessage("暂无支付接口，跳过付款页面，进入付款成功页面中。。。")  
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {                           
					     public void onClick(DialogInterface dialog, int which) {
							goCheckInventory();
							goPaymentActivity();
					     }
					  }
					)
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.dismiss();
						}
					})
					.show();
									
					break;
				case -1: //用户登录失败
					goLoginActivity();
					break;
				case -2: //用户登录失败
					goLoginActivity();
					break;
				case 2: //商品删除后更新总价
					updateView();
					break;
	            }
	          super.handleMessage(msg);
		 }
		};
		
		initView();
	}
	
	private void initView(){
		try {
			productList = db.findAll(Selector.from(ShoppingCartTable.class));
		} catch (DbException e) {
			e.printStackTrace();
		}
		
		if (productList == null || productList.size() == 0){
			initEmptyCartView();
		} else {
			new FileUtils(this);	
			mShoppingCartAdapter = new ShoppingCartAdapter(this, mListView, productList, mHandler);
			mListView.setAdapter(mShoppingCartAdapter);			
			initNotEmptyCartView();
		}
	}
	
	private void updateView(){
		try {
			productList = db.findAll(Selector.from(ShoppingCartTable.class));
		} catch (DbException e) {
			e.printStackTrace();
		}
		
		if (productList == null || productList.size() == 0){
			initEmptyCartView();
		} else {
			initNotEmptyCartView();
		}
	}
	
	private void initEmptyCartView(){
		emptyCart.setVisibility(View.VISIBLE);
		shopNow.setVisibility(View.VISIBLE);
		bottomLayout.setVisibility(View.GONE);
		shopNow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ShoppingCartActivity.this.finish();
			}
		});
	}
	
	private void initNotEmptyCartView(){
		emptyCart.setVisibility(View.GONE);
		shopNow.setVisibility(View.GONE);
		bottomLayout.setVisibility(View.VISIBLE);			
		float totalAmount = 0;
		for (int i = 0; i < productList.size(); i++){
			totalAmount += productList.get(i).getPprice() * productList.get(i).getQty();
		}
		total.setText("总价:" + Float.toString(totalAmount));		
		goPayment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SharedPreferences sharedPreferences = getSharedPreferences("simplyocean_userprofile", Context.MODE_PRIVATE);
				String savedUsername = sharedPreferences.getString("username", null);
				String savedPassword = sharedPreferences.getString("password", null);
				if(savedUsername != null && savedPassword != null){
					performLogin(savedUsername, savedPassword);
				} else {
					goLoginActivity();
				}
			}
		});
	}
	
	private void performLogin(String savedUsername, String savedPassword) {
		final String username = savedUsername;
		final String password = savedUsername;
		new Thread(new Runnable(){
			@Override
			public void run() {
				String result = null;
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
	
	private void goLoginActivity() {
		Intent intent = new Intent(ShoppingCartActivity.this, LoginActivity.class);
		startActivity(intent);
	}
	
	private void goCheckInventory() {	
		Log.e("MyApp", "正在进入检测库存");
		//数据库判断库存是否充足，锁行或锁表	
	}
	
	private void goPaymentActivity() {
		ArrayList<List<ShoppingCartTable>> parseList = new ArrayList<List<ShoppingCartTable>>(); //这个list用于在budnle中传递 需要传递的ArrayList<Object>
		parseList.add(productList);
		Intent intent = new Intent(ShoppingCartActivity.this, PaymentActivity.class);
		intent.putExtra("cartproduct", parseList);
		startActivityForResult(intent, 1);
		this.finish();
		Log.e("MyApp","正在进入付款页面");
	}
}
