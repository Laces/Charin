/*
 * Copyright 2014 yuki312 All Right Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yuki312.android.charin.demo.tool;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;

public abstract class CursorRecyclerAdapter<VH extends android.support.v7.widget.RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private boolean isDataValid;
    private Context context;
    private Cursor cursor;
    private ChangeObserver contentObserver;
    private DataSetObserver dataSetObserver;

    public CursorRecyclerAdapter(Context context, Cursor cursor) {
        init(context, cursor);
    }

    void init(Context context, Cursor cursor) {
        boolean cursorPresent = cursor != null;
        this.cursor = cursor;
        this.isDataValid = cursorPresent;
        this.context = context;

        this.contentObserver = new ChangeObserver();
        this.dataSetObserver = new MyDataSetObserver();

        if (cursorPresent) {
            if (contentObserver != null) {
                cursor.registerContentObserver(contentObserver);
            }
            if (dataSetObserver != null) {
                cursor.registerDataSetObserver(dataSetObserver);
            }
        }
    }

    @Override
    public final void onBindViewHolder(VH holder, int position) {
        if (!isDataValid) {
            throw new IllegalStateException("Bind view should only be called when the cursor is valid.");
        }
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position);
        }
        onBindViewHolderCursor(holder, cursor);
    }

    /**
     * Like in {@link android.widget.CursorAdapter},
     *
     * @param holder View holder.
     * @param cursor The cursor from which to get the data. The cursor is already
     *               moved to the correct position.
     */
    public abstract void onBindViewHolderCursor(VH holder, Cursor cursor);

    @Override
    public int getItemCount() {
        if (!isDataValid || cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        if (!isDataValid || cursor == null) {
            return 0;
        }
        return (cursor.moveToPosition(position)) ? cursor.getLong(getIdColumnIndex(cursor)) : 0L;
    }

    public Cursor getCursor() {
        return cursor;
    }

    protected int getIdColumnIndex(Cursor cursor) {
        return (cursor != null) ? cursor.getColumnIndexOrThrow(BaseColumns._ID) : -1;
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     *
     * @param newCursor The new cursor to be used
     */
    public void changeCursor(Cursor newCursor) {
        Cursor oldCursor = swapCursor(newCursor);
        if (oldCursor != null) {
            oldCursor.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     *
     * @param newCursor The new cursor to be used.
     * @return Returns the previously set Cursor, or null if there wasa not one.
     * If the given new Cursor is the same instance is the previously set
     * Cursor, null is also returned.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == cursor) {
            return null;
        }
        Cursor oldCursor = cursor;
        if (oldCursor != null) {
            if (contentObserver != null) oldCursor.unregisterContentObserver(contentObserver);
            if (dataSetObserver != null) oldCursor.unregisterDataSetObserver(dataSetObserver);
        }
        cursor = newCursor;
        if (newCursor != null) {
            if (contentObserver != null) newCursor.registerContentObserver(contentObserver);
            if (dataSetObserver != null) newCursor.registerDataSetObserver(dataSetObserver);
            isDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        }
        if (cursor == null) {
            isDataValid = false;
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;
    }

    /**
     * Called when the {@link ContentObserver} on the cursor receives a change notification.
     * Can be implemented by sub-class.
     *
     * @see ContentObserver#onChange(boolean)
     */
    protected void onContentChanged() {

    }

    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onContentChanged();
        }
    }

    private class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            isDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            isDataValid = false;
            // notifyDataSetInvalidated();
            notifyItemRangeRemoved(0, getItemCount());
        }
    }

}