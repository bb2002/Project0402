package kr.saintdev.project0402.views.activitys;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import kr.saintdev.project0402.R;
import kr.saintdev.project0402.modules.workspace.task.OnWorkTaskListener;
import kr.saintdev.project0402.modules.workspace.task.SingleWorkTask;
import kr.saintdev.project0402.modules.workspace.work.Work;
import kr.saintdev.project0402.modules.workspace.work.implem.TTSWork;

/**
 * Created by yuuki on 18. 4. 15.
 */

public class CommandPlayActivity extends AppCompatActivity {
    String command = null;
    MediaPlayer player = null;
    TextView view = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_command);

        Intent intent = getIntent();
        this.command = intent.getStringExtra("command");
        this.player = new MediaPlayer();
        this.view = findViewById(R.id.play_command_view);

        // 해당 command 를 TTS 를 통해 음성 합성을 시도합니다.
        TTSWork work = new TTSWork(this, this.command);
        SingleWorkTask task = new SingleWorkTask(new OnWorkListener());
        task.execute(work);

        this.view.setText("렌더링 중...");
    }

    class OnWorkListener implements OnWorkTaskListener {
        @Override
        public void onTaskListener(Work[] result) {
            TTSWork work = (TTSWork) result[0];

            File ttsFile = work.getResultObject();
            if(ttsFile == null) {
                // 오류가 발생한거 같다.
                Toast.makeText(getApplicationContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                view.setText("오류가 발생했습니다.");
            } else {
                Toast.makeText(getApplicationContext(), "재생 중...", Toast.LENGTH_SHORT).show();

                try {
                    Uri tts = Uri.fromFile(ttsFile);
                    player.setDataSource(getApplicationContext(), tts);
                    player.prepare();
                    player.start();

                    view.setText(command);
                } catch(IOException iex) {
                    Toast.makeText(getApplicationContext(), "IOException.", Toast.LENGTH_SHORT).show();
                    iex.printStackTrace();

                    view.setText(iex.getMessage());
                }
            }
        }

        @Override
        public void onProcessedUpdate(int now, int all) {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.release();
    }
}
