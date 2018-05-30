package kr.saintdev.project0402.models.tasks.http;

import android.content.Context;

import java.util.HashMap;
import java.util.Iterator;


import kr.saintdev.project0402.models.tasks.BackgroundWork;
import kr.saintdev.project0402.models.tasks.OnBackgroundWorkListener;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yuuki on 18. 4. 22.
 */

public class HttpRequester extends BackgroundWork<HttpResponseObject> {
    private String url = null;
    private HashMap<String, Object> param = null;

    public HttpRequester(String url, HashMap<String, Object> args, int requestCode, OnBackgroundWorkListener listener, Context context) {
        super(requestCode, listener);
        this.url = url;
        this.param = args;
    }

    @Override
    protected HttpResponseObject script() throws Exception {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder reqBuilder = new FormBody.Builder();

        // 인자 값이 있다면 넣어줍니다.
        if(param != null) {
            Iterator keyIterator = param.keySet().iterator();

            while (keyIterator.hasNext()) {
                String key = (String) keyIterator.next();
                Object value = param.get(key);

                if(value == null) {
                    reqBuilder.add(key, "null");
                } else {
                    reqBuilder.add(key, value.toString());
                }
            }
        }

        RequestBody reqBody = reqBuilder.build();
        Request request = new Request.Builder().url(this.url).post(reqBody).build();

        Response response = client.newCall(request).execute();
        String jsonScript = response.body().string();

        HttpResponseObject responseObj = new HttpResponseObject(jsonScript);

        // 응답 완료
        response.close();

        return responseObj;
    }
}
