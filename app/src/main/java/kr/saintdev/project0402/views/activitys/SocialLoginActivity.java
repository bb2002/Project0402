package kr.saintdev.project0402.views.activitys;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kr.saintdev.project0402.R;


/**
 * Callback.
 */

public class SocialLoginActivity extends AppCompatActivity {
    SessionCallback callback = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sociallogin);
        getSupportActionBar().hide();

        this.callback = new SessionCallback();
        Session.getCurrentSession().addCallback(this.callback);
        Session.getCurrentSession().checkAndImplicitOpen();

        try {
            PackageInfo info = getPackageManager().getPackageInfo("kr.saintdev.project0402", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("P0402","key_hash="+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(this.callback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            // 세션을 열었다.
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션을 못열었다.
            // 취소한거 같다.

            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
