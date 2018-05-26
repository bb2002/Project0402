package kr.saintdev.project0402.views.fragments;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;

import kr.saintdev.project0402.views.window.dialog.DialogManager;
import kr.saintdev.project0402.views.window.dialog.clicklistener.OnYesClickListener;


/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-10
 */

public class SuperFragment extends Fragment {
    public SuperFragment() {}

    public void onErrorOccurred(Activity control, int title, int content, boolean shutdown) {
        if(control != null) {
            String t = control.getResources().getString(title);
            String m = control.getResources().getString(content);

            DialogManager dm = new DialogManager(control);
            dm.setTitle(t);
            dm.setDescription(m);
            dm.setOnYesButtonClickListener(new OnDialogCloseHandler(shutdown), "OK");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {}


    class OnDialogCloseHandler implements OnYesClickListener {
        boolean isShutdown = false;

        public OnDialogCloseHandler(boolean isShutdown) {
            this.isShutdown = isShutdown;
        }

        @Override
        public void onClick(DialogInterface dialog) {
            dialog.dismiss();
            if(isShutdown) {
                System.exit(0);
            }
        }
    }
}
