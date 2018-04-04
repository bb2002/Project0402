package kr.saintdev.project0402.modules.workspace.task;

import android.os.AsyncTask;

import kr.saintdev.project0402.modules.workspace.work.Work;


/**
 * Created by 5252b on 2018-03-23.
 * Work 객체 1개를 받아 비동기 처리 후 리턴합니다.
 */

public class SingleWorkTask extends AsyncTask<Work, Void, Work> {
    private OnWorkTaskListener listener = null;

    public SingleWorkTask(OnWorkTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(Work work) {
        super.onPostExecute(work);

        if(listener != null) {
            listener.onTaskListener(new Work[]{work});
        }
    }

    @Override
    protected Work doInBackground(Work... works) {
        Work work = works[0];

        work.runtimeCalculation();
        work.run();
        work.runtimeCalculation();

        return work;
    }
}
