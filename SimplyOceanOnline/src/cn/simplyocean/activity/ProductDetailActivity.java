package cn.simplyocean.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.simplyocean.entities.ShoppingCartTable;
import cn.simplyocean.jsonmodel.Product;
import cn.simplyocean.utils.ConstantValues;
import cn.simplyocean.volley.VolleyQueue;

import com.android.volley.toolbox.NetworkImageView;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class ProductDetailActivity extends Activity implements OnClickListener{
	private NetworkImageView productImage;
	private TextView productId;
	private TextView productName;
	private TextView productPrice;
	private TextView productDescription;
	private TextView productInventory;
	private EditText productQty;
	private Button plus;
	private Button minus;
	private Button addToCart;
	private Product product;
	private ImageView goBack;
	
	protected void onCreate(Bundle savedInstanceState){
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_productdetail);
		productImage = (NetworkImageView)this.findViewById(R.id.product_image);
		productId = (TextView)this.findViewById(R.id.product_id);
		productName = (TextView)this.findViewById(R.id.product_name);
		productPrice = (TextView)this.findViewById(R.id.product_price);
		productDescription = (TextView)this.findViewById(R.id.product_description);
		productInventory = (TextView)this.findViewById(R.id.product_inventory);
		productQty = (EditText)this.findViewById(R.id.product_qty);
		plus = (Button)this.findViewById(R.id.plus);
		minus = (Button)this.findViewById(R.id.minus);
		addToCart = (Button)this.findViewById(R.id.add_to_cart);
		goBack = (ImageView)this.findViewById(R.id.back_img);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("productInfoBundle");
		this.product = (Product)bundle.getSerializable("productInfo");
		
		VolleyQueue volley = new VolleyQueue();
        volley.init(this);
		String mImageUrl = ConstantValues.PRODUCT_PICS_PATH + product.getProduct_pic();
		productImage.setDefaultImageResId(R.drawable.ic_empty);
        productImage.setImageUrl(mImageUrl, volley.getImageLoader());
		productId.setText("选择的商品ID为：" + Integer.toString(product.getPid()));
		productName.setText(product.getProduct_name());
		productPrice.setText(Float.toString(product.getProduct_price()));
		productDescription.setText(product.getProduct_description());
		productInventory.setText(Integer.toString(product.getProduct_inventory()));
		plus.setOnClickListener(this);
		minus.setOnClickListener(this);
		addToCart.setOnClickListener(this);
		goBack.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int quantity;
		switch (v.getId()) {
		case R.id.plus:
			quantity = Integer.parseInt(productQty.getText().toString());
			productQty.setText(Integer.toString(quantity + 1));
			break;
		case R.id.minus:
			quantity = Integer.parseInt(productQty.getText().toString());
			if(quantity != 1){
				productQty.setText(Integer.toString(quantity - 1));
			}
			break;
		case R.id.add_to_cart:
			try {
				quantity = Integer.parseInt(productQty.getText().toString());
				//加入数据库
				DbUtils db = DbUtils.create(this);
				db.configAllowTransaction(true);
				db.configDebug(true);
				ShoppingCartTable mProduct = null;	
				mProduct = db.findFirst(Selector.from(ShoppingCartTable.class).where("pid", "in", new int[]{product.getPid()}));
	            if (mProduct != null) {
	                mProduct.setQty(mProduct.getQty() + quantity);
	            } else {
	            	mProduct = new ShoppingCartTable();
	            	mProduct.setPid(product.getPid());
	            	mProduct.setPname(product.getProduct_name());
	            	mProduct.setPpic(ConstantValues.PRODUCT_PICS_PATH + product.getProduct_pic());
	            	mProduct.setPprice(product.getProduct_price());
	            	mProduct.setQty(quantity);
	            }
	            db.saveOrUpdate(mProduct);
	            Toast toast = Toast.makeText(this, "商品 " + product.getProduct_name() + " 已成功加入购物车！", Toast.LENGTH_SHORT);
	            toast.setGravity(Gravity.CENTER, 0, 0);
	            toast.show();
			} catch (DbException e) {
				e.printStackTrace();
			}
			break;
		case R.id.back_img:
			this.finish();
			break;
		}	
	}
}
