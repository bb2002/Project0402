package kr.saintdev.project0402.modules.defines;

/**
 * Created by 5252b on 2018-03-23.
 */

public interface HttpUrlDefines {
    String TARGET_SERVER = "http://saintdev.kr/p0402/";

    String SECURE_CREATE_ACCOUNT = TARGET_SERVER + "secure/join.php";
    String SECURE_AUTHME_ACCOUNT = TARGET_SERVER + "secure/auth.php";

    String ADD_COMMAND = TARGET_SERVER + "service/add-command.php";     // 명령어 추가
    String LIST_COMMAND = TARGET_SERVER + "service/list-command.php";   // 명령어 목록 조회
    String EXECUTE_COMMAND = TARGET_SERVER + "service/execute-command.php";     // 가장 유사한 명령어 검색
}
