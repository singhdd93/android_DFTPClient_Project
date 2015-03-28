package com.singhdd.dftpclient.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by damandeep on 25/3/15.
 */
public class ServerDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ftpservers.db";


    public ServerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE_SERVERS = "CREATE TABLE "+ FTPServerEntry.TABLE_NAME + " ("+
                FTPServerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                FTPServerEntry.COLUMN_NAME+ " TEXT NOT NULL, "+
                FTPServerEntry.COLUMN_HOST+ " TEXT NOT NULL, "+
                FTPServerEntry.COLUMN_USERNAME+ " TEXT NOT NULL, "+
                FTPServerEntry.COLUMN_PASSWORD+ " TEXT, "+
                FTPServerEntry.COLUMN_PORT+ " INTEGER NOT NULL, "+
                FTPServerEntry.COLUMN_PASSIVE+" BOOLEAN NOT NULL"+
                " );";

        db.execSQL(CREATE_TABLE_SERVERS);

    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+FTPServerEntry.TABLE_NAME);
        onCreate(db);

    }
}
