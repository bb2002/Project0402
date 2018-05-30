package kr.saintdev.project0402.models.datas.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 5252b on 2018-05-04.
 */

public class DBHelper extends SQLiteOpenHelper {
    private SQLiteDatabase readDB = null;
    private SQLiteDatabase writeDB = null;

    public DBHelper(Context context) {
        super(context, "project_narre", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLQuerys.KAKAO_ACCOUNT_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void open() {
        // read 와 write 할 수 있는 db 객체를 가져옵니다.
        this.readDB = getReadableDatabase();
        this.writeDB = getWritableDatabase();
    }

    public Cursor sendReadableQuery(String query) {
        return this.readDB.rawQuery(query, null);
    }

    public void sendWriteableQuery(String query) {
        this.writeDB.execSQL(query);
    }

    public SQLiteDatabase getReadDB() {
        return readDB;
    }

    public SQLiteDatabase getWriteDB() {
        return writeDB;
    }

    public interface SQLQuerys {
        String KAKAO_ACCOUNT_QUERY = "CREATE TABLE narre_user_profile ("+
                "kakao_id TEXT NOT NULL,"+
                "kakao_nickname TEXT NOT NULL,"+
                "kakao_profile TEXT NOT NULL,"+
                "user_pin TEXT NULL" +
                ")";
    }
}
