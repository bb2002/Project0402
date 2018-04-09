package kr.saintdev.project0402.views.activitys;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import kr.saintdev.project0402.R;
import kr.saintdev.project0402.modules.defines.HttpUrlDefines;
import kr.saintdev.project0402.modules.secure.auth.Authme;
import kr.saintdev.project0402.modules.workspace.task.OnWorkTaskListener;
import kr.saintdev.project0402.modules.workspace.task.SingleWorkTask;
import kr.saintdev.project0402.modules.workspace.work.Work;
import kr.saintdev.project0402.modules.workspace.work.implem.HttpWork;
import kr.saintdev.project0402.views.windows.dialogs.main.DialogManager;
import kr.saintdev.project0402.views.windows.dialogs.main.clicklistener.OnYesClickListener;
import kr.saintdev.project0402.views.windows.progress.ProgressManager;

/**
 * Created by 5252b on 2018-04-04.
 */

public class ListCommandActivity extends AppCompatActivity {
    Button startVoiceCommand = null;    // 음성 명령 입력 화면으로 이동
    ListView commandList = null;        // 현재 등록된 계정
    ArrayAdapter adapter = null;

    DialogManager dm = null;
    ProgressManager pm = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commandlist);

        this.startVoiceCommand = findViewById(R.id.list_start_voicecmd);
        this.commandList = findViewById(R.id.list_commands);
        this.dm = new DialogManager(this);
        this.pm = new ProgressManager(this);
        this.adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);

        this.commandList.setAdapter(this.adapter);

        // 명령 목록을 불러옵니다.
        // 내 계정을 불러옵니다.
        Authme me = Authme.getInstance(this);

        HashMap<String, Object> args = new HashMap<>();
        args.put("user_pin", me.getPin());

        HttpWork work = new HttpWork(HttpUrlDefines.LIST_COMMAND, args);
        SingleWorkTask task = new SingleWorkTask(new OnHttpListener());
        task.execute(work);

        this.pm.setMessage("조회 중...");
        this.pm.enable();
    }

    class OnHttpListener implements OnWorkTaskListener, OnYesClickListener {
        @Override
        public void onTaskListener(Work[] result) {
            HttpWork work = (HttpWork) result[0];
            pm.disable();

            if(work.getResultObject().getRequestResultCode() == HttpWork.HttpCodes.HTTP_OK) {
                JSONObject response = work.getResultObject().getJsonObject();

                try {
                    JSONArray orignCommands = response.getJSONArray("orign");
                    JSONArray ids = response.getJSONArray("id");
                } catch(JSONException jex) {
                    jex.printStackTrace();
                }
            } else {
                // 요청 오류
                dm.setTitle("Fatal error.");
                dm.setDescription("데이터 요청에 실패했습니다!");
                dm.setOnYesButtonClickListener(this, "확인");
                dm.show();
            }
        }

        @Override
        public void onProcessedUpdate(int now, int all) {

        }

        @Override
        public void onClick(DialogInterface dialog) {

        }
    }
}
