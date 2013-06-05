package com.ipsmarx.dialer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter
{
  public static final String CACHED_NUMBER_LABEL = "no_label";
  static final String[] CALL_LOG_PROJECTION = { "_id", "date", "name", "number", "type", "numbertype", "numberlabel" };
  private static final String DATABASE_CREATE = "create table IF NOT EXISTS callog (_id integer primary key autoincrement, date integer, name text, number text, type integer, no_type integer, no_label text,duration integer,username text);";
  private static final String DATABASE_NAME = "calllogdb";
  private static final String DATABASE_TABLE = "callog";
  private static final int DATABASE_VERSION = 1;
  public static final String DURATION_COLUMN_INDEX = "duration";
  public static final String KEY_CACHED_NAME = "name";
  public static final String KEY_CACHED_NUMBER_TYPE = "no_type";
  public static final String KEY_DATE = "date";
  public static final String KEY_NUMBER = "number";
  public static final String KEY_ROWID = "_id";
  public static final String KEY_TYPE = "type";
  private static final String TAG = "DBAdapter";
  public static final String USER_NAME = "username";
  private DatabaseHelper DBHelper;
  private final Context context;
  private SQLiteDatabase db;

  public DBAdapter(Context paramContext)
  {
    this.context = paramContext;
    this.DBHelper = new DatabaseHelper(this.context);
  }

  public void close()
  {
    this.DBHelper.close();
  }

  public void deleteAll()
  {
    this.db.delete("callog", null, null);
  }

  public boolean deleteCall(long paramLong)
  {
    return this.db.delete("callog", "_id=" + paramLong, null) > 0;
  }

  public Cursor getAllCalls()
  {
    return this.db.query("callog", new String[] { "_id", "date", "name", "number", "type", "no_type", "no_label", "duration", "username" }, null, null, null, null, "dateDESC");
  }

  public Cursor getAllCalls(String paramString)
  {
    return this.db.query("callog", new String[] { "_id", "date", "name", "number", "type", "no_type", "no_label", "duration", "username" }, "username=?", new String[] { paramString }, null, null, "date DESC");
  }

  public Cursor getCall(long paramLong)
    throws SQLException
  {
    Cursor localCursor = this.db.query(true, "callog", new String[] { "_id", "date", "name", "number", "type", "no_type", "no_label", "duration", "username" }, "_id=" + paramLong, null, null, null, null, null);
    if (localCursor != null)
      localCursor.moveToFirst();
    return localCursor;
  }

  public long insertCall(long paramLong1, String paramString1, String paramString2, long paramLong2, Integer paramInteger, String paramString3, long paramLong3, String paramString4)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("date", Long.valueOf(paramLong1));
    localContentValues.put("name", paramString1);
    localContentValues.put("number", paramString2);
    localContentValues.put("type", Long.valueOf(paramLong2));
    localContentValues.put("no_type", paramInteger);
    localContentValues.put("no_label", paramString3);
    localContentValues.put("duration", Long.valueOf(paramLong3));
    localContentValues.put("username", paramString4);
    return this.db.insert("callog", null, localContentValues);
  }

  public boolean isOpen()
  {
    return this.db.isOpen();
  }

  public DBAdapter open()
    throws SQLException
  {
    this.db = this.DBHelper.getWritableDatabase();
    return this;
  }

  public boolean updateCall(long paramLong1, long paramLong2, String paramString1, String paramString2, Integer paramInteger1, Integer paramInteger2, String paramString3, long paramLong3, String paramString4)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("date", Long.valueOf(paramLong2));
    localContentValues.put("name", paramString1);
    localContentValues.put("number", paramString2);
    localContentValues.put("type", paramInteger1);
    localContentValues.put("no_type", paramInteger2);
    localContentValues.put("no_label", paramString3);
    localContentValues.put("duration", Long.valueOf(paramLong3));
    localContentValues.put("username", paramString4);
    return this.db.update("callog", localContentValues, "_id=" + paramLong1, null) > 0;
  }

  private static class DatabaseHelper extends SQLiteOpenHelper
  {
    DatabaseHelper(Context paramContext)
    {
      super("calllogdb", null, 1);
    }

    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      paramSQLiteDatabase.execSQL("create table IF NOT EXISTS callog (_id integer primary key autoincrement, date integer, name text, number text, type integer, no_type integer, no_label text,duration integer,username text);");
    }

    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
    {
      Log.w("DBAdapter", "Upgrading database from version " + paramInt1 + " to " + paramInt2 + ", which will destroy all old data");
      paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS titles");
      onCreate(paramSQLiteDatabase);
    }
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.DBAdapter
 * JD-Core Version:    0.6.2
 */