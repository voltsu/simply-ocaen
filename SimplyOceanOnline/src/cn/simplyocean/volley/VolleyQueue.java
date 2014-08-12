package cn.simplyocean.volley;

import java.lang.ref.SoftReference;
import java.util.HashSet;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;

public class VolleyQueue {
	private static RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;
    public static LruCache<String, Bitmap> imagesCache = null;
    private static int DEFAULT_MEM_CACHE_SIZE = 1024 * 5; // 5MB
    public static HashSet<SoftReference<Bitmap>> mReusableBitmaps = new HashSet<SoftReference<Bitmap>>(); //存放图片的软引用

    public VolleyQueue() {
    
    }
 
    /**
     * 初始化我们的请求队列。这个地方有一个BitmapLruCache，这个是在后面做图片加载的时候会提到的图片缓存策略
     * 
     * @param context
     */
    public void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
        //int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        // Use 1/8th of the available memory for this memory cache.
        //int cacheSize = 1024 * 1024 * memClass / 8;
        
        imagesCache = new LruCache<String, Bitmap>(DEFAULT_MEM_CACHE_SIZE) {
            @Override
            protected void entryRemoved(boolean evicted, String key,
                    Bitmap oldValue, Bitmap newValue) {
                // TODO Auto-generated method stub
                //将超出设置的最大内存限制的图片放入软引用系统会自动回收
                mReusableBitmaps.add(new SoftReference<Bitmap>(oldValue));
            }
            //重写这个方法计算图片的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // TODO Auto-generated method stub
                final int bitmapSize = getBitmapSize(value) / 1024;
                return bitmapSize == 0 ? 1 : bitmapSize;
            }
        };
           
        mImageLoader = new ImageLoader(mRequestQueue, new ImageCache() {
			@Override
			public Bitmap getBitmap(String url) {
				return imagesCache.get(url);
			}

			@Override
			public void putBitmap(String url, Bitmap bitmap) {
				imagesCache.put(url, bitmap);
				
			}});
    }
 
    public RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }
 
    public ImageLoader getImageLoader() {
        if (mImageLoader != null) {
            return mImageLoader;
        } else {
            throw new IllegalStateException("ImageLoader not initialized");
        }
    }
    
    public static int getBitmapSize(Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }
}
