package kr.saintdev.project0402.modules.workspace.task;

import android.os.AsyncTask;

import kr.saintdev.project0402.modules.workspace.work.Work;


/**
 * Created by 5252b on 2018-03-23.
 * Work 객체를 n 개 받아 모두 처리 한후 결과 값을 해당 work 객체에 저장하여 리턴합니다.
 */

public class MultipleWorkTask {
    private OnWorkTaskListener listener = null;
    private BackgroundTask backgroundTask = null;

    public MultipleWorkTask(OnWorkTaskListener listener) {
        this.listener = listener;
    }

    public void execute(Work... works) {
        this.backgroundTask = new BackgroundTask();
        this.backgroundTask.execute(works);
    }

    class BackgroundTask extends AsyncTask<Work, Integer, Void> {
        Work[] works = null;

        @Override
        protected void onProgressUpdate(Integer... args) {
            super.onProgressUpdate(args);

            int nowProcessed = args[0];
            int allProcessed = args[1];

            // 진행 상황을 업데이트 합니다.
            listener.onProcessedUpdate(nowProcessed, allProcessed);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(listener != null) {
                listener.onTaskListener(this.works);
            }
        }

        @Override
        protected Void doInBackground(Work... works) {
            /*
                WorkTask Background execute task
             */

            for(int i = 0; i < works.length; i ++) {
                Work work = works[i];
                synchronized (work) {
                    work.runtimeCalculation();  // 테스크 시작
                    work.run();                 // 테스크 작업
                    work.runtimeCalculation();  // 테스크 종료
                }

                onProgressUpdate(i+1, works.length);
            }

            // 작업 완료된 객체 저장
            this.works = works;
            return null;
        }
    }
}
