package kr.saintdev.project0402.views.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import kr.saintdev.project0402.R;
import kr.saintdev.project0402.models.datas.constant.InternetConst;
import kr.saintdev.project0402.models.datas.objects.CommandItem;
import kr.saintdev.project0402.models.datas.profile.MeProfileManager;
import kr.saintdev.project0402.models.datas.profile.MeProfileObject;
import kr.saintdev.project0402.models.tasks.BackgroundWork;
import kr.saintdev.project0402.models.tasks.OnBackgroundWorkListener;
import kr.saintdev.project0402.models.tasks.http.HttpRequester;
import kr.saintdev.project0402.models.tasks.http.HttpResponseObject;
import kr.saintdev.project0402.views.adapters.CommandListAdapter;
import kr.saintdev.project0402.views.window.dialog.DialogManager;
import kr.saintdev.project0402.views.window.dialog.clicklistener.OnYesClickListener;
import kr.saintdev.project0402.views.window.progress.ProgressManager;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-30
 */

public class ShowCommandActivity extends AppCompatActivity {
    ListView commandList = null;            // 명령어 목록
    TextView emptyMessageView = null;       // 비어있을 경우
    Button runButton = null;                // 명령어를 실행합니다.

    DialogManager dm = null;
    ProgressManager pm = null;
    CommandListAdapter adapter = null;

    MeProfileObject profile = null;
    OnBackgroundWorkListener listener = null;

