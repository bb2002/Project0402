package kr.saintdev.project0402.models.components.kakao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import kr.saintdev.project0402.models.datas.database.DBHelper;


/**
 * Created by 5252b on 2018-03-26.
 * Database 에 KakaoLogin 객체를 저장합니다.
 */

public class KakaoLoginManager {
    private static KakaoLoginManager kakaoLoginManager = null;

    private DBHelper dbHelper = null;

    public static KakaoLoginManager getInstance(Context context) {
        if(kakaoLoginManager == null) {
            kakaoLoginManager = new KakaoLoginManager(context);
        }

        return kakaoLoginManager;
    }

    private KakaoLoginManager(Context context) {
        this.dbHelper = new DBHelper(context);
        this.dbHelper.open();
    }

    public void setKakaoLoginObject(KakaoLoginObject loginObj) {
        String sql;
        if(getKakaoLoginObject() == null) {
            // 새로운 데이터를 넣습니다.
            sql = "INSERT INTO narre_user_profile (kakao_id, kakao_nickname, kakao_profile, user_pin) VALUES(?,?,?, null)";
        } else {
            // 기존 데이터를 업데이트 합니다.
            sql = "UPDATE narre_user_profile SET kakao_id = ?, kakao_nickname = ?, kakao_profile = ?";
        }

        SQLiteDatabase db = dbHelper.getWriteDB();
        SQLiteStatement pst = db.compileStatement(sql);
        pst.bindString(1, loginObj.getKakaoID());
        pst.bindString(2, loginObj.getKakaoNickname());
        pst.bindString(3, loginObj.getProfileUrl());
        pst.execute();
    }

    public KakaoLoginObject getKakaoLoginObject() {
        String sql = "SELECT * FROM narre_user_profile";

        Cursor cs = dbHelper.sendReadableQuery(sql);
        if(cs.moveToNext()) {
            KakaoLoginObject loginObject = new KakaoLoginObject(
                    cs.getString(0),
                    cs.getString(1),
                    cs.getString(2)
            );

            return loginObject;
        } else {
            return null;
        }
    }
}
