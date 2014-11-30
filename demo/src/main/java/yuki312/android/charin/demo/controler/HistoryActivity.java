package yuki312.android.charin.demo.controler;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import yuki312.android.charin.demo.R;
import yuki312.android.charin.demo.interaction.WearableNotification;
import yuki312.android.charin.demo.model.CharinContract.Banks;
import yuki312.android.charin.demo.tool.CursorRecyclerAdapter;
import yuki312.android.charin.demo.tool.SwipeDismissRecyclerViewTouchListener;
import yuki312.android.charin.demo.tool.SwipeDismissRecyclerViewTouchListener.DismissCallbacks;
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

        bankRecyclerView = (RecyclerView) findViewById(R.id.view_pay_list);
        bankLayoutManager = new LinearLayoutManager(this);
        bankRecyclerView.setLayoutManager(bankLayoutManager);
        bankAdapter = new PayAdapter(this, null);
        bankRecyclerView.setAdapter(bankAdapter);
        bankRecyclerView.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public void onRemoveFinished(ViewHolder item) {
                item.itemView.setX(0);
            }
        });
        SwipeDismissRecyclerViewTouchListener touchListener = new SwipeDismissRecyclerViewTouchListener(bankRecyclerView, new DismissCallbacks() {
            @Override
            public boolean canDismiss(int position) {
                return true;
            }

            @Override
            public void onDismiss(RecyclerView recyclerView, int position) {
                recyclerView.invalidate();
                long id = recyclerView.getAdapter().getItemId(position);
                if (RecyclerView.NO_ID != id) {
                    int deleted = getContentResolver().delete(Banks.buildBankUri(id), null, null);
                }
            }
        });
        bankRecyclerView.setOnTouchListener(touchListener);
        bankRecyclerView.setOnScrollListener(touchListener.makeScrollListener());
        // mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,

        getSupportLoaderManager().initLoader(LOADER_ID_REMIND, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("hoge");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ContentValues values = new ContentValues();
        values.put(Banks.BANK_MONEY, 100);
        values.put(Banks.BANK_TIME, 0);
        values.put(Banks.TYPE_ID, 0);
        getContentResolver().insert(Banks.CONTENT_URI, values);
        getContentResolver().insert(Banks.CONTENT_URI, values);
        getContentResolver().insert(Banks.CONTENT_URI, values);
        getSupportLoaderManager().restartLoader(LOADER_ID_REMIND, null, this);

        return true;
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
        bankAdapter.notifyDataSetChanged();
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
            public TextView title;
            public TextView summary;

            public ViewHolder(View v) {
                super(v);
                title = (TextView) v.findViewById(R.id.view_list_title);
                summary = (TextView) v.findViewById(R.id.view_list_summary);
            }
        }

        @Override
        protected void onContentChanged() {

        }

        @Override
        public PayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
            View listItem = inflater.inflate(R.layout.view_hitstory_item, null);
            //listItem.setOnClickListener(this);
            return new ViewHolder(listItem);
        }

        @Override
        public void onBindViewHolderCursor(ViewHolder holder, Cursor cursor) {
            holder.title.setText(cursor.getString(cursor.getColumnIndex(Banks.BANK_MONEY)));
            holder.summary.setText(cursor.getString(cursor.getColumnIndex(Banks._ID)));
        }
    }
}