    private static final int REQUEST_MY_COMMANDS = 0x0;
    private static final int REQUEST_REMOVE_COMMAND = 0x1;
    private static final int REQUEST_STT = 0x2;
    private static final int REQUEST_EXECUTE = 0x4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_commands);

        this.commandList = findViewById(R.id.show_commands_list);
        this.emptyMessageView = findViewById(R.id.show_commands_empty);
        this.runButton = findViewById(R.id.add_command_run);

        MeProfileManager profileManager = MeProfileManager.getInstance(this);
        this.profile = profileManager.getCertification();

        this.dm = new DialogManager(this);
        this.dm.setOnYesButtonClickListener(new OnYesClickListener() {
            @Override
            public void onClick(DialogInterface dialog) {
                dialog.dismiss();
            }
        }, "OK");
        this.pm = new ProgressManager(this);
        this.runButton.setOnClickListener(new OnRunButtonClickHandler());
        this.adapter = new CommandListAdapter();

        // 이벤트 처리
        this.commandList.setAdapter(this.adapter);
        this.listener = new OnBackgroundWorkHandler();

        // 서버에 요청하여 데이터를 불러옵니다.
        updateCommandList();

        // 요청 날리고 대기
        this.pm.setMessage("명령어를 불러오는 중...");
        this.pm.enable();
    }

    /**
     * ListView 에 Delete Button 을 눌렀을 경우
     */
    class OnItemDeleteClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            HashMap<String, Object> args = new HashMap<>();
            args.put("user_pin", profile.getUserPin());
            args.put("command-id", v.getTag());
            HttpRequester requester =
                    new HttpRequester(InternetConst.REMOVE_COMMAND, args, REQUEST_REMOVE_COMMAND, listener, ShowCommandActivity.this);
            requester.execute();

            pm.setMessage("제거 중...");
            pm.enable();
        }
    }

    /**
     * Run button 을 눌렀습니다.
     */
    class OnRunButtonClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "명령어를 말하세요.");

            startActivityForResult(i, REQUEST_STT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_STT) {
            // 결과값을 받는다.
            ArrayList<String> mResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(mResult.size() == 0) {
                // 인식된게 없다.
                Toast.makeText(getApplicationContext(), "알 수 없는 명령어 입니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 입력 성공
            pm.setMessage("검색 중...");
            pm.enable();

            // 서버에 데이터를 요청 한다.
            JSONArray ment = new JSONArray();
            for(String s : mResult) ment.put(s);

            HashMap<String, Object> args = new HashMap<>();

            args.put("data", ment.toString());  // 음성 입력 받은 데이터를 JSON Array 로 바꾸고 인자값으로 넣는다.
            args.put("user_pin", profile.getUserPin());

            HttpRequester requester =
                    new HttpRequester(InternetConst.EXECUTE_COMMAND, args, REQUEST_EXECUTE, listener, ShowCommandActivity.this);
            requester.execute();
        }
    }

    /**
     * 서버에서 명령어 목록을 불러옴
     */
    class OnBackgroundWorkHandler implements OnBackgroundWorkListener {
        @Override
        public void onSuccess(int requestCode, BackgroundWork worker) {
            pm.disable();

            HttpResponseObject respObj = (HttpResponseObject) worker.getResult();
            if(requestCode == REQUEST_MY_COMMANDS) {
                try {

                    if (respObj.getResponseResultCode() == InternetConst.HTTP_OK) {
                        // 정상 처리, 목록을 리스트뷰에 띄웁니다.
                        JSONObject body = respObj.getBody();

                        JSONArray originalCommands = body.getJSONArray("orign");
                        JSONArray commandId = body.getJSONArray("id");
                        OnItemDeleteClickHandler handler = new OnItemDeleteClickHandler();

                        adapter.clear();

                        for(int i = 0; i < originalCommands.length(); i ++) {
                            CommandItem item = new CommandItem(
                                    commandId.getString(i),
                                    originalCommands.getString(i),
                                    handler);

                            adapter.addItem(item);
                        }

                        adapter.notifyDataSetChanged();

                        if(originalCommands.length() == 0) {
                            // 아이템이 없을 경우
                            commandList.setVisibility(View.INVISIBLE);
                            emptyMessageView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        dm.setTitle("Fatal error");
                        dm.setDescription("An error occurred.");
                        dm.show();
                    }
                } catch(JSONException jex) {
                    dm.setTitle("JSON Error");
                    dm.setDescription("An error occurred.\n" + jex.getMessage());
                    dm.show();
                }
            } else if(requestCode == REQUEST_REMOVE_COMMAND) {
                if (respObj.getResponseResultCode() == InternetConst.HTTP_OK) {
                    // 정상 처리, 제거
                    updateCommandList();
                } else {
                    dm.setTitle("Fatal error");
                    dm.setDescription("Remove failed.");
                    dm.show();
                }
            } else if(requestCode == REQUEST_EXECUTE) {
                if(respObj.getResponseResultCode() == InternetConst.HTTP_OK) {
                    try {
                        JSONObject body = respObj.getBody();

                        if(body.getBoolean("is_success")) {
                            Intent tts = new Intent(ShowCommandActivity.this, CommandPlayActivity.class);
                            tts.putExtra("command", body.getString("command"));
                            startActivity(tts);
                        } else {
                            dm.setTitle("명령어가 없슴");
                            dm.setDescription("근접한 명령어가 없습니다.\n다시 시도해보세요.");
                            dm.show();
                        }
                    } catch(Exception ex) {
                        Toast.makeText(getApplicationContext(), "TTS error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    dm.setTitle("Fatal error");
                    dm.setDescription("An error occurred.");
                    dm.show();
                }
            }
        }

        @Override
        public void onFailed(int requestCode, Exception ex) {
            pm.disable();

            dm.setTitle("Fatal error");
            dm.setDescription("An error occurred.\n" + ex.getMessage());
            dm.show();
        }
    }

    /**
     * 명령 목록을 업데이트 합니다.
     */
    private void updateCommandList() {
        HashMap<String, Object> args = new HashMap<>();
        args.put("user_pin", profile.getUserPin());
        HttpRequester requester =
                new HttpRequester(InternetConst.LIST_COMMAND, args, 0x0, new OnBackgroundWorkHandler(), this);
        requester.execute();
    }
}
