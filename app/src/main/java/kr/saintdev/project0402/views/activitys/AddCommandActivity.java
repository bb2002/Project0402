package kr.saintdev.project0402.views.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.saintdev.project0402.R;

/**
 * Created by 5252b on 2018-04-04.
 */

public class AddCommandActivity extends AppCompatActivity {
    Button commitButton = null;
    EditText commandEditor = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_command);
        getSupportActionBar().hide();

        // 개체 찾기
        this.commandEditor = findViewById(R.id.addcmd_command_editor);
        this.commitButton = findViewById(R.id.addcmd_button);
        this.commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String command = commandEditor.getText().toString();
                if(command.length() == 0) {
                    // 명령어가 없습니다.
                    Toast.makeText(getApplicationContext(), "등록할 명령어를 입력하세요.", Toast.LENGTH_SHORT).show();
                    commandEditor.setFocusable(true);
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), AddVoiceCommandActivity.class);
                intent.putExtra("command", command);
                startActivity(intent);

                finish();
            }
        });
    }
}
