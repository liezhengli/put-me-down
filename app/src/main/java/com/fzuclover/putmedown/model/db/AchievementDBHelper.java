package com.fzuclover.putmedown.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lkl on 2016/11/16.
 */

public class AchievementDBHelper extends SQLiteOpenHelper {

    public static final String CREATE_ACHIEVEMENT_TABLE = "create table achievement ("
            +"id integer primary key autoincrement, "
            + "_date text not null, "
            + "total_time_today integer not null, "
            + "success_times integer not null, "
            + "failed_times integer not null)";

    public AchievementDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ACHIEVEMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
