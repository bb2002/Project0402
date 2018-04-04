package kr.saintdev.project0402.modules.workspace.task;


import kr.saintdev.project0402.modules.workspace.work.Work;

/**
 * Created by 5252b on 2018-03-23.
 */

public interface OnWorkTaskListener {
    void onTaskListener(Work[] result);
    void onProcessedUpdate(int now, int all);
}
