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

package yuki312.android.charin.demo.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

import yuki312.android.charin.demo.model.CharinContract.Banks;

public class ProviderTest extends ProviderTestCase2 {
    private Context context;
    private ContentResolver resolver;

    public void testQueryBank() {
        deleteBankRecords();
        Uri insertedUri = insertBankRecords(100, CharinContract.Banks.FLAG_DELETED, "memo", 0L, 1);
        Cursor cursor = queryBankRecords(Banks.CONTENT_URI);
        cursor.moveToFirst();
        assertEquals("Bank money check", 100, cursor.getInt(cursor.getColumnIndex(Banks.BANK_MONEY)));
    }

    public void testInsertTypeOfSystem() {
        // Prepare all type records deletion.
        deleteTypeRecords();
        Uri insertedUri = insertTypeRecords(1);

        Cursor cursor;

        // test inserted record.
        cursor = queryTypeRecords(insertedUri);
        cursor.moveToFirst();
        assertEquals("Query result count", 1, cursor.getCount());
        assertEquals("System ID check", 1, cursor.getInt(cursor.getColumnIndex(CharinContract.Types.TYPE_SYSTEMID)));
        cursor.close();

        insertedUri = insertTypeRecords(2);

        // test all record.
        cursor = queryTypeRecords(CharinContract.Types.CONTENT_URI);
        cursor.moveToFirst();
        assertEquals("Query result count", 2, cursor.getCount());
        cursor.close();

        // test inserted record.
        cursor = queryTypeRecords(insertedUri);
        cursor.moveToFirst();
        assertEquals("Query result count", 1, cursor.getCount());
        assertEquals("System ID check", 2, cursor.getInt(cursor.getColumnIndex(CharinContract.Types.TYPE_SYSTEMID)));
        cursor.close();
    }

    public void testInsertTypeOfUser() {
        // Prepare all type records deletion.
        deleteTypeRecords();
        Uri insertedUri = insertTypeRecords(-1);

        Cursor cursor;

        // test all record.
        cursor = queryTypeRecords(CharinContract.Types.CONTENT_URI);
        cursor.moveToFirst();
        assertEquals("Query result count", 1, cursor.getCount());
        cursor.close();

        // test inserted record.
        cursor = queryTypeRecords(insertedUri);
        cursor.moveToFirst();
        assertEquals("Query result count", 1, cursor.getCount());
        assertEquals("System ID check", -1, cursor.getInt(cursor.getColumnIndex(CharinContract.Types.TYPE_SYSTEMID)));
        cursor.close();
    }

    public void testInsertBank() {
        // Prepare all bank records
        deleteBankRecords();
        Uri insertedUri = insertBankRecords(100, CharinContract.Banks.FLAG_DELETED, "memo", 0L, 1);

        Cursor cursor;

        // test all record.
        cursor = queryBankRecords(CharinContract.Banks.CONTENT_URI);
        cursor.moveToFirst();
        assertEquals("Query result count", 1, cursor.getCount());
        cursor.close();

        // test inserted record.
        cursor = queryBankRecords(insertedUri);
        cursor.moveToFirst();
        assertEquals("Query result count", 1, cursor.getCount());
        assertEquals("System ID check", 100, cursor.getInt(cursor.getColumnIndex(CharinContract.Banks.BANK_MONEY)));
        cursor.close();
    }

    public void testInsertRemind() {
        // Prepare all bank records
        deleteRemindRecords();
        Uri insertedUri = insertRemindRecords(CharinContract.Reminds.FLAG_DELETED, "memo", 0L, 1);

        Cursor cursor;

        // test all record.
        cursor = queryRemindRecords(CharinContract.Reminds.CONTENT_URI);
        cursor.moveToFirst();
        assertEquals("Query result count", 1, cursor.getCount());
        cursor.close();

        // test inserted record.
        cursor = queryRemindRecords(insertedUri);
        cursor.moveToFirst();
        assertEquals("Query result count", 1, cursor.getCount());
        assertEquals("System ID check", "memo", cursor.getString(cursor.getColumnIndex(CharinContract.Reminds.REMIND_MEMO)));
        cursor.close();
    }

    private Cursor queryBankRecords(Uri contentUri) {
        String[] projection = new String[]{
                CharinContract.Banks._ID,
                CharinContract.Banks.BANK_MONEY,
                CharinContract.Banks.BANK_MEMO,
                CharinContract.Banks.BANK_TIME,
                CharinContract.Banks.BANK_DELETE,
        };
        return resolver.query(contentUri, projection, null, null, null);
    }

    private Cursor queryRemindRecords(Uri contentUri) {
        String[] projection = new String[]{
                CharinContract.Reminds._ID,
                CharinContract.Reminds.REMIND_MEMO,
                CharinContract.Reminds.REMIND_TIME,
                CharinContract.Reminds.REMIND_DELETE,
        };
        return resolver.query(contentUri, projection, null, null, null);
    }

    private Cursor queryTypeRecords(Uri contentUri) {
        String[] projection = new String[]{
                CharinContract.Types._ID,
                CharinContract.Types.TYPE_SYSTEMID
        };
        return resolver.query(contentUri, projection, null, null, null);
    }

    private void deleteTypeRecords() {
        resolver.delete(CharinContract.Types.CONTENT_URI, null, null);
    }

    private void deleteBankRecords() {
        resolver.delete(CharinContract.Banks.CONTENT_URI, null, null);
    }

    private void deleteRemindRecords() {
        resolver.delete(CharinContract.Reminds.CONTENT_URI, null, null);
    }

    private Uri insertBankRecords(Integer money, Integer deleted, String memo, Long time, Integer typeID) {
        ContentValues values = new ContentValues();
        values.put(CharinContract.Banks.BANK_MONEY, money);
        values.put(CharinContract.Banks.BANK_TIME, time);
        values.put(CharinContract.Banks.BANK_DELETE, deleted);
        values.put(CharinContract.Banks.BANK_MEMO, memo);
        values.put(CharinContract.Banks.TYPE_ID, typeID);
        return resolver.insert(CharinContract.Banks.CONTENT_URI, values);
    }

    private Uri insertRemindRecords(Integer deleted, String memo, Long time, Integer typeID) {
        ContentValues values = new ContentValues();
        values.put(CharinContract.Reminds.REMIND_TIME, time);
        values.put(CharinContract.Reminds.REMIND_DELETE, deleted);
        values.put(CharinContract.Reminds.REMIND_MEMO, memo);
        values.put(CharinContract.Reminds.TYPE_ID, typeID);
        return resolver.insert(CharinContract.Reminds.CONTENT_URI, values);
    }

    private Uri insertTypeRecords(Integer systemId) {
        ContentValues values = new ContentValues();
        values.put(CharinContract.Types.TYPE_SYSTEMID, systemId);
        return resolver.insert(CharinContract.Types.CONTENT_URI, values);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = getMockContext();
        resolver = context.getContentResolver();
    }

    public ProviderTest() {
        super(CharinProvider.class, CharinContract.CONTENT_AUTHORITY);
    }

    public ProviderTest(Class providerClass, String providerAuthority) {
        super(providerClass, providerAuthority);
    }
}