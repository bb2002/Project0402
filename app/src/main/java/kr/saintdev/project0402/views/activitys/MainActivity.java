package kr.saintdev.project0402.views.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import kr.saintdev.project0402.R;
import kr.saintdev.project0402.models.datas.profile.MeProfileManager;

/**
 * Created by 5252b on 2018-04-02.
 */

public class MainActivity extends AppCompatActivity {
    ImageButton addCommandButton = null;     // 명령어 추가 화면
    ImageButton listCommandButton = null;    // 명령어 목록 화면
    Button logoutButton = null;                   // 로그아웃 버튼

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        // 개체 찾기
        this.addCommandButton = findViewById(R.id.main_buttons_addcommand);
        this.listCommandButton = findViewById(R.id.main_buttons_listcommand);
        this.logoutButton = findViewById(R.id.main_buttons_logout);

        // 핸들러 등록
        OnButtonClickHandler handler = new OnButtonClickHandler();
        this.addCommandButton.setOnClickListener(handler);
        this.listCommandButton.setOnClickListener(handler);
        this.logoutButton.setOnClickListener(handler);

        // 마이크 권한만 확인합니다.
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // 마이크 권한을 요청합니다.
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.RECORD_AUDIO }, 0x0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 0x0) {
            // 마이크 권한 확인
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "마이크를 사용 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    class OnButtonClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = null;

            switch(v.getId()) {
                case R.id.main_buttons_addcommand:
                    // 명령어 추가 화면
                    intent = new Intent(getApplicationContext(), kr.saintdev.project0402.views.activitys.AddCommandActivity.class);
                    break;
                case R.id.main_buttons_listcommand:
                    // 명령어 목록 화면
                    intent = new Intent(getApplicationContext(), ShowCommandActivity.class);
                    break;
                case R.id.main_buttons_logout:
                    logout();
                    return;
                default: return;
            }

            startActivity(intent);
        }

        private void logout() {
            // 카카오 로그아웃
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    // TODO Something
                }
            });

            // 인증서 초기화
            MeProfileManager.getInstance(MainActivity.this).clear();

            Intent auth = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(auth);
            finish();
        }
    }
}
