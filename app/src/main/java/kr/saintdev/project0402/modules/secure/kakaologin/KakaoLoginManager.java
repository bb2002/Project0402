package kr.saintdev.project0402.modules.secure.kakaologin;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;

/**
 * Created by 5252b on 2018-03-26.
 */

public class KakaoLoginManager {
    private static KakaoLoginObject loginObject = null;

    public static void isLoginCallback(MeResponseCallback callback) {
        UserManagement.requestMe(callback);
    }

    public static KakaoLoginObject getKakaoLoginObject() {
        if(loginObject == null) {
            loginObject = new KakaoLoginObject();
            loginObject.initSelf();
        }

        return loginObject;
    }
}
