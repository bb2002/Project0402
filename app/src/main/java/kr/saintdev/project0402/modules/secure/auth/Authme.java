package kr.saintdev.project0402.modules.secure.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 인증 관리 도구
 */

public class Authme {
    private static Authme instance = null;
    private SharedPreferences prep = null;
    private SharedPreferences.Editor prepEditor = null;

    private static final String REPO = "screentran.repo";

    public static Authme getInstance(Context context) {
        if(Authme.instance == null) {
            Authme.instance = new Authme(context);
        }

        return Authme.instance;
    }

    public Authme() {}

    private Authme(Context context) {
        // 레포지토리 오픈
        this.prepEditor = context.getSharedPreferences(REPO, Context.MODE_PRIVATE).edit();
        this.prep = context.getSharedPreferences(REPO, Context.MODE_PRIVATE);
    }

    public void setPin(String pin) {
        prepEditor.putString("user_pin", pin);
        prepEditor.commit();
    }

    public String getPin() {
        String pin = prep.getString("user_pin", null);
        return pin;
    }

    // SharedPrep 에서 바로 읽어 온다.
    public UserInfo getUserInfo() {

        UserInfo info = new UserInfo(
            this.prep.getString("user_nickname", null),
            this.prep.getString("user_profile", null)
        );

        return info;
    }

    // SharedPrep 에 바로 적용한다.
    public void setUserInfo(UserInfo info) {
        String nickname = null;
        String userProfile = null;

        if(info != null) {
            nickname = info.getUserNickname();
            userProfile = info.getUserProfile();
        }

        prepEditor.putString("user_nickname", nickname);
        prepEditor.putString("user_profile", userProfile);
        prepEditor.apply();
    }

    public class UserInfo {
        String userNickname = null;
        String userProfile = null;

        public UserInfo(String userNickname, String userProfile) {
            this.userNickname = userNickname;
            this.userProfile = userProfile;
        }

        public String getUserNickname() {
            return userNickname;
        }

        public String getUserProfile() {
            return userProfile;
        }
    }
}
