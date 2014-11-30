package yuki312.android.charin.demo.controler;

import android.app.Service;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import yuki312.android.charin.demo.R;
import yuki312.android.charin.demo.interaction.WearableNotification;
import yuki312.android.charin.demo.model.CharinContract;
import yuki312.android.charin.demo.model.CharinContract.Banks;
import yuki312.android.charin.demo.tool.CursorRecyclerAdapter;
import yuki312.android.charin.demo.util.LogUtils;

import static yuki312.android.charin.demo.util.LogUtils.LOGW;

public class HistoryActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = LogUtils.makeLogTag(HistoryActivity.class);

    private RecyclerView bankRecyclerView;
    private RecyclerView.LayoutManager bankLayoutManager;
    private PayAdapter bankAdapter;

    private static final int LOADER_ID_REMIND = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.view_toolbar);
        setSupportActionBar(toolbar);

        bankLayoutManager = new LinearLayoutManager(this);
        bankAdapter = new PayAdapter(this, null);
        bankRecyclerView = (RecyclerView) findViewById(R.id.view_pay_list);
        bankRecyclerView.setLayoutManager(bankLayoutManager);
        bankRecyclerView.setAdapter(bankAdapter);

        getSupportLoaderManager().initLoader(LOADER_ID_REMIND, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if (id == LOADER_ID_REMIND) {
            return new CursorLoader(this, Banks.CONTENT_URI,
                    new String[]{Banks._ID, Banks.BANK_MONEY, Banks.BANK_TIME}, null, null, Banks.DEFAULT_SORT);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        bankAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    public static class PayAdapter extends CursorRecyclerAdapter<PayAdapter.ViewHolder>
            implements View.OnClickListener {

        public PayAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public void onClick(View v) {
            if (v == null) {
                LOGW(TAG, "PayアイテムのonClick引数vがnull");
                return;
            } else if (!(v.getTag() instanceof Integer)) {
                LOGW(TAG, "PayアイテムのonClick引数v.getTagが非int型");
                return;
            }
            new WearableNotification().notify(v.getContext());
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;

            public ViewHolder(View v) {
                super(v);
                mTextView = (TextView) v.findViewById(R.id.view_list_title);
            }
        }

        @Override
        public PayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
            View listItem = inflater.inflate(R.layout.view_hitstory_item, null);
            listItem.setOnClickListener(this);
            return new ViewHolder(listItem);
        }

        @Override
        public void onBindViewHolderCursor(ViewHolder holder, Cursor cursor) {
            holder.mTextView.setText(cursor.getString(cursor.getColumnIndex(Banks.BANK_MONEY)));
            holder.mTextView.setTag(cursor.getString(cursor.getColumnIndex(CharinContract.Banks.BANK_MONEY)));
            holder.mTextView.setOnClickListener(this);
        }
    }
}
