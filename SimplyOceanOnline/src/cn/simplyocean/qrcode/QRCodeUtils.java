package cn.simplyocean.qrcode;

import java.util.Hashtable;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeUtils {
	public static Bitmap Create2DCode(String str, int picWidth, int picHeight) throws WriterException {  
        // ���ɶ�ά����,����ʱָ����С,��Ҫ������ͼƬ�Ժ��ٽ�������,������ģ������ʶ��ʧ��  
		Hashtable<EncodeHintType, ErrorCorrectionLevel> hints = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();  
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);  
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, picWidth, picHeight, hints);  
        int width = matrix.getWidth();  
        int height = matrix.getHeight();  
        // ��ά����תΪһά��������,Ҳ����һֱ��������  
        int[] pixels = new int[width * height];  
        for (int y = 0; y < height; y++) {  
            for (int x = 0; x < width; x++) {  
                if (matrix.get(x, y)) {  
                    pixels[y * width + x] = 0xff000000;  
                } else {  
                    pixels[y * width + x] = 0xffffffff;  
                }  
  
            }  
        }  
  
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);  
        // ͨ��������������bitmap,����ο�api  
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
        return bitmap;  
    }
}
