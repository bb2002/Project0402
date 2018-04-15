package kr.saintdev.project0402.views.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import kr.saintdev.project0402.R;
import kr.saintdev.project0402.modules.defines.HttpUrlDefines;
import kr.saintdev.project0402.modules.secure.auth.Authme;
import kr.saintdev.project0402.modules.workspace.task.OnWorkTaskListener;
import kr.saintdev.project0402.modules.workspace.task.SingleWorkTask;
import kr.saintdev.project0402.modules.workspace.work.Work;
import kr.saintdev.project0402.modules.workspace.work.implem.HttpWork;
import kr.saintdev.project0402.views.windows.progress.ProgressManager;

/**
 * Created by 5252b on 2018-04-04.
 */

public class AddVoiceCommandActivity extends AppCompatActivity {
    // View 개체
    TextView commandView = null;
    ImageButton microPhoneButton = null;    // 마이크
    TextView statusView = null;     // 마이크에 대한 상태 표시기
    Button commitButton = null;     // 확인 버튼 겸 스테이터스 바

    // 입력 받은 데이터
    String command = null;
    ArrayList<String> linkSentence = new ArrayList<>(); // 입력 받은 stt 문장
    Intent stt = null;
    SpeechRecognizer recognizer = null;

    // 사용자에게 진행 상태를 알리기 위한 다이얼로그
    ProgressManager pm = null;

    int count = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_voicecmd);

        // 개체 찾기
        this.commandView = findViewById(R.id.voicecmd_commandview);
        this.microPhoneButton = findViewById(R.id.voicecmd_write);
        this.statusView = findViewById(R.id.voicecmd_status);
        this.commitButton = findViewById(R.id.voicecmd_ok);
        this.pm = new ProgressManager(this);

        // 핸들링 정의
        OnButtonClickHandler handler = new OnButtonClickHandler();
        this.microPhoneButton.setOnClickListener(handler);
        this.commitButton.setOnClickListener(handler);

        Intent intent = getIntent();
        this.command = intent.getStringExtra("command");    // 명령어를 가져옵니다.
        this.commandView.setText(command);

        // STT 인텐트를 정의한다.
        this.stt = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        this.stt.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        this.stt.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        this.recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        this.recognizer.setRecognitionListener(new RecognizerHandler());
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.recognizer.destroy();
    }

    class OnButtonClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.voicecmd_write:
                    recognizer.startListening(stt);
                    break;
                case R.id.voicecmd_ok:
                    if(linkSentence.size() != 0) {
                        // 입력 받은 데이터를 서버로 보낸다.
                        commit();
                    } else {
                        Toast.makeText(getApplicationContext(), "한번 이상 입력하세요.", Toast.LENGTH_LONG).show();
                    }

                    break;
            }
        }
    }

    // 수집된 데이터를 서버로 보냅니다.
    private void commit() {
        StringBuilder argsBuilder = new StringBuilder();

        for(String s : linkSentence) {
            argsBuilder.append(s);
            argsBuilder.append(",");
        }

        Authme me = Authme.getInstance(this);

        HashMap<String, Object> args = new HashMap<>();
        args.put("user_pin", me.getPin());
        args.put("args", argsBuilder.toString());
        args.put("command", this.command);

        HttpWork work = new HttpWork(HttpUrlDefines.ADD_COMMAND, args);

        SingleWorkTask task = new SingleWorkTask(new OnCommandAddedListener());
        task.execute(work);

        this.pm.setMessage("Execute...");
        this.pm.enable();
    }

    class RecognizerHandler implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {
            statusView.setText("말씀하세요.");
            microPhoneButton.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {
            microPhoneButton.setVisibility(View.VISIBLE);
            statusView.setText("오류 : " + error);
        }

        @Override
        public void onResults(Bundle results) {
            microPhoneButton.setVisibility(View.VISIBLE);

            ArrayList<String> mResult = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if(mResult.size() == 0) {
                // 인식된게 없다.
                statusView.setText("입력된 문장이 없습니다.\n좀 더 또박또박 말해주세요.");
                return;
            } else {
                statusView.setText("입력되었습니다.");
            }

            // 데이터 삽입
            for(String s : mResult) {
                if(!linkSentence.contains(s)) {
                    linkSentence.add(s);   // 있는 데이터라면 넘어갑니다
                }
            }

            if(count == 10) {
                microPhoneButton.setVisibility(View.INVISIBLE);

            }

            commitButton.setText(++count + "/10 - 총 ");
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    }

    class OnCommandAddedListener implements OnWorkTaskListener {
        @Override
        public void onTaskListener(Work[] result) {
            pm.disable();

            HttpWork work = (HttpWork) result[0];
            HttpWork.HttpResponse resp = work.getResultObject();

            if(resp.getResponseCode() == HttpWork.HttpCodes.HTTP_OK) {
                // 처리 성공
                Toast.makeText(getApplicationContext(), "추가되었습니다.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }

            finish();
        }

        @Override
        public void onProcessedUpdate(int now, int all) {

        }
    }
}
