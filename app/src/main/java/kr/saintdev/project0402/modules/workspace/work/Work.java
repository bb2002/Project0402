package kr.saintdev.project0402.modules.workspace.work;

/**
 * Created by 5252b on 2018-03-23.
 * 하나의 작업을 의미합니다.
 */

public abstract class Work<T> {
    private T resultObject = null;  // 결과 반환 객체
    private long executeTime = -1;   // 처리 하는 동안걸린 시간

    public abstract void run();

    protected void setReturnObject(T obj) {
        // 처리 완료 후 이 메서드를 통해 결과값을 저장합니다.
        this.resultObject = obj;
    }

    public T getResultObject() {
        return this.resultObject;
    }

    public void runtimeCalculation() {
        // 작업이 시작할 때, 끝날때 한번씩 호출해주면
        // 처리 시간을 계산해줍니다.

        if(executeTime == -1) {
            // 처리 시작
            this.executeTime = System.currentTimeMillis();
        } else {
            // 처리 완료
            this.executeTime = System.currentTimeMillis() - this.executeTime;
        }
    }

    public long getExecuteTime() {
        return this.executeTime;
    }
}
