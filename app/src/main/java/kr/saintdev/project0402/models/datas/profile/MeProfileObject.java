package kr.saintdev.project0402.models.datas.profile;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-11
 * PSCT 사용자 객체
 */

public class MeProfileObject {
    private String kakaoId = null;
    private String kakaoNickname = null;
    private String kakaoProfile = null;
    private String userPin = null;

    public MeProfileObject(String kakaoId, String kakaoNickname, String kakaoProfile, String userPin) {
        this.kakaoId = kakaoId;
        this.kakaoNickname = kakaoNickname;
        this.kakaoProfile = kakaoProfile;
        this.userPin = userPin;
    }

    public String getKakaoId() {
        return kakaoId;
    }

    public String getKakaoNickname() {
        return kakaoNickname;
    }

    public String getKakaoProfile() {
        return kakaoProfile;
    }

    public String getUserPin() {
        return userPin;
    }
}
