package kr.saintdev.project0402.models.tasks.tts;

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

import kr.saintdev.project0402.models.datas.constant.APIKeys;
import kr.saintdev.project0402.models.datas.constant.InternetConst;
import kr.saintdev.project0402.models.tasks.BackgroundWork;
import kr.saintdev.project0402.models.tasks.OnBackgroundWorkListener;


/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-15
 */

public class TTS extends BackgroundWork<TTSObject> {
    private String sentence = null;     // 번역 대상의 문자
    private Context context = null;

    public TTS(String sentence, int requestCode, OnBackgroundWorkListener listener, Context context) {
        super(requestCode, listener);
        this.sentence = sentence;
        this.context = context;
    }

    @Override
    protected TTSObject script() throws Exception {
        String text = URLEncoder.encode(this.sentence, "UTF-8"); // 13자
        String apiURL = InternetConst.TTS_API;

        URL url = new URL(apiURL);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", APIKeys.NCLOUD_ID);
        con.setRequestProperty("X-NCP-APIGW-API-KEY", APIKeys.NCLOUD_SEC);

        // post request
        String postParams = "speaker=mijin&speed=0&text=" + text;
        con.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        BufferedReader br;

        if (responseCode == 200) {
            // TTS 변환에 성공함.
            InputStream is = con.getInputStream();
            int read = 0;
            byte[] bytes = new byte[1024];

            // 임의의 이름으로 저장한다.
            File f = new File(context.getCacheDir(), System.currentTimeMillis() + ".mp3");

            if (f.createNewFile()) {
                // 파일 생성 성공
                // mp3 파일을 받아옵니다.
                OutputStream outputStream = new FileOutputStream(f);
                while ((read = is.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                is.close();

                TTSObject ttsObj = new TTSObject(this.sentence, f.getAbsoluteFile());
                return ttsObj;
            } else {
                // 파일 생성 실패
                throw new Exception("Cannot create file!");
            }

        } else {  // 에러 발생
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            String msg = response.toString();
            throw new Exception(msg);
        }
    }
}
