package cn.simplyocean.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import cn.simplyocean.adapter.ProductListAdapter;
import cn.simplyocean.asyncimageloader.FileUtils;
import cn.simplyocean.jsonmodel.Product;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ProductListActivity extends Activity implements View.OnClickListener{
	private GridView productGridView;
	private ProductListAdapter productListAdapter;
	private Button cartButton;
	private ImageView goBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_productlist);
		
		Intent intent = getIntent();
		//String cid = intent.getStringExtra("categoryId");
		String jsonResult = intent.getStringExtra("productListJsonString");

		List<Product> productList = new ArrayList<Product>();
		Gson gson = new Gson();
		productList = gson.fromJson(jsonResult, new TypeToken<List<Product>>() {}.getType());

		new FileUtils(this);
		productGridView = (GridView) this.findViewById(R.id.productGridView);
		productListAdapter = new ProductListAdapter(this, productGridView, productList);
		productGridView.setAdapter(productListAdapter);

		cartButton = (Button) this.findViewById(R.id.goCart);
		goBack = (ImageView) this.findViewById(R.id.back_img);
		goBack.setVisibility(View.VISIBLE);
		cartButton.setOnClickListener(this);
		goBack.setOnClickListener(this);
//		cartButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				Intent intent = new Intent(ProductListActivity.this,
//						ShoppingCartActivity.class);
//				startActivity(intent);
//			}
//		});
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.goCart:
			Intent intent = new Intent(ProductListActivity.this,
					ShoppingCartActivity.class);
			startActivity(intent);
			break;
		case R.id.back_img:
			this.finish();
			break;
		}
	}
}
