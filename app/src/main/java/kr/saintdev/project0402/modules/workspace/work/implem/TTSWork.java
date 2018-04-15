package kr.saintdev.project0402.modules.workspace.work.implem;

import android.content.Context;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.UUID;

import kr.saintdev.project0402.modules.defines.APIKeys;
import kr.saintdev.project0402.modules.workspace.work.Work;

/**
 * Created by yuuki on 18. 4. 15.
 */

public class TTSWork extends Work<File> {
    private String sentence = null;
    private Context context = null;

    public TTSWork(Context context, String sentence) {
        this.sentence = sentence;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            String text = URLEncoder.encode(this.sentence, "UTF-8"); // 13자
            String apiURL = "https://openapi.naver.com/v1/voice/tts.bin";

            // Create request.
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id", APIKeys.CLIENT_ID);
            con.setRequestProperty("X-Naver-Client-Secret", APIKeys.CLIENT_SEC);

            // post request
            String postParams = "speaker=mijin&speed=0&text=" + text;       // 한국 여성, 속도 기본값

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();

            if(responseCode == HttpWork.HttpCodes.HTTP_OK) {
                // 정상 호출
                InputStream is = con.getInputStream();
                int read = 0;
                byte[] bytes = new byte[1024];

                // 랜덤한 이름으로 mp3 파일 생성
                String tempname = UUID.randomUUID().toString().replaceAll("-","");
                File f = new File(context.getCacheDir(), tempname + ".mp3");

                if(f.createNewFile()) {
                    // 성공, 파일에 데이터를 씁니다.
                    OutputStream outputStream = new FileOutputStream(f);
                    while ((read = is.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }

                    is.close();
                } else {
                    // 파일을 생성 할 수 없습니다.
                    throw new Exception("Can not create file.");
                }

                // 결과를 제공한다.
                super.setReturnObject(f);
            } else {
                // 에러 발생
                super.setReturnObject(null);
            }
        } catch (Exception e) {
            super.setReturnObject(null);
            e.printStackTrace();
        }
    }
}
