package kr.saintdev.project0402;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import kr.saintdev.project0402.modules.defines.ActivityResultCodes;
import kr.saintdev.project0402.modules.defines.HttpUrlDefines;
import kr.saintdev.project0402.modules.lib.HttpObject;
import kr.saintdev.project0402.modules.lib.NetState;
import kr.saintdev.project0402.modules.secure.auth.Authme;
import kr.saintdev.project0402.modules.workspace.task.OnWorkTaskListener;
import kr.saintdev.project0402.modules.workspace.task.SingleWorkTask;
import kr.saintdev.project0402.modules.workspace.work.Work;
import kr.saintdev.project0402.modules.workspace.work.implem.HttpWork;
import kr.saintdev.project0402.views.activitys.MainActivity;
import kr.saintdev.project0402.views.activitys.SocialLoginActivity;
import kr.saintdev.project0402.views.windows.dialogs.main.DialogManager;
import kr.saintdev.project0402.views.windows.dialogs.main.clicklistener.OnYesClickListener;

public class SplashActivity extends AppCompatActivity {
    TextView logcat = null;     // 현재 상태를 표시해주는 로그 켓

    DialogManager mDialog = null;   // 정보 표시를 위한 다이얼로그



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        this.logcat = findViewById(R.id.splash_logger);
        this.mDialog = new DialogManager(this);

        getSupportActionBar().hide();

        // 네트워크 연결 확인.
        showLogcat(R.string.splash_status_checkinternet);
        if(!NetState.isNetworkEnable(this)) {
            // 어머, 네트워크 연결이 되어 있지 않습니다.
            showDialog(getString(R.string.fatal_error), getString(R.string.error_no_internet), true);
            return;
        }

