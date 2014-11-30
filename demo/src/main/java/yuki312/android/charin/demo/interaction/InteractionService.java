package yuki312.android.charin.demo.interaction;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;

import yuki312.android.charin.demo.model.CharinContract;
import yuki312.android.charin.demo.util.TimeUtils;

import static yuki312.android.charin.demo.util.LogUtils.makeLogTag;

public class InteractionService extends IntentService {
    private static final String TAG = makeLogTag(InteractionService.class);

    public static final String ACTION_REMOTE_INPUT_PAID = "yuki312.android.charin.demo.interaction.action.FOO";

    public static final String EXTRA_PAID_MONEY = "yuki312.android.charin.demo.interaction.extra.PRICE";


    public InteractionService() {
        super("InteractionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        final String action = intent.getAction();
        if (ACTION_REMOTE_INPUT_PAID.equals(action)) {
            handleActionPaid(intent);
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionPaid(Intent intent) {
        Bundle paidIntent = RemoteInput.getResultsFromIntent(intent);
        if (paidIntent == null) {
            throw new IllegalArgumentException("RemoteInput bundle is null.");
        }

        CharSequence extra = paidIntent.getCharSequence(EXTRA_PAID_MONEY);
        if (extra == null) {
            throw new IllegalArgumentException("RemoteInput extra is null.");
        }

        int money = Integer.parseInt(extra.toString());
        ContentValues values = new ContentValues();
        values.put(CharinContract.Banks.BANK_MONEY, money);
        values.put(CharinContract.Banks.BANK_TIME, TimeUtils.getCurrentTime());

        final Context context = getApplicationContext();
        context.getContentResolver().insert(CharinContract.Banks.CONTENT_URI, values);
    }
}
