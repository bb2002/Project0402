package kr.saintdev.project0402.modules.workspace.work.implem;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import kr.saintdev.project0402.modules.workspace.work.Work;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 5252b on 2018-03-23.
 */

public class HttpWork extends Work<HttpWork.HttpResponse> {
    private String resUrl = null;       // 요청 URL
    private HashMap<String, Object> args = null;    // 인자값

    public HttpWork(String resourceUrl, HashMap<String, Object> args) {
        this.resUrl = resourceUrl;
        this.args = args;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder reqBuilder = new FormBody.Builder();

        // 인자 값이 있다면 넣어줍니다.
        if(args != null) {
            Iterator keyIterator = args.keySet().iterator();

            while (keyIterator.hasNext()) {
                String key = (String) keyIterator.next();
                Object value = args.get(key);
                reqBuilder.add(key, value.toString());
            }
        }

        RequestBody reqBody = reqBuilder.build();
        Request request = new Request.Builder().url(this.resUrl).post(reqBody).build();

        // http 를 통해 서버에 요청합니다.
        HttpResponse httpResponseObj = null;

        try {
            Response response = client.newCall(request).execute();
            String jsonScript = response.body().string();

            JSONObject responseObj = new JSONObject(jsonScript);
            httpResponseObj = new HttpResponse(responseObj, HttpCodes.HTTP_OK);

            // 응답 완료
            response.close();
        } catch(IOException iex) {
            // http 요청 실패
            httpResponseObj = new HttpResponse(getCustomResponse(HttpCodes.HTTP_CLIENT_NO_REQUEST, "IOException " + iex.getMessage()), HttpCodes.CLIENT_REQUEST_EXCEPTION);
        } catch(JSONException jex) {
            // HTTP 요청 오류 등의 이유
            httpResponseObj = new HttpResponse(getCustomResponse(HttpCodes.HTTP_CLIENT_NO_REQUEST, "JSON Exception " + jex.getMessage()), HttpCodes.CLIENT_REQUEST_EXCEPTION);
        }

        super.setReturnObject(httpResponseObj);
    }


    // http 응답 객체
    public class HttpResponse {
        private JSONObject responseObject = null;   // 응답 객체
        private int responseCode = 0;       // 응답 결과

        public HttpResponse(JSONObject resp, int code) {
            this.responseObject = resp;
            this.responseCode = code;
        }

        // 결과 객체
        public JSONObject getJsonObject() {
            return responseObject;
        }

        // 서버에게 요청을 성공 했읍니까?
        public int getRequestResultCode() {
            return responseCode;
        }

        // 서버 응답 결고
        public int getResponseCode() {
            try {
                JSONObject header = this.responseObject.getJSONObject("header");
                return header.getInt("code");
            } catch(JSONException jex) {
                jex.printStackTrace();
            }

            return HttpCodes.CLIENT_REQUEST_EXCEPTION;
        }
    }

    // http 응답 코드
    public interface HttpCodes {
        int HTTP_OK = 200;      // 요청 성공

        int HTTP_CLIENT_MISSING_ARGUMENT = 400;     // 인자 값이 부족하다.
        int HTTP_CLIENT_AUTH_ERROR = 401;           // 해당 pin 또는 kakaoid 값이 잘못되었다.
        int HTTP_CLIENT_NO_REQUEST = 0;             // 클라이언트가 요청을 하지 않았다. (대부분 안드로이드 오류)
        int HTTP_INTERNAL_SERVER_ERROR = 500;       // 내부 서버 오류

        int CLIENT_REQUEST_EXCEPTION = 600;         // 클라이언트에서 요청 도중 오류가 발생했다.
        int CLIENT_NOTCONNECT_INTERNET = 601;       // 디바이스가 인터넷에 연결되어 있지 않다.
    }

    public static JSONObject getCustomResponse(int code, String msg) {
        JSONObject resp = new JSONObject();

        try {
            JSONObject header = new JSONObject();
            header.put("code", code);
            header.put("msg", msg);

            resp.put("header", header);
        } catch(JSONException jex){}

        return resp;
    }
}
