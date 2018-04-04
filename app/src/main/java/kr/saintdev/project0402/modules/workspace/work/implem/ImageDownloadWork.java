package kr.saintdev.project0402.modules.workspace.work.implem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.saintdev.project0402.modules.workspace.work.Work;


/**
 * Created by 5252b on 2018-03-23.
 * 이미지 다운로드 작업
 */

public class ImageDownloadWork extends Work<Bitmap> {
    String resource = null;

    public ImageDownloadWork(String resource) {
        this.resource = resource;
    }

    @Override
    public void run() {
        Bitmap bitmap = null;

        try {
            URL url = new URL(resource);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);  // 서버 응답 수신
            conn.connect();

            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        super.setReturnObject(bitmap);
    }
}
