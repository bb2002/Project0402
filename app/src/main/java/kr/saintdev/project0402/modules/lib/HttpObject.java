package kr.saintdev.project0402.modules.lib;

import org.json.JSONException;
import org.json.JSONObject;

import kr.saintdev.project0402.modules.workspace.work.implem.HttpWork;


/**
 * Created by 5252b on 2018-03-29.
 */

public class HttpObject {
    JSONObject header = null;   // 요청 결과에 대한 해더
    JSONObject body = null;     // 요청 결과에 대한 바디

    int requestResultCode = 0;         // 요청 결과 코드
    int responseResultCode = 0;        // 응답 코드

    public HttpObject(HttpWork work) {
        try {
            JSONObject obj = work.getResultObject().getJsonObject();
            this.header = obj.getJSONObject("header");

            if(!obj.isNull("body")) {
                this.body = obj.getJSONObject("body");
            }

            this.requestResultCode = work.getResultObject().getRequestResultCode();    // 클라이언트 요청 처리 결과 받기
            this.responseResultCode = this.header.getInt("code");               // 서버 처리 결과 코드 받기
        } catch(JSONException ex) {
            // JSON
            ex.printStackTrace();

            this.body = null;
            this.header = null;
            this.requestResultCode = HttpWork.HttpCodes.CLIENT_REQUEST_EXCEPTION;       // 클라이언트 요청 실패
            this.responseResultCode = HttpWork.HttpCodes.HTTP_CLIENT_NO_REQUEST;        // 서버는 요청 받지 못함
        }
    }

    public JSONObject getBody() {
        return body;
    }
    public JSONObject getHeader() { return header; };

    public int getReqeustResultCode() {
        return this.requestResultCode;      // 클라이언트 -> 서버 요청에 대한 결과 코드
    }

    public int getResponseResultCode() {
        return this.responseResultCode;     // 서버에서 처리에 대한 결과 코드
    }
}
