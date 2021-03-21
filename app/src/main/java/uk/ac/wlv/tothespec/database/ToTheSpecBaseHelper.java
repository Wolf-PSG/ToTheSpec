package uk.ac.wlv.tothespec.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ToTheSpecBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "specBase.db";
    public ToTheSpecBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ToTheSpecDbSchema.SpecTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                        ToTheSpecDbSchema.SpecTable.Cols.UUID + ", " +
                        ToTheSpecDbSchema.SpecTable.Cols.MESSAGE + ", " +
                        ToTheSpecDbSchema.SpecTable.Cols.URL +
                ")"
                );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
