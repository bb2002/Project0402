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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import kr.saintdev.project0402.R;
import kr.saintdev.project0402.modules.defines.HttpUrlDefines;
import kr.saintdev.project0402.modules.secure.auth.Authme;
import kr.saintdev.project0402.modules.workspace.task.OnWorkTaskListener;
import kr.saintdev.project0402.modules.workspace.task.SingleWorkTask;
import kr.saintdev.project0402.modules.workspace.work.Work;
import kr.saintdev.project0402.modules.workspace.work.implem.HttpWork;

/**
 * Created by 5252b on 2018-04-11.
 */

public class LinkVoiceCommandActivity extends AppCompatActivity {
    TextView statusView = null;
    ImageButton microPhoneButton = null;

    Intent stt = null;
    SpeechRecognizer recognizer = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_voicecmd);

        this.statusView = findViewById(R.id.write_voicecmd_status);
        this.microPhoneButton = findViewById(R.id.write_voicecmd_start);

        this.stt = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        this.stt.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        this.stt.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        this.recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        this.recognizer.setRecognitionListener(new RecognizerHandler());

        this.microPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 마이크 버튼이 눌르면 다시 시도한다.
                recognizer.startListening(stt);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면이 보여지면 실행한다.
        this.recognizer.startListening(this.stt);
    }

    @Override
    protected void onStop() {
        super.onStop();
        recognizer.destroy();
    }

    class RecognizerHandler implements RecognitionListener, OnWorkTaskListener {
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
            statusView.setText("오류가 발생했습니다. " + error);
        }

        @Override
        public void onResults(Bundle results) {
            // 결과값을 받는다.
            ArrayList<String> mResult = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if(mResult.size() == 0) {
                // 인식된게 없다.
                statusView.setText("입력된 문장이 없습니다.\n좀 더 또박또박 말해주세요.");
                microPhoneButton.setVisibility(View.VISIBLE);
                return;
            }

            // 입력 성공
            statusView.setText("유사 명령 검색 중 ... ");

            // 서버에 데이터를 요청 한다.
            JSONArray data = new JSONArray();
            for(String s : mResult) {
                data.put(s);
            }

            HashMap<String, Object> args = new HashMap<>();
            Authme me = Authme.getInstance(getApplicationContext());

            args.put("data", data.toString());  // 음성 입력 받은 데이터를 JSON Array 로 바꾸고 인자값으로 넣는다.
            args.put("user_pin", me.getPin());

            HttpWork work = new HttpWork(HttpUrlDefines.EXECUTE_COMMAND, args);
            SingleWorkTask task = new SingleWorkTask(this);
            task.execute(work);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }

        @Override
        public void onTaskListener(Work[] result) {
            HttpWork work = (HttpWork) result[0];

            try {
                HttpWork.HttpResponse response = work.getResultObject();

                JSONObject returnObj = response.getJsonObject();
                JSONObject body = returnObj.getJSONObject("body");

                // 데이터를 잘 불러왔는지 확인
                boolean isSuccess = body.getBoolean("is_success");
                if(isSuccess) {
                    String command = body.getString("command");

                    // 커맨드 실행창을 엽니다.
                    Intent runActivity = new Intent(getApplicationContext(), CommandPlayActivity.class);
                    runActivity.putExtra("command", command);
                    startActivity(runActivity);

                    finish();
                } else {
                    statusView.setText("유사 명령을 찾을 수 없슴.");
                }
            } catch(JSONException jex) {
                jex.printStackTrace();
            }
        }

        @Override
        public void onProcessedUpdate(int now, int all) {

        }
    }
}
