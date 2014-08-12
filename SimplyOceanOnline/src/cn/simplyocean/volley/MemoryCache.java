package cn.simplyocean.volley;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class MemoryCache extends LruCache<String, Bitmap> implements ImageCache {
	@SuppressWarnings("unused")
	private static final String TAG = "MemoryCache";

	public MemoryCache(int maxSize) {
		super(maxSize);
	}
	
	protected int sizeOf(String key, Bitmap value) {
		// TODO value.getByteCount();
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	public Bitmap getBitmap(String url) {
		Bitmap tbm = get(url);
		if(tbm != null){
			return tbm;
		}
		return null; //TODO local file
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}
}
