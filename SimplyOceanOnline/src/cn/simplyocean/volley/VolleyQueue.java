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
    public static HashSet<SoftReference<Bitmap>> mReusableBitmaps = new HashSet<SoftReference<Bitmap>>(); //���ͼƬ��������

    public VolleyQueue() {
    
    }
 
    /**
     * ��ʼ�����ǵ�������С�����ط���һ��BitmapLruCache��������ں�����ͼƬ���ص�ʱ����ᵽ��ͼƬ�������
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
                //���������õ�����ڴ����Ƶ�ͼƬ����������ϵͳ���Զ�����
                mReusableBitmaps.add(new SoftReference<Bitmap>(oldValue));
            }
            //��д�����������ͼƬ�Ĵ�С
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
