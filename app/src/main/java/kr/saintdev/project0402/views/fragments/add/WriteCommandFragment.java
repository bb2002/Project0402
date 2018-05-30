package kr.saintdev.project0402.views.fragments.add;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import kr.saintdev.project0402.R;
import kr.saintdev.project0402.views.activitys.AddCommandActivity;
import kr.saintdev.project0402.views.fragments.SuperFragment;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-30
 */

public class WriteCommandFragment extends SuperFragment {
    EditText commandEditor = null;  // 멍령어를 입력 받는 데이터
    Button nextButton = null;       // 다음 버튼

    AddCommandActivity control = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmn_add_command, container, false);
        this.control = (AddCommandActivity) getActivity();

        // 개체 찾기
        this.commandEditor = v.findViewById(R.id.add_command_editor);
        this.nextButton = v.findViewById(R.id.add_command_next);
        this.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String command = commandEditor.getText().toString();
                if(command.length() == 0) {
                    // 명령어가 없습니다.
                    Toast.makeText(getActivity(), "등록할 명령어를 입력하세요.", Toast.LENGTH_SHORT).show();
                    commandEditor.setFocusable(true);
                } else {
                    // 다음 화면으로 이동합니다.
                    control.setTag(command);
                    control.switchFragment(new WriteVoiceFragment());
                }


            }
        });

        return v;
    }
}
