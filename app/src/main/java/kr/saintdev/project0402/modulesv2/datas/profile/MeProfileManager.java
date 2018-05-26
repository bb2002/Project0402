package kr.saintdev.project0402.modulesv2.datas.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import kr.saintdev.project0402.modulesv2.datas.database.DBHelper;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-11
 * 사용자 인증서를 관리합니다.
 */

public class MeProfileManager {
    private static MeProfileManager instance = null;
    private DBHelper helper = null;

    public static MeProfileManager getInstance(Context context) {
        if(MeProfileManager.instance == null) {
            MeProfileManager.instance = new MeProfileManager(context);
        }

        return MeProfileManager.instance;
    }

    private MeProfileManager(Context context) {
        // 레포지토리 오픈
        this.helper = new DBHelper(context);
    }

    public void setCertification(MeProfileObject profile) {
        String sql;

        if(getCertification() == null) {
            sql = "INSERT INTO narre_user_profile (kakao_id, kakao_nickname, kakao_profile, user_pin) VALUES(?,?,?,?)";
        } else {
            sql = "UPDATE narre_user_profile SET kakao_id = ?, kakao_nickname = ?, kakao_profile = ?, user_pin = ?";
        }

        SQLiteDatabase db = helper.getWriteDB();
        SQLiteStatement pst = db.compileStatement(sql);
        pst.bindString(1, profile.getKakaoId());
        pst.bindString(2, profile.getKakaoNickname());
        pst.bindString(3, profile.getKakaoProfile());
        pst.bindString(4, profile.getUserPin());

        pst.execute();
    }

    public MeProfileObject getCertification() {
        Cursor cs = helper.sendReadableQuery("SELECT * FROM narre_user_profile");

        if(cs.moveToNext()) {
            // Profile 을 생성합니다.
            MeProfileObject profile = new MeProfileObject(
                    cs.getString(0),
                    cs.getString(1),
                    cs.getString(2),
                    cs.getString(3)
            );

            return profile;
        } else {
            return null;
        }
    }

    public void clear() {
        helper.sendWriteableQuery("DELETE FROM narre_user_profile");
    }
}
