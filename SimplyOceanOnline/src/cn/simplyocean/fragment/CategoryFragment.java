package cn.simplyocean.fragment;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.simplyocean.activity.ProductListActivity;
import cn.simplyocean.activity.R;
import cn.simplyocean.application.MyApplication;
import cn.simplyocean.utils.ConstantValues;

public class CategoryFragment extends BaseFragment {
	private static final String TAG = "CategoryFragment";
	private TextView mTitleTv;
	private ImageButton seafoodGrid;
	private ImageButton beefGrid;
	private ImageButton wineGrid;
	private ImageButton sauceGrid;
	private ProgressDialog loadingDialog;
	private Handler mHandler;
	
	public static CategoryFragment newInstance() {
		CategoryFragment categoryFragment = new CategoryFragment();
		return categoryFragment;
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
		View view = inflater.inflate(R.layout.fragment_category, container, false);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mTitleTv = (TextView) view.findViewById(R.id.title_tv);
		mTitleTv.setText(R.string.category);
		seafoodGrid = (ImageButton) view.findViewById(R.id.seafoodGrid);
		beefGrid = (ImageButton) view.findViewById(R.id.beefGrid);
		wineGrid = (ImageButton) view.findViewById(R.id.wineGrid);
		sauceGrid = (ImageButton) view.findViewById(R.id.sauceGrid);
	}

	@SuppressLint("HandlerLeak") @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				Toast toast = null;
				switch (msg.what){
				case 1:
					toast = Toast.makeText(getActivity().getApplicationContext(), "服务器连接超时，请稍后再试！", Toast.LENGTH_SHORT);
					break;
				}
				loadingDialog.dismiss();
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		};
		
		seafoodGrid.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				goProductList(ConstantValues.SEAFOOD_CATEGORY_ID);
			}
		});

		beefGrid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				goProductList(ConstantValues.BEEF_CATEGORY_ID);
			}
		});

		wineGrid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				goProductList(ConstantValues.WINE_CATEGORY_ID);
			}
		});
	
		sauceGrid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				goProductList(ConstantValues.SAUCE_CATEGORY_ID);
			}
		});
	}
	
	private void goProductList(final String cid){
		loadingDialog = ProgressDialog.show(getActivity(), "" , "正在获取商品数据中，请稍候 ... ", true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String jsonString = fetchProductList(cid);
				if(jsonString != null){
					Intent intent = new Intent(getActivity(), ProductListActivity.class);
					intent.putExtra("categoryId", cid);
					intent.putExtra("productListJsonString", jsonString);
					startActivity(intent);
					loadingDialog.dismiss();
				}
			}
		}).start();	
	}
	
	private String fetchProductList(String cid){
		String result = null;
		try {
			//创建HttpClient，根据商品分类ID获取商品列表
			MyApplication app = (MyApplication)getActivity().getApplication();
			HttpClient client = app.getHttpClient();
			HttpGet get = new HttpGet();
			get.setURI(new URI(ConstantValues.SERVERLET_URL + "product/productlist?product_type=" + cid));
			HttpResponse response = client.execute(get);
			result = EntityUtils.toString(response.getEntity());
			//Log.e("MyApp", result);
			//将商品列表存入SharedPreferences
			SharedPreferences sharedPreferences = getActivity().getSharedPreferences("simplyocean_productlist", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("productlist_" + cid, result);
			editor.commit();
		} catch(ConnectionPoolTimeoutException e) {
			networkError();
			Log.e("MyApp", "网络连接超时");
		} catch (ConnectTimeoutException e) {
			networkError();
			Log.e("MyApp", "服务器连接超时");
		} catch (SocketTimeoutException e) {
			networkError();
			Log.e("MyApp", "获取数据超时");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private void networkError(){
		Message msg = new Message();
		msg.what = 1;
		mHandler.sendMessage(msg);
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

}
