package kr.saintdev.project0402.views.fragments.auth;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;

import kr.saintdev.project0402.R;
import kr.saintdev.project0402.models.components.libs.DebugOnly;
import kr.saintdev.project0402.views.activitys.AuthActivity;
import kr.saintdev.project0402.views.fragments.SuperFragment;
import kr.saintdev.project0402.views.window.dialog.DialogManager;
import kr.saintdev.project0402.views.window.dialog.clicklistener.OnYesClickListener;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @date 2018-05-26
 */

public class KakaoLoginFragment extends SuperFragment {
    AuthActivity control = null;

    DialogManager dm = null;
    SessionCallback callback = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.callback = new SessionCallback();
        Session.getCurrentSession().addCallback(this.callback);
        Session.getCurrentSession().checkAndImplicitOpen();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmn_kakaologin, container, false);
        this.control = (AuthActivity) getActivity();
        this.control.setActionBarTitle(null);

        this.dm = new DialogManager(control);
        this.dm.setOnYesButtonClickListener(new OnDialogCloseHandler(), "OK");

        DebugOnly.showKeyHash(control);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(this.callback);
    }

    class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            // 세션을 열었다.
            // 서버측에 회원가입을 요청한다.
            control.switchFragment(new LoadingFragment());
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션을 못열었다.
            // 취소한거 같다.
            dm.setTitle("오류");
            dm.setDescription("로그인 세션을 열 수 없습니다.\n다시 시도 해 보세요.");
            dm.show();
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