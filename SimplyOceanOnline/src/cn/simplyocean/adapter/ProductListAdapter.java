package cn.simplyocean.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import cn.simplyocean.activity.ProductDetailActivity;
import cn.simplyocean.activity.R;
import cn.simplyocean.entities.ShoppingCartTable;
import cn.simplyocean.jsonmodel.Product;
import cn.simplyocean.utils.ConstantValues;
import cn.simplyocean.volley.VolleyQueue;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class ProductListAdapter extends BaseAdapter {
    private Context context;  
    //private GridView mGridView;  
    //private ImageDownLoader mImageDownLoader;      
    private LayoutInflater mLayoutInflater = null;
    private ArrayList<String> imageThumbUrls = new ArrayList<String>();  
    private List<Product> productList;
    private ImageLoader mImageLoader;
    //private List<ImageContainer> icList = new ArrayList<ImageContainer>();
    
    public ProductListAdapter(Context context, GridView mGridView, List<Product> productList){  
        this.context = context;  
		for (int i = 0; i < productList.size(); i++){
			this.imageThumbUrls.add(ConstantValues.PRODUCT_PICS_PATH + productList.get(i).getProduct_pic());
		}              
       // this.mImageDownLoader = new ImageDownLoader(context);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.productList = productList;
        VolleyQueue volley = new VolleyQueue();
        volley.init(context);
        mImageLoader = volley.getImageLoader();
    }  

        
    @Override  
    public int getCount() {  
        return imageThumbUrls.size();  
    }  
  
    @Override  
    public Object getItem(int position) {  
        return imageThumbUrls.get(position);  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {   
        ViewHolder viewHolder = null;
        final String mImageUrl = imageThumbUrls.get(position); 
        
        if(convertView == null){ 
        	viewHolder = new ViewHolder();
        	convertView = mLayoutInflater.inflate(R.layout.productlist_item, null);
        	viewHolder.productImage = (NetworkImageView)convertView.findViewById(R.id.product_image);
        	viewHolder.productName = (TextView)convertView.findViewById(R.id.product_name);
        	viewHolder.productQty = (EditText)convertView.findViewById(R.id.product_qty);       	
        	viewHolder.goDetails = (Button)convertView.findViewById(R.id.goDetails);
        	viewHolder.goPurchase = (Button)convertView.findViewById(R.id.goPurchase);
        	viewHolder.minus = (Button)convertView.findViewById(R.id.minus);
        	viewHolder.plus = (Button)convertView.findViewById(R.id.plus);
        	viewHolder.productImage.setTag(mImageUrl);
            convertView.setTag(viewHolder);  
        }else{  
        	viewHolder = (ViewHolder)convertView.getTag();
        }    
        

        final int cursor = position;
        final View myView = convertView;
        viewHolder.productImage.setDefaultImageResId(R.drawable.ic_empty);
        viewHolder.productImage.setImageUrl(mImageUrl, mImageLoader);
        viewHolder.productName.setText(productList.get(position).getProduct_name());
               
    	viewHolder.minus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ViewHolder viewHolder = (ViewHolder)myView.getTag();
				int quantity = Integer.parseInt(viewHolder.productQty.getText().toString());
				if(quantity == 1){
					viewHolder.productQty.setText("1");
				} else {
					viewHolder.productQty.setText(Integer.toString(quantity - 1));
				}
			}
		});

    	viewHolder.plus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ViewHolder viewHolder = (ViewHolder)myView.getTag();
				int quantity = Integer.parseInt(viewHolder.productQty.getText().toString());
				viewHolder.productQty.setText(Integer.toString(quantity + 1));
			}
		});
    	
    	viewHolder.goPurchase.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					ViewHolder myViewHolder = (ViewHolder)myView.getTag();
					int quantity = Integer.parseInt(myViewHolder.productQty.getText().toString());
					//加入数据库
					DbUtils db = DbUtils.create(context);
					db.configAllowTransaction(true);
					db.configDebug(true);
					ShoppingCartTable mProduct = null;	
					mProduct = db.findFirst(Selector.from(ShoppingCartTable.class).where("pid", "in", new int[]{productList.get(cursor).getPid()}));
		            if (mProduct != null) {
		                mProduct.setQty(mProduct.getQty() + quantity);
		            } else {
		            	mProduct = new ShoppingCartTable();
		            	mProduct.setPid(productList.get(cursor).getPid());
		            	mProduct.setPname(productList.get(cursor).getProduct_name());
		            	mProduct.setPpic(ConstantValues.PRODUCT_PICS_PATH + productList.get(cursor).getProduct_pic());
		            	mProduct.setPprice(productList.get(cursor).getProduct_price());
		            	mProduct.setQty(quantity);
		            }
		            db.saveOrUpdate(mProduct);
		            Toast toast = Toast.makeText(context, "商品 " + productList.get(cursor).getProduct_name() + " 已成功加入购物车！", Toast.LENGTH_SHORT);
		            toast.setGravity(Gravity.CENTER, 0, 0);
		            toast.show();
				} catch (DbException e) {
					e.printStackTrace();
				}
			}
		});
    	
    	viewHolder.goDetails.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Bundle bundle = new Bundle();
				bundle.putSerializable("productInfo", (Serializable) productList.get(cursor));				
				Intent intent = new Intent(context, ProductDetailActivity.class);
				intent.putExtra("productInfoBundle", bundle);
				context.startActivity(intent);
			}
		});
               
        return convertView;  
    }  
    
    
    public static class ViewHolder{
    	public NetworkImageView productImage;
    	public TextView productName;
    	public EditText productQty;
    	public Button goDetails;
    	public Button goPurchase;
    	public Button minus;
    	public Button plus;
    }
}
