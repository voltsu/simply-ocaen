package cn.simplyocean.application;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {

	private static final String TAG = "SimplyOceanApplication";
	private HttpClient httpClient;
	
	@Override
	public void onCreate() {
		super.onCreate();
		httpClient = createHttpClient();
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		shutdownHttpClient();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		shutdownHttpClient();
	}
	
	// ����HttpClient����
	private HttpClient createHttpClient() {
		Log.i(TAG, "Create HttpClient...");
		HttpParams params = new BasicHttpParams();
        // ��ʱ����
		/* �����ӳ���ȡ���ӵĳ�ʱʱ�� */
        ConnManagerParams.setTimeout(params, 1000);
        /* ���ӳ�ʱ */
        HttpConnectionParams.setConnectionTimeout(params, 2000);
        /* ����ʱ */
        HttpConnectionParams.setSoTimeout(params, 4000);
        
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params,
				HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
				params, schReg);
		return new DefaultHttpClient(conMgr, params);
	}

	// �ر�HttpClent
	private void shutdownHttpClient() {
		if (httpClient != null && httpClient.getConnectionManager() != null) {
			httpClient.getConnectionManager().shutdown();
		}
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}
}
