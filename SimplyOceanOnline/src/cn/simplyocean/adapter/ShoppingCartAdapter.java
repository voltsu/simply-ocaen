package cn.simplyocean.adapter;


import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import cn.simplyocean.activity.R;
import cn.simplyocean.entities.ShoppingCartTable;
import cn.simplyocean.volley.VolleyQueue;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

public class ShoppingCartAdapter extends BaseAdapter {
	private Context mContext;
	private ImageLoader mImageLoader; 
    private LayoutInflater mLayoutInflater = null;
	private List<ShoppingCartTable> productList;
	private Handler mHandle;
	
	public ShoppingCartAdapter(Context mContext, ListView mListView, List<ShoppingCartTable> productList, Handler mHandler) {
		this.mContext = mContext;
		this.productList = productList;
		VolleyQueue volley = new VolleyQueue();
        volley.init(mContext);
		mImageLoader = volley.getImageLoader();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mHandle = mHandler;
	}

	@Override
	public int getCount() {
		return productList.size();
	}

	@Override
	public Object getItem(int position) {
		return productList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
 
		final String mImageUrl = productList.get(position).getPpic();
		final int cursor = position;
		ViewHolder viewHolder = null;
		if(convertView == null){ 
        	viewHolder = new ViewHolder();
        	convertView = mLayoutInflater.inflate(R.layout.shoppingcart_item, null);
        	viewHolder.productImage = (NetworkImageView)convertView.findViewById(R.id.product_image);
        	viewHolder.productName = (TextView)convertView.findViewById(R.id.product_name);
        	viewHolder.productQty = (EditText)convertView.findViewById(R.id.product_qty);
        	viewHolder.goDelete = (Button)convertView.findViewById(R.id.goDelete);    
            convertView.setTag(viewHolder);  
        }else{  
        	viewHolder = (ViewHolder)convertView.getTag();
        }          

		viewHolder.productImage.setImageUrl(mImageUrl, mImageLoader);
		viewHolder.productQty.setText(Integer.toString(productList.get(position).getQty()));
    	viewHolder.productName.setText(productList.get(position).getPname());   	
    	viewHolder.goDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					DbUtils db = DbUtils.create(mContext);
					db.configAllowTransaction(true);
					db.configDebug(true);
					String sql = "DELETE FROM shoppingcart WHERE pid = " + Integer.toString(productList.get(cursor).getPid()) + ";";
					db.execNonQuery(sql);
					Message msg = new Message();
					msg.what = 2;
					mHandle.sendMessage(msg);
					productList.remove(cursor);
					notifyDataSetChanged();
				} catch (DbException e) {
					e.printStackTrace();
				}
			}
		});
		 
		return convertView;
	}
	
    public class ViewHolder{
    	public NetworkImageView productImage;
    	public TextView productName;
    	public EditText productQty;
    	public Button goDelete;
    }
}
