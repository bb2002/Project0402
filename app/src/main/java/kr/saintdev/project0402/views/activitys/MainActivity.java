package kr.saintdev.project0402.views.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import kr.saintdev.project0402.R;

/**
 * Created by 5252b on 2018-04-02.
 */

public class MainActivity extends AppCompatActivity {
    Button addCommandButton = null;     // 명령어 추가 화면
    Button listCommandButton = null;    // 명령어 목록 화면

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        // 개체 찾기
        this.addCommandButton = findViewById(R.id.main_buttons_addcommand);
        this.listCommandButton = findViewById(R.id.main_buttons_listcommand);

        // 핸들러 등록
        OnButtonClickHandler handler = new OnButtonClickHandler();
        this.addCommandButton.setOnClickListener(handler);
        this.listCommandButton.setOnClickListener(handler);
    }

    class OnButtonClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = null;

            switch(v.getId()) {
                case R.id.main_buttons_addcommand:
                    // 명령어 추가 화면
                    intent = new Intent(getApplicationContext(), AddCommandActivity.class);
                    break;
                case R.id.main_buttons_listcommand:
                    // 명령어 목록 화면
                    intent = new Intent(getApplicationContext(), ListCommandActivity.class);
                    break;
                default: return;
            }

            startActivity(intent);
        }
    }
}
