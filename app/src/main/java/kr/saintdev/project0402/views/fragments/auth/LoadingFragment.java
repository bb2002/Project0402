package kr.saintdev.project0402.views.fragments.auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

import org.json.JSONObject;

import java.util.HashMap;

import kr.saintdev.project0402.R;
import kr.saintdev.project0402.models.components.kakao.KakaoLoginManager;
import kr.saintdev.project0402.models.components.kakao.KakaoLoginObject;
import kr.saintdev.project0402.models.datas.constant.InternetConst;
import kr.saintdev.project0402.models.datas.profile.MeProfileManager;
import kr.saintdev.project0402.models.datas.profile.MeProfileObject;
import kr.saintdev.project0402.models.tasks.BackgroundWork;
import kr.saintdev.project0402.models.tasks.OnBackgroundWorkListener;
import kr.saintdev.project0402.models.tasks.http.HttpRequester;
import kr.saintdev.project0402.models.tasks.http.HttpResponseObject;
import kr.saintdev.project0402.views.activitys.MainActivity;
import kr.saintdev.project0402.views.activitys.AuthActivity;
import kr.saintdev.project0402.views.fragments.SuperFragment;
import kr.saintdev.project0402.views.window.dialog.DialogManager;
import kr.saintdev.project0402.views.window.dialog.clicklistener.OnYesClickListener;


/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @date 2018-05-26
 */

public class LoadingFragment extends SuperFragment {
    AuthActivity control = null;
    MeProfileManager me = null;
    DialogManager dm = null;

    KakaoLoginManager kakaoLoginManager = null;


    private static final int REQEUST_ACCOUNT_CREATE = 0x0;
    private static final int REQUEST_ACCOUNT_LOGIN = 0x1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmn_loading, container, false);

        this.control = (AuthActivity) getActivity();
        this.dm = new DialogManager(control);
        this.dm.setOnYesButtonClickListener(new OnDialogCloseHandler(), "CLOSE");

        this.kakaoLoginManager = KakaoLoginManager.getInstance(control);
        this.me = MeProfileManager.getInstance(control);

        control.setActionBarTitle(null);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 카카오 로그인을 확인한다.
        UserManagement.getInstance().requestMe(new OnKakaoProfileHandler());
    }

    class OnKakaoProfileHandler extends MeResponseCallback {
        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            onNotSignedUp();
        }

        @Override
        public void onNotSignedUp() {
            control.switchFragment(new KakaoLoginFragment());
        }

        @Override
        public void onSuccess(UserProfile result) {
            // 카카오 계정 데이터를 업데이트 한다.
            KakaoLoginObject kakaoLoginObject = new KakaoLoginObject(result);
            kakaoLoginManager.setKakaoLoginObject(kakaoLoginObject);

            if(kakaoLoginObject.isValid()) {
                // 유효한 필드의 카카오 계정이다.
                HttpRequester httpRequester;
                OnBackgroundTaskHandler listener = new OnBackgroundTaskHandler();

                // Narre 에 가입되어 있는지 확인한다.
                MeProfileObject profile = me.getCertification();
                if(profile == null) {
                    // 가입되지 않은 사용자 이다.
                    // Narre 서비스에 가입시킨다.
                    HashMap<String, Object> args = new HashMap<>();
                    args.put("nickname", kakaoLoginObject.getKakaoNickname());
                    args.put("kakao_profile", kakaoLoginObject.getProfileUrl());
                    args.put("kakao_id", kakaoLoginObject.getKakaoID());

                    httpRequester =
                            new HttpRequester(InternetConst.SECURE_CREATE_ACCOUNT, args, REQEUST_ACCOUNT_CREATE, listener, control);
                } else {
                    // 가입된 사용자 이다. 자동 로그인을 시도한다.
                    HashMap<String, Object> args = new HashMap<>();
                    args.put("kakao_id", profile.getKakaoId());
                    args.put("user_pin", profile.getUserPin());
                    httpRequester =
                            new HttpRequester(InternetConst.SECURE_AUTHME_ACCOUNT, args, REQUEST_ACCOUNT_LOGIN, listener, control);
                }

                httpRequester.execute();
            } else {
                dm.setTitle("Fatal error");
                dm.setDescription("카카오 계정 필드가 유효하지 않습니다.");
                dm.show();
            }
        }
    }

    class OnBackgroundTaskHandler implements OnBackgroundWorkListener {
        @Override
        public void onSuccess(int requestCode, BackgroundWork worker) {
            HttpResponseObject httpResp = (HttpResponseObject) worker.getResult();

            if(httpResp.getResponseResultCode() != InternetConst.HTTP_OK) {
                // 서버 요청 오류
                dm.setTitle("Internal server error");
                dm.setDescription("계정 생성에 실패했습니다!");
                dm.show();
            } else {
                Intent startActivity = new Intent(control, MainActivity.class);

                try {
                    if (requestCode == REQEUST_ACCOUNT_CREATE) {
                        // 회원가입 처리 결과
                        JSONObject body = httpResp.getBody();
                        String userUUID = body.getString("user_pin");

                        KakaoLoginObject kakaologinObj = kakaoLoginManager.getKakaoLoginObject();

                        MeProfileObject profile = new MeProfileObject(
                                kakaologinObj.getKakaoID(),
                                kakaologinObj.getKakaoNickname(),
                                kakaologinObj.getProfileUrl(),
                                userUUID
                        );

                        me.setCertification(profile);
                    } else if (requestCode == REQUEST_ACCOUNT_LOGIN) {
                        // 로그인 처리 결과
                        JSONObject body = httpResp.getBody();

                        // 자동 로그인 성공?
                        if(httpResp.getResponseResultCode() == InternetConst.HTTP_AUTH_ERROR) {
                            // 잘못된 인증서 입니다.
                            dm.setTitle("인증서 오류");
                            dm.setDescription("자동 로그인에 실패했습니다.\n잘못된 인증서 입니다.");
                            dm.show();

                            // 인증서를 제거한다.
                            me.clear();
                        } else if(httpResp.getResponseResultCode() != InternetConst.HTTP_OK) {
                            // 다른 오류
                            dm.setTitle("알 수 없는 오류");
                            dm.setDescription("내부 서버 오류가 발생했습니다.");
                            dm.show();
                        }
                    }
                } catch(Exception ex) {
                    dm.setTitle("Fatal error");
                    dm.setDescription("An error occurred.\n" + ex.getMessage());
                    dm.show();
                }

                // 초기화과 되었는지 확인하고
                // 초기 설정 또는 메인 화면으로 이동합니다.
                startActivity(startActivity);
                control.finish();
            }
        }

        @Override
        public void onFailed(int requestCode, Exception ex) {
            // 실패했습니다.
            String title;
            if(requestCode == REQEUST_ACCOUNT_CREATE) {
                // 회원가입 처리 결과
                title = "계정 생성 실패!";
            } else  if(requestCode == REQUEST_ACCOUNT_LOGIN) {
                // 로그인 처리 결과
                title = "자동 로그인 실패!";
            } else {
                title ="Unknwon request!";
            }

            dm.setTitle(title);
            dm.setDescription(ex.getMessage());
            dm.show();

            ex.printStackTrace();
        }
    }

    class OnDialogCloseHandler implements OnYesClickListener {
        @Override
        public void onClick(DialogInterface dialog) {
            dialog.dismiss();
            control.finish();
        }
    }
}