        // 카카오 로그인 확인
        showLogcat(R.string.splash_status_kakaologin);
        UserManagement.requestMe(new OnKakaoLoginCallback());
    }

    private void doAccountUpdate(UserProfile profile) {
        // 해당 pin 을 통해 사용자 인증을 하고
        // 서버에서 데이터를 불러와 클라이언트에 업데이트 합니다.
        if(profile == null) {
            showDialog(getString(R.string.fatal_error), getString(R.string.kakaologin_failed_noreason) + "\n지속되는경우, 앱을 재설치 하세요.", true);
            return;
        }

        // 데이터를 모읍니다.
        String kakaoId = profile.getId() + "";
        String userPin = Authme.getInstance(this).getPin();

        if(userPin == null) {
            // 아직 가입되지 않았나봅니다.
            showLogcat(R.string.splash_status_reloadpin);
            onActivityResult(ActivityResultCodes.ON_KAKAO_SIGNUP, RESULT_OK, null);
            return;
        }

        // 데이터 준비 완료. 데이터를 불러옵니다.
        SingleWorkTask task = new SingleWorkTask(new OnUpdateDataCallback());

        HashMap<String, Object> args = new HashMap<>();
        args.put("kakao_id", kakaoId);
        args.put("user_pin", userPin);
        task.execute(new HttpWork(HttpUrlDefines.SECURE_AUTHME_ACCOUNT, args));

        showLogcat(R.string.splash_status_updatedata);
    }

    /*
        사용자에게 정보를 제공하는 다이얼로그 띄우기
     */
    private void showDialog(String title, String msg, final boolean isFinish) {
        this.mDialog.setTitle(title);
        this.mDialog.setDescription(msg);
        this.mDialog.setOnYesButtonClickListener(new OnYesClickListener() {
            @Override
            public void onClick(DialogInterface dialog) {
                dialog.dismiss();
                if(isFinish) {
                    finish();
                }
            }
        }, "Ok");
        this.mDialog.show();
    }

    /*
        statusView updater
     */
    private void showLogcat(int id) {
        this.logcat.setText(getString(id));
        // this.logcat.startAnimation(this.showAnime);   나중에 애니메이션도 많이 쓸겁니다.
    }



    /*
        // TODO 카카오 로그인 콜백
     */
    class OnKakaoLoginCallback extends MeResponseCallback {
        @Override
        public void onFailure(ErrorResult errorResult) {
            // 카카오 로그인 실패.
            if(errorResult == null) {
                // 이유 없슴
                showDialog(getString(R.string.fatal_error), getString(R.string.kakaologin_failed_noreason), true);
            } else {
                // 이유 있슴.
                showDialog(getString(R.string.fatal_error), getString(R.string.kakaologin_failed_reason) + errorResult.getErrorMessage(), true);
            }
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            gotoKakaoLoginActivity();   // 카카오 로그인 허러 출동!
        }

        @Override
        public void onNotSignedUp() {
            gotoKakaoLoginActivity();   // 카카오 로그인 하러 출동!
        }

        @Override
        public void onSuccess(UserProfile result) {
            // 로그인 성공!
            // 다음 작업, 해당 ID 값이 서버에서 존재하는지 확인합니다.
            doAccountUpdate(result);
        }

        // 카카오 로그인이 필요합니다.
        // 대체적으로 첫 사용자에게 제공됩니다.
        private void gotoKakaoLoginActivity() {
            showLogcat(R.string.splash_status_newuser);
            Intent intent = new Intent(getApplicationContext(), SocialLoginActivity.class);
            startActivityForResult(intent, ActivityResultCodes.ON_KAKAO_SIGNUP);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ActivityResultCodes.ON_KAKAO_SIGNUP) {
            // 카카오 로그인을 완료하고 온거 같습니다.

            if(resultCode == RESULT_OK) {
                // 성공
                UserManagement.requestMe(new MeResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        showDialog(getString(R.string.fatal_error), getString(R.string.kakaologin_failed_userstop), true);
                    }

                    @Override
                    public void onNotSignedUp() {
                        showDialog(getString(R.string.fatal_error), getString(R.string.kakaologin_failed_userstop), true);
                    }

                    @Override
                    public void onSuccess(UserProfile result) {
                        String kakaoId = result.getId() + "";
                        String nickname = result.getNickname();
                        String profile = result.getProfileImagePath();

                        HashMap<String, Object> args = new HashMap<>();
                        args.put("kakao_id", kakaoId);
                        args.put("nickname", nickname);
                        args.put("kakao_profile", profile);

                        SingleWorkTask task = new SingleWorkTask(new OnRegistedCallback());
                        task.execute(new HttpWork(HttpUrlDefines.SECURE_CREATE_ACCOUNT, args));
                    }
                });
            } else {
                // 실패
                showDialog(getString(R.string.fatal_error), getString(R.string.kakaologin_failed_userstop), true);
            }
        }
    }


    /*
        회원가입 처리후 콜백
    */
    class OnRegistedCallback implements OnWorkTaskListener {
        @Override
        public void onTaskListener(Work[] result) {
            HttpWork httpWork = (HttpWork) result[0];
            HttpObject httpObj = new HttpObject(httpWork);
            JSONObject respBody = httpObj.getBody();

            if(httpObj.getReqeustResultCode() != HttpWork.HttpCodes.HTTP_OK) {
                // 클라이언트 오류
                showDialog(getString(R.string.fatal_error), getString(R.string.http_client_error), true);
                return;
            } else {

                if (httpObj.getResponseResultCode() == HttpWork.HttpCodes.HTTP_OK) {
                    // 가입된 정보 저장
                    try {
                        String pin = respBody.getString("user_pin");

                        Authme me = Authme.getInstance(getApplicationContext());
                        me.setPin(pin);     // 사용자 고유 인증 키 저장

                        // 카카오 계정 확인
                        UserManagement.requestMe(new OnKakaoLoginCallback());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showDialog(getString(R.string.fatal_error), "An error occurred. \n" + ex.getMessage(), true);
                    }
                } else {
                    // Http 요청 오류
                    try {
                        showDialog(getString(R.string.fatal_error), "Network I/O Failed.\n" + respBody.getString("msg"), true);
                    } catch (JSONException jex) {
                        jex.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onProcessedUpdate(int now, int all) {

        }
    }

    /*
        사용자 데이터 콜백
     */
    class OnUpdateDataCallback implements OnWorkTaskListener {
        @Override
        public void onTaskListener(Work[] result) {
            HttpWork httpWork = (HttpWork) result[0];
            HttpObject httpObj = new HttpObject(httpWork);
            Authme me = Authme.getInstance(getApplicationContext());

            JSONObject respBody = httpObj.getBody();    // 응답 바디

            if(httpObj.getReqeustResultCode() == HttpWork.HttpCodes.HTTP_OK) {
                // 클라이언트에서 요청 성공

                if(httpObj.getResponseResultCode() == HttpWork.HttpCodes.HTTP_CLIENT_AUTH_ERROR) {
                    // 인증서 오류
                    try {
                        showDialog(getString(R.string.fatal_error), getString(R.string.splash_status_pinerr) + "\n", true);

                        // 데이터 초기화
                        me.setPin(null);        // PIN 초기화
                        me.setUserInfo(null);   // UserInfo 초기화
                        UserManagement.requestLogout(new LogoutResponseCallback() {
                            @Override
                            public void onCompleteLogout() {

                            }
                        }); // 카카오 로그인 초기화.
                    } catch(Exception ex) {
                        ex.printStackTrace();
                        System.exit(0);
                    }

                    return;
                } else if(httpObj.getResponseResultCode() != HttpWork.HttpCodes.HTTP_OK) {
                    // 다른 오류
                    showDialog(getString(R.string.fatal_error), "An error occurred.\nRestart your application.", true);
                    return;
                }

                // 성공적으로 처리됨.
                try {
                    Authme.UserInfo info = me.new UserInfo(
                            respBody.getString("ai_nickname"),
                            respBody.getString("ai_profile")
                    );

                    me.setUserInfo(info);   // 사용자 데이터 갱신
                    showLogcat(R.string.splash_status_welcome);

                    // 2 초 뒤에 화면을 넘긴다.
                    Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    };
                    handler.sendEmptyMessageDelayed(0, 1500);
                } catch(JSONException jex) {
                    // JSON Exception. 무시 가능.
                    showDialog("Warning!", getString(R.string.splash_status_clienterr) + jex.getMessage(), false);
                }
            } else {
                // 클라이언트 요청 오류
                showDialog(getString(R.string.fatal_error), "Client request failed!", true);
            }
        }

        @Override
        public void onProcessedUpdate(int now, int all) {

        }
    }
}
