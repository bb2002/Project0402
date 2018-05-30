package kr.saintdev.project0402.models.datas.constant;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @date 2018-05-26
 */

public interface InternetConst {
    int HTTP_OK = 200;
    int HTTP_AUTH_ERROR = 400;
    int HTTP_CLIENT_REQUEST_ERROR = 401;
    int HTTP_INTERNAL_SERVER_ERROR = 500;

    String TARGET_SERVER = "http://saintdev.kr/p0402/";
    String KAKAO_PROFILE_DEFAULT = TARGET_SERVER + "cdn/default.png";    // 기본 프로필 사진

    String SECURE_CREATE_ACCOUNT = TARGET_SERVER + "secure/join.php";
    String SECURE_AUTHME_ACCOUNT = TARGET_SERVER + "secure/auth.php";

    String ADD_COMMAND = TARGET_SERVER + "service/add-command.php";     // 명령어 추가
    String LIST_COMMAND = TARGET_SERVER + "service/list-command.php";   // 명령어 목록 조회
    String EXECUTE_COMMAND = TARGET_SERVER + "service/execute-command.php";     // 가장 유사한 명령어 검색
    String REMOVE_COMMAND = TARGET_SERVER + "service/remove-command.php";       // 명령어 삭제




    String TTS_API = "https://naveropenapi.apigw.ntruss.com/voice/v1/tts";
}
