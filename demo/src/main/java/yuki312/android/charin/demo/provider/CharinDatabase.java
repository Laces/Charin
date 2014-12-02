package yuki312.android.charin.demo.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import yuki312.android.charin.demo.provider.CharinContract.BankColumns;
import yuki312.android.charin.demo.provider.CharinContract.Banks;
import yuki312.android.charin.demo.provider.CharinContract.RemindColumns;
import yuki312.android.charin.demo.provider.CharinContract.Reminds;
import yuki312.android.charin.demo.util.LogUtils;

import static yuki312.android.charin.demo.util.LogUtils.LOGE;
import static yuki312.android.charin.demo.util.LogUtils.LOGI;

public class CharinDatabase extends SQLiteOpenHelper {
    private static final String TAG = LogUtils.makeLogTag(CharinDatabase.class);

    private static final String DATABASE_NAME = "Charin.db";
    private static final int DATABASE_VERSION = 1;

    interface Tables {
        String Bank = "bank";
        String Remind = "remind";
        String Type = "type";
    }

    private interface References {
        String TYPE_ID = "REFERENCES " + Tables.Type + "(" + CharinContract.Types._ID + ")";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();

            db.execSQL("CREATE TABLE " + Tables.Type + " ("
                    + CharinContract.TypeColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + CharinContract.TypeColumns.TYPE_SYSTEMID + " INTEGER NOT NULL DEFAULT -1"
                    + ")");

            db.execSQL("CREATE TABLE " + Tables.Bank + " ("
                    + BankColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + BankColumns.BANK_MONEY + " INTEGER NOT NULL DEFAULT 0,"
                    + BankColumns.BANK_MEMO + " TEXT NOT NULL DEFAULT '',"
                    + BankColumns.BANK_DELETE + " INTEGER DEFAULT 0,"
                    + BankColumns.BANK_TIME + " INTEGER NOT NULL,"
                    + Banks.TYPE_ID + " INTEGER NOT NULL " + References.TYPE_ID
                    + ")");

            db.execSQL("CREATE TABLE " + Tables.Remind + " ("
                    + RemindColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + RemindColumns.REMIND_MEMO + " TEXT NOT NULL DEFAULT '',"
                    + RemindColumns.REMIND_DELETE + " INTEGER DEFAULT 0,"
                    + RemindColumns.REMIND_TIME + " INTEGER NOT NULL,"
                    + Reminds.TYPE_ID + " INTEGER NOT NULL " + References.TYPE_ID
                    + ")");

            db.setTransactionSuccessful();
            LOGI(TAG, "Create Database Succeed");

        } catch (RuntimeException ex) {
            // TODO. Create database failed sequence.
            LOGE(TAG, "Create Database Failed.");
            throw ex;

        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: update database.
    }

    private static CharinDatabase singleton = null;

    private CharinDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized CharinDatabase getInstance(Context context) {
        if (singleton == null) {
            singleton = new CharinDatabase(context);
        }
        return singleton;
    }
}
