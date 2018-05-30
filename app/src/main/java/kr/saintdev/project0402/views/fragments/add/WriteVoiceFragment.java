package kr.saintdev.project0402.views.fragments.add;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import kr.saintdev.project0402.R;
import kr.saintdev.project0402.models.datas.constant.InternetConst;
import kr.saintdev.project0402.models.datas.profile.MeProfileManager;
import kr.saintdev.project0402.models.datas.profile.MeProfileObject;
import kr.saintdev.project0402.models.tasks.BackgroundWork;
import kr.saintdev.project0402.models.tasks.OnBackgroundWorkListener;
import kr.saintdev.project0402.models.tasks.http.HttpRequester;
import kr.saintdev.project0402.models.tasks.http.HttpResponseObject;
import kr.saintdev.project0402.views.activitys.AddCommandActivity;
import kr.saintdev.project0402.views.fragments.SuperFragment;
import kr.saintdev.project0402.views.window.dialog.DialogManager;
import kr.saintdev.project0402.views.window.dialog.clicklistener.OnYesClickListener;
import kr.saintdev.project0402.views.window.progress.ProgressManager;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-30
 */

public class WriteVoiceFragment extends SuperFragment {
    TextView commandView = null;        // 현재 저장될 명령어 뷰
    TextView statusView = null;         // 현재 상태를 표시합니다.
    ImageButton recordButton = null;    // STT 를 통한 녹음
    Button saveButton = null;           // 저장 버튼
    ProgressBar progressBar = null;     // 진행률

    String command = null;
    ArrayList<String> likeCharacter = new ArrayList<>();
    Intent stt = null;
    RecognizerHandler recoHandler = null;
    SpeechRecognizer recorder = null;

    AddCommandActivity control = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmn_add_voice, container, false);
        this.commandView = v.findViewById(R.id.add_voice_title);
        this.recordButton = v.findViewById(R.id.add_voice_record);
        this.progressBar = v.findViewById(R.id.add_voice_progress);
        this.statusView = v.findViewById(R.id.add_voice_status);
        this.saveButton = v.findViewById(R.id.add_voice_save);

        this.control = (AddCommandActivity) getActivity();

        // 명령어를 불러온다.
        Object o = control.getTag();
        if(o != null) {
            this.command = (String) o;
            this.commandView.setText(this.command);
        } else {
            Toast.makeText(control, "잘못된 접근입니다!", Toast.LENGTH_SHORT).show();
            control.finish();
        }

        // STT 인텐트를 정의한다.
        this.stt = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        this.stt.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, control.getPackageName());
        this.stt.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        // STT 리스너에 대해 정의합니다.
        this.recoHandler = new RecognizerHandler();
        this.recorder = SpeechRecognizer.createSpeechRecognizer(control);
        this.recorder.setRecognitionListener(this.recoHandler);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        OnButtonClickHandler handler = new OnButtonClickHandler();
        this.recordButton.setOnClickListener(handler);
        this.saveButton.setOnClickListener(handler);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(this.recorder != null) this.recorder.destroy();
    }

    class RecognizerHandler implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {
            statusView.setText("말씀하세요.");
            recordButton.setVisibility(View.INVISIBLE); // 녹음중임을 표시
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
            recordButton.setVisibility(View.VISIBLE);
            statusView.setText("오류 : " + error);
        }

        @Override
        public void onResults(Bundle results) {
            recordButton.setVisibility(View.VISIBLE);       // 녹음이 완료됨을 표시

            ArrayList<String> mResult = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            int length = 0;

            // 데이터 삽입
            for(String s : mResult) {
                if(!likeCharacter.contains(s)) {
                    likeCharacter.add(s);       // 새 단어를 추가합니다.
                    length ++;
                }
            }

            // 현재 진행률을 표시한다.
            statusView.setText(length + "개를 추가하였습니다.");
            progressBar.setProgress(likeCharacter.size());

            if(likeCharacter.size() > 7) {
                // 다음 버튼을 활성화 한다.
                saveButton.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    }

    class OnButtonClickHandler implements View.OnClickListener, OnBackgroundWorkListener, OnYesClickListener {
        ProgressManager pm = null;
        DialogManager dm = null;

        public OnButtonClickHandler() {
            this.dm = new DialogManager(control);
            this.dm.setOnYesButtonClickListener(this, "OK");
            this.pm = new ProgressManager(control);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.add_voice_record:
                    // STT 변환을 시작합니다.
                    recorder.startListening(stt);
                    break;
                case R.id.add_voice_save:
                    saveAndExit();
                    break;
            }
        }

        @Override
        public void onSuccess(int requestCode, BackgroundWork worker) {
            this.pm.disable();

            HttpResponseObject respObj = (HttpResponseObject) worker.getResult();
            if(respObj.getResponseResultCode() != 200) {
                dm.setTitle("Internal server error");
                dm.setDescription("내부 서버 오류!");
                dm.show();
            } else {
                Toast.makeText(control, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                control.finish();
            }
        }

        @Override
        public void onFailed(int requestCode, Exception ex) {
            this.pm.disable();

            this.dm.setTitle("Fatal error.");
            this.dm.setDescription(ex.getMessage());
            this.dm.show();
        }

        @Override
        public void onClick(DialogInterface dialog) {
            dialog.dismiss();
        }

        private void saveAndExit() {
            StringBuilder argsBuilder = new StringBuilder();

            for(String s : likeCharacter) {
                argsBuilder.append(s);
                argsBuilder.append(",");
            }

            MeProfileManager profileManager = MeProfileManager.getInstance(control);
            MeProfileObject profile = profileManager.getCertification();

            if(profile != null) {
                HashMap<String, Object> args = new HashMap<>();
                args.put("user_pin", profile.getUserPin());
                args.put("args", argsBuilder.toString());
                args.put("command", command);

                HttpRequester requester = new HttpRequester(InternetConst.ADD_COMMAND, args, 0x1, this, control);
                requester.execute();

                this.pm.setMessage("처리 중...");
                this.pm.enable();
            } else {
                dm.setTitle("Auth error");
                dm.setDescription("인증서를 찾을 수 없습니다.");
                dm.show();
            }
        }
    }
}
