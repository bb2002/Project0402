package kr.saintdev.project0402.views.activitys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import kr.saintdev.project0402.R;
import kr.saintdev.project0402.views.fragments.SuperFragment;
import kr.saintdev.project0402.views.fragments.add.WriteCommandFragment;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-30
 */

public class AddCommandActivity extends AppCompatActivity {
    SuperFragment nowFragment = null;
    Object tag = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_commands);
        switchFragment(new WriteCommandFragment());
    }

    public void switchFragment(SuperFragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.add_command_container, fragment);
        ft.commit();

        this.nowFragment = fragment;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }
}
