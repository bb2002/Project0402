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

import java.util.ArrayList;

import kr.saintdev.project0402.R;

/**
 * Created by 5252b on 2018-04-11.
 */

public class WriteVoiceCommandActivity extends AppCompatActivity {
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.recognizer.startListening(this.stt);
    }

    class RecognizerHandler implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {
            statusView.setText("말씀하세요.");
            microPhoneButton.setEnabled(false);
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
            microPhoneButton.setEnabled(true);
            statusView.setText("오류가 발생했습니다. " + error);
        }

        @Override
        public void onResults(Bundle results) {
            // 결과값을 받는다.
            ArrayList<String> mResult = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if(mResult.size() == 0) {
                // 인식된게 없다.
                statusView.setText("입력된 문장이 없습니다.\n좀 더 또박또박 말해주세요.");
                return;
            }

            // 입력 성공
            String matchCommand = mResult.get(0);
            microPhoneButton.setVisibility(View.INVISIBLE);
            statusView.setText("유사 명령 검색 중 ... " + matchCommand);

            // 서버에 데이터를 요청 한다.
            for(String msg : mResult) {
                Log.d("P0402", msg);
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    }
}
