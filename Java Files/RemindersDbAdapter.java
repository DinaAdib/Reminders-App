package com.example.dinaadeb.reminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



public class RemindersDbAdapter {

    //these are the column names
    public static final String COL_ID = "_id";
    public static final String COL_CONTENT = "content";
    public static final String COL_IMPORTANT = "important";
    //these are the corresponding indices
    public static final int INDEX_ID = 0;
    public static final int INDEX_CONTENT = INDEX_ID + 1;
    public static final int INDEX_IMPORTANT = INDEX_ID + 2;
    //used for logging
    private static final String TAG = "RemindersDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final String DATABASE_NAME = "dba_remdrs";
    private static final String TABLE_NAME = "tbl_remdrs";
    private static final int DATABASE_VERSION = 1;
    private final Context mCtx;
    //SQL statement used to create the database
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                    COL_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    COL_CONTENT + " TEXT, " +
                    COL_IMPORTANT + " INTEGER );";


    public RemindersDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    //open
    public void open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
    }
    //close
    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }


    //TODO implement the function createReminder() which take the name as the content of the reminder and boolean important...note that the id will be created for you automatically
    public void createReminder(String name, int Important) {
        String Insert_Reminder =
                "INSERT INTO " + TABLE_NAME + " ( " +
                        COL_CONTENT + ", " +
                        COL_IMPORTANT + ") VALUES( '" + name + "' , " + Important + " );";
        mDb.execSQL(Insert_Reminder);

    }
    //TODO overloaded to take a reminder
    public long createReminder(Reminder reminder) {
        String Insert_Reminder =
                "INSERT INTO " + TABLE_NAME + " ( " +
                        COL_CONTENT + ", " +
                        COL_IMPORTANT + ") VALUES( '" + reminder.getContent() + "', " + reminder.getImportant() + " );";
        // Log.w(TAG, Insert_Reminder);
        mDb.execSQL(Insert_Reminder);
        return 0;
    }

    //TODO implement the function fetchReminderById() to get a certain reminder given its id
    public Reminder fetchReminderById(int id) {
        Reminder reminderFetched = null;
        String Select_Reminder =
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ID +" = " + id + ";";
        Cursor c = mDb.rawQuery(Select_Reminder, null);
        if(c!=null && c.getCount()!=0) {

            if (c.moveToFirst()) {
                reminderFetched = new Reminder(0, "", 0);
                reminderFetched.setId(Integer.parseInt(c.getString(0)));
                reminderFetched.setContent(c.getString(1));
                reminderFetched.setImportant(Integer.parseInt(c.getString(2)));
            }
        }
        return reminderFetched;
    }

    public Cursor fetchAllReminders() {
        Cursor c = mDb.rawQuery("SELECT  * FROM " + TABLE_NAME, null);
        return c;
    }

    public void updateReminder(Reminder reminder) {
        String Update_Reminder =
                "UPDATE " + TABLE_NAME + " SET " + COL_CONTENT + " = '" + reminder.getContent() + "', " +
                        COL_IMPORTANT + " = " + reminder.getImportant()+ " WHERE " +  COL_ID + " = " + reminder.getId() +";";
       mDb.execSQL(Update_Reminder);
    }

    public void deleteReminderById(int nId) {
        String Delete_Reminder =
                "DELETE FROM " + TABLE_NAME + " WHERE " +  COL_ID + " = " + nId +";";
        mDb.execSQL(Delete_Reminder);
    }

    public void deleteAllReminders() {
        String DeleteAll_Reminder =
                "DELETE FROM " + TABLE_NAME + ";";
        mDb.execSQL(DeleteAll_Reminder);
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }


}
