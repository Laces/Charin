package yuki312.android.charin.demo.model;

import android.net.Uri;
import android.provider.BaseColumns;

public class CharinContract {
    public static final String CONTENT_AUTHORITY = "yuki312.android.charin.demo";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String QUERY_PARAMETER_DISTINCT = "distinct";

    /* - - - - - - - - - - -
     * Bank
     */

    interface BankColumns extends BaseColumns {
        String BANK_MONEY = "bank_money";
        String BANK_MEMO = "bank_memo";
        String BANK_DELETE = "bank_delete";
        String BANK_TIME = "bank_time";
    }

    private static final String PATH_BANK = "banks";

    public static class Banks implements BankColumns {
        public static final int FLAG_DELETED = 1; /* !0 */
        public static final int FLAG_NOT_DELETED = 0;

        public static final String DEFAULT_SORT = BankColumns.BANK_TIME;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BANK).build();

        // Reference columns
        public static final String TYPE_ID = "type_id";

        public static String getBankID(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildBankUri(long bankID) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(bankID)).build();
        }
    }

    /* - - - - - - - - - - -
     * Remind
     */

    interface RemindColumns extends BaseColumns {
        String REMIND_MEMO = "remind_memo";
        String REMIND_DELETE = "remind_delete";
        String REMIND_TIME = "remind_time";
    }

    private static final String PATH_REMIND = "reminds";

    public static class Reminds implements RemindColumns {
        public static final int FLAG_DELETED = 1; /* !0 */
        public static final int FLAG_NOT_DELETED = 0;

        public static final String DEFAULT_SORT = RemindColumns.REMIND_TIME;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REMIND).build();

        // Reference columns
        public static final String TYPE_ID = "type_id";

        public static String getRemindID(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildRemindUri(long remindID) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(remindID)).build();
        }
    }

    /* - - - - - - - - - - -
     * Type
     */

    interface TypeColumns extends BaseColumns {
        String TYPE_SYSTEMID = "type_systemid";
    }

    private static final String PATH_TYPE = "types";

    public static class Types implements TypeColumns {
        public static final String DEFAULT_SORT = TypeColumns._ID;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TYPE).build();

        // No Reference columns

        public static String getTypeID(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildTypeUri(long typeID) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(typeID)).build();
        }
    }
}
