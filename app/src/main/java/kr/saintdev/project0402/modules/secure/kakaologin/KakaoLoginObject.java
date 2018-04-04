package kr.saintdev.project0402.modules.secure.kakaologin;

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

/**
 * Created by 5252b on 2018-03-26.
 */

public class KakaoLoginObject {
    String nickname = null;     // 카카오 닉네임
    String profileUrl = null;      // 프로필 이미지
    long uuid = 0;         // 고유번호

    public KakaoLoginObject() {}

    public KakaoLoginObject(String nick, String pro) {
        this.nickname = nick;
        this.profileUrl = pro;
    }

    public void initSelf() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                if(errorResult != null) {

                }
            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(UserProfile result) {
                nickname = result.getNickname();
                uuid = result.getId();
                profileUrl = result.getProfileImagePath();
            }
        });
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public long getUuid() {
        return uuid;
    }
}
