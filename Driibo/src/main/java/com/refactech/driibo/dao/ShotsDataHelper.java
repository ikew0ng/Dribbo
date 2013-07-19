
package com.refactech.driibo.dao;

import com.refactech.driibo.type.dribble.Category;
import com.refactech.driibo.type.dribble.Shot;
import com.refactech.driibo.util.database.Column;
import com.refactech.driibo.util.database.SQLiteTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.content.CursorLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Issac on 7/18/13.
 */
public class ShotsDataHelper extends BaseDataHelper {
    private Category mCategory;

    public ShotsDataHelper(Context context, Category category) {
        super(context);
        mCategory = category;
    }

    @Override
    protected Uri getContentUri() {
        return DataProvider.SHOTS_CONTENT_URI;
    }

    private ContentValues getContentValues(Shot shot) {
        ContentValues values = new ContentValues();
        values.put(ShotsDBInfo.ID, shot.getId());
        values.put(ShotsDBInfo.CATEGORY, mCategory.ordinal());
        values.put(ShotsDBInfo.JSON, shot.toJson());
        return values;
    }

    public Shot query(long id) {
        Shot shot = null;
        Cursor cursor = query(null, ShotsDBInfo.CATEGORY + "=?" + " AND " + ShotsDBInfo.ID + "= ?",
                new String[] {
                        String.valueOf(mCategory.ordinal()), String.valueOf(id)
                }, null);
        if (cursor.moveToFirst()) {
            shot = Shot.fromCursor(cursor);
        }
        cursor.close();
        return shot;
    }

    public void bulkInsert(List<Shot> shots) {
        ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
        for (Shot shot : shots) {
            ContentValues values = getContentValues(shot);
            contentValues.add(values);
        }
        ContentValues[] valueArray = new ContentValues[contentValues.size()];
        bulkInsert(contentValues.toArray(valueArray));
    }

    public int deleteAll() {
        synchronized (DataProvider.DBLock) {
            DataProvider.DBHelper mDBHelper = DataProvider.getDBHelper();
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            int row = db.delete(ShotsDBInfo.TABLE_NAME, ShotsDBInfo.CATEGORY + "=?", new String[] {
                String.valueOf(mCategory.ordinal())
            });
            return row;
        }
    }

    public CursorLoader getCursorLoader() {
        return new CursorLoader(getContext(), getContentUri(), null, ShotsDBInfo.CATEGORY + "=?",
                new String[] {
                    String.valueOf(mCategory.ordinal())
                }, ShotsDBInfo._ID + " ASC");
    }

    public static final class ShotsDBInfo implements BaseColumns {
        private ShotsDBInfo() {
        }

        public static final String TABLE_NAME = "shots";

        public static final String ID = "id";

        public static final String CATEGORY = "category";

        public static final String JSON = "json";

        public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME)
                .addColumn(ID, Column.DataType.INTEGER)
                .addColumn(CATEGORY, Column.DataType.INTEGER).addColumn(JSON, Column.DataType.TEXT);
    }
}
