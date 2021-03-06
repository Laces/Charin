package yuki312.android.charin.demo.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;

import yuki312.android.charin.demo.provider.CharinContract.Banks;
import yuki312.android.charin.demo.provider.CharinContract.Reminds;
import yuki312.android.charin.demo.provider.CharinContract.Types;
import yuki312.android.charin.demo.provider.CharinDatabase.Tables;
import yuki312.android.charin.demo.tool.SelectionBuilder;
import yuki312.android.charin.demo.util.LogUtils;

import static yuki312.android.charin.demo.util.LogUtils.LOGV;

public class CharinProvider extends ContentProvider {
    private static final String TAG = LogUtils.makeLogTag(CharinProvider.class);

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private CharinDatabase dbOpenHelper;

    private static final int BANKS = 100;
    private static final int BANKS_ID = 101;
    private static final int REMINDS = 200;
    private static final int REMINDS_ID = 201;
    private static final int TYPES = 300;
    private static final int TYPES_ID = 301;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CharinContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "banks", BANKS);
        matcher.addURI(authority, "banks/*", BANKS_ID);
        matcher.addURI(authority, "reminds", REMINDS);
        matcher.addURI(authority, "reminds/*", REMINDS_ID);
        matcher.addURI(authority, "types", TYPES);
        matcher.addURI(authority, "types/*", TYPES_ID);

        return matcher;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);

        if (LogUtils.VERBOSE(TAG)) {
            LOGV(TAG, "URI=" + uri
                    + " Match=" + match
                    + " Selection=" + selection
                    + " args=" + Arrays.toString(selectionArgs));
        }

        final SelectionBuilder builder = buildSimpleSelection(uri, match);
        int deleted = builder.where(selection, selectionArgs).delete(db);
        notifyChange(uri);

        return deleted;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);

        if (LogUtils.VERBOSE(TAG)) {
            LOGV(TAG, "URI=" + uri
                    + " Match=" + match
                    + " Values=" + values.toString());
        }

        switch (match) {
            case BANKS: {
                long id = db.insertOrThrow(Tables.Bank, null, values);
                notifyChange(uri);
                return Banks.buildBankUri(id);
            }
            case REMINDS: {
                long id = db.insertOrThrow(Tables.Remind, null, values);
                notifyChange(uri);
                return Reminds.buildRemindUri(id);
            }
            case TYPES: {
                long id = db.insertOrThrow(Tables.Type, null, values);
                notifyChange(uri);
                return Types.buildTypeUri(id);
            }
            default: {
                throw new UnsupportedOperationException("Unknown Operation match=" + match + ", uri=" + uri);
            }
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        final int match = uriMatcher.match(uri);

        if (LogUtils.VERBOSE(TAG)) {
            LOGV(TAG, "URI=" + uri
                    + " Match=" + match
                    + " Projection=" + Arrays.toString(projection)
                    + " Selection=" + selection
                    + " args=" + Arrays.toString(selectionArgs));
        }

        boolean distinct = !TextUtils.isEmpty(
                uri.getQueryParameter(CharinContract.QUERY_PARAMETER_DISTINCT));

        final SelectionBuilder builder = buildSimpleSelection(uri, match);
        Cursor cursor = builder.where(selection, selectionArgs)
                .query(db, distinct, projection, sortOrder, null);

        Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;

    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        dbOpenHelper = CharinDatabase.getInstance(getContext());
        return true;
    }

    private SelectionBuilder buildSimpleSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();

        switch (match) {
            case BANKS: {
                return builder.table(Tables.Bank);
            }
            case BANKS_ID: {
                return builder.table(Tables.Bank).where(Banks._ID + "=?", Banks.getBankID(uri));
            }
            case REMINDS: {
                return builder.table(Tables.Remind);
            }
            case REMINDS_ID: {
                return builder.table(Tables.Remind).where(Reminds._ID + "=?", Reminds.getRemindID(uri));
            }
            case TYPES: {
                return builder.table(Tables.Type);
            }
            case TYPES_ID: {
                return builder.table(Tables.Type).where(Types._ID + "=?", Types.getTypeID(uri));
            }
            default: {
                throw new UnsupportedOperationException("Unknown Operation match=" + match + ", uri=" + uri);
            }
        }
    }

    private void notifyChange(Uri uri) {
        Context context = getContext();
        context.getContentResolver().notifyChange(uri, null);
//        // Widgets can't register content observers so we refresh widgets separately.
//        context.sendBroadcast(ScheduleWidgetProvider.getRefreshBroadcastIntent(context, false));
    }
}
