package kr.saintdev.project0402.models.components.kakao;

import com.kakao.usermgmt.response.model.UserProfile;

import kr.saintdev.project0402.models.datas.constant.InternetConst;

/**
 * Created by 5252b on 2018-03-26.
 * 카카오 로그인을 표현하는 개체
 */

public class KakaoLoginObject {
    private String kakaoNickname = null;    // 카카오 닉네임
    private String profileUrl = null;       // 프로필 사진 주소
    private String kakaoID = null;        // 카카오 UUID

    public KakaoLoginObject(UserProfile profile) {
        this.kakaoNickname = profile.getNickname();

        String pUrl = profile.getProfileImagePath();
        this.profileUrl = (pUrl == null) ? InternetConst.KAKAO_PROFILE_DEFAULT : pUrl;
        this.kakaoID = profile.getId() + "";
    }

    public KakaoLoginObject(String kakaoID, String kakaoNick, String profileUrl) {
        this.kakaoNickname = kakaoNick;
        this.profileUrl = profileUrl;
        this.kakaoID = kakaoID;
    }


    public String getKakaoNickname() {
        return kakaoNickname;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getKakaoID() {
        return kakaoID;
    }

    public boolean isValid() {
        if(kakaoNickname == null || profileUrl == null || kakaoID == null) {
            return false;
        } else {
            return true;
        }
    }
}
