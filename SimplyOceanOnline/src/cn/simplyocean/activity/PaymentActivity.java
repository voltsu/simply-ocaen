package cn.simplyocean.activity;

import java.io.FileOutputStream;
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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import cn.simplyocean.application.MyApplication;
import cn.simplyocean.asyncimageloader.FileUtils;
import cn.simplyocean.entities.ShoppingCartTable;
import cn.simplyocean.qrcode.QRCodeUtils;
import cn.simplyocean.utils.ConstantValues;

import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

public class PaymentActivity extends Activity {
	
	private Handler mHandler;
	private List<ShoppingCartTable> cartProductList = new ArrayList<ShoppingCartTable>();
	private TextView oidText;
	private ImageView qrcodeImage;
	private DbUtils db;
	private ImageView goBack;
	
	@SuppressLint("HandlerLeak") @SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_payment);
		Intent intent = getIntent();
		ArrayList<Parcelable> parseList = intent.getParcelableArrayListExtra("cartproduct");		
		cartProductList = (List<ShoppingCartTable>) parseList.get(0);
		
		oidText = (TextView)this.findViewById(R.id.order_number);
		qrcodeImage = (ImageView)this.findViewById(R.id.qrcodeImage);
		goBack = (ImageView)this.findViewById(R.id.back_img);
		goBack.setVisibility(View.VISIBLE);
		goBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				Bundle bundle = new Bundle();
				switch (msg.what) {
				case 1: //getUid成功，并开始创建order
					bundle = msg.getData();
					String uid = bundle.getString("uid");
					createOrder(uid);
					emptyShoppingCart();
					break;
				case 2: //createOrder成功获取oid
					bundle = msg.getData();
					String oid = bundle.getString("oid");
					oidText.setText(oid.toString());
					createQRCode(oid);
					break;
	            }
	          super.handleMessage(msg);
		 }
		};	
		getUid();
	}
	
	private void getUid() {
		SharedPreferences sharedPreferences = getSharedPreferences("simplyocean_userprofile", Context.MODE_PRIVATE);
		final String savedUsername = sharedPreferences.getString("username", null);
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
					param = new BasicNameValuePair("username", savedUsername);
					paramList.add(param);
					post.setEntity(new UrlEncodedFormEntity(paramList, HTTP.UTF_8));
					post.setURI(new URI(ConstantValues.SERVERLET_URL + "user/getUid/"));
					HttpResponse response = client.execute(post);
					result = EntityUtils.toString(response.getEntity());	
					
			        Message msg = new Message();
			        Bundle bundle = new Bundle();
			        bundle.putString("uid", result);
			        msg.setData(bundle);
			        msg.what = 1;
			        mHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}}).start();
	}
	
	private void createOrder(String uid) {
		final String post_uid = uid;
		Gson gson = new Gson();
		final String json = gson.toJson(cartProductList);
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
					param = new BasicNameValuePair("uid", post_uid);
					paramList.add(param);
					param = new BasicNameValuePair("json", json);
					paramList.add(param);
					post.setEntity(new UrlEncodedFormEntity(paramList, HTTP.UTF_8));
					post.setURI(new URI(ConstantValues.SERVERLET_URL + "order/createOrder/"));
					HttpResponse response = client.execute(post);
					result = EntityUtils.toString(response.getEntity());
					
					Message msg = new Message();
			        Bundle bundle = new Bundle();
			        bundle.putString("oid", result);
			        msg.setData(bundle);
			        msg.what = 2;
			        mHandler.sendMessage(msg);
			        
				} catch (Exception e) {
					e.printStackTrace();
				}
			}}).start();
	}
	
	private void emptyShoppingCart (){
		try {
			db = DbUtils.create(this);
			db.configAllowTransaction(true);
			db.configDebug(true);
			db.dropTable(ShoppingCartTable.class);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createQRCode(String oid){
        final int desiredWidth = 120;
        final int desiredHeight = 120;
        final String imageFileName = "qr"+ oid +".png";
        FileOutputStream fos = null;
        Bitmap bitmap = null;
        try {//生成二维码图像
            bitmap = QRCodeUtils.Create2DCode(oid, desiredWidth, desiredHeight);
            if(null != bitmap) {//将二维码图像保存到文件
//
//                File file = new File(Environment.getExternalStorageDirectory(), imageFileName);
//                fos = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 0, fos);
            	FileUtils file = new FileUtils(getApplicationContext());
            	file.savaBitmap(imageFileName, bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != fos) {
                try {
                    fos.close();
                } catch (Exception e) {}
            }
        }

        //显示QRCode
        if(null != bitmap) {
        	qrcodeImage.setImageBitmap(bitmap);
        	qrcodeImage.setScaleType(ScaleType.FIT_CENTER);
        }	
	}
}
