package kr.saintdev.project0402.views.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import kr.saintdev.project0402.R;
import kr.saintdev.project0402.views.fragments.SuperFragment;
import kr.saintdev.project0402.views.fragments.auth.LoadingFragment;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @date 2018-05-26
 */

public class AuthActivity extends AppCompatActivity {
    private SuperFragment nowFragment = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // 로딩 화면을 띄웁니다.
        switchFragment(new LoadingFragment());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.nowFragment.onActivityResult(requestCode, resultCode, data);
    }

    public void switchFragment(SuperFragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.auth_container, fragment);
        ft.commit();

        this.nowFragment = fragment;
    }

    public void setActionBarTitle(@Nullable String title) {
        ActionBar bar = getSupportActionBar();
        if(bar != null) {
            if(title == null) {
                bar.hide();
            } else {
                bar.show();
                bar.setTitle(title);
            }
        }
    }
}
