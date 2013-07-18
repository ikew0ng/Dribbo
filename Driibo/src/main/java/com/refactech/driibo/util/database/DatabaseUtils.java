
package com.refactech.driibo.util.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseUtils {
    public static int queryCount(SQLiteDatabase db, String tableName, String where,
            String[] whereArgs) {
        StringBuffer stringBuffer = new StringBuffer("select count(*) from ");
        stringBuffer.append(tableName);
        if (where != null) {
            stringBuffer.append(" where ");
            stringBuffer.append(where);
        }
        Cursor cursor = db.rawQuery(stringBuffer.toString(), whereArgs);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }
}
