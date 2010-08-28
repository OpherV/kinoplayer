package com.android.kino.musiclibrary;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class LibraryDB extends SQLiteOpenHelper {

    final String SONG_TABLE="songs";
    final String LIBRARY_DB_CREATE =    
    "CREATE TABLE ["+SONG_TABLE+"] ("+
            "[filename] TEXT  UNIQUE NOT NULL PRIMARY KEY,"+
            "[title] TEXT  NULL,"+
            "[artist] teXT  NULL,"+
            "[albumTitle] TEXT  NULL,"+
            "[albumYear] INTEGER  NULL,"+
            "[tracknumber] INTEGER  NULL,"+
            "[genre] TEXT  NULL,"+
            "[duration] INTEGER  NULL,"+
            "[bitrate] INTEGER  NULL"+
            ")";    
    
    
    SQLiteDatabase MusicLibraryDB=null;
    
    public LibraryDB(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
        MusicLibraryDB=getWritableDatabase();
        Log.d(this.getClass().toString(),"musiclibrary DB fetched");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        MusicLibraryDB=db;
        
        try{
            db.execSQL(LIBRARY_DB_CREATE);
            Log.d(this.getClass().toString(),"musiclibrary created tables");
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        
    }

    //check if song is in DB
    public boolean songInDb(File song) {        
        Cursor cursor = MusicLibraryDB.query(SONG_TABLE,
                                            new String[]{"filename"},
                                            "filename=\""+song.getAbsolutePath()+"\"",
                                            null, null, null, null);
        return (cursor.getCount()>0);        
    }
    
    
    //add new song to db
    public boolean addSong(String filename,
                           String title,
                           String artist,                           
                           String albumTitle,
                           int albumYear,
                           int trackNumber,
                           String genre,
                           int duration,
                           int bitrate) {
        
        ContentValues songTableValues=createSongTableValues(
                filename,                
                title,
                artist,
                albumTitle,
                albumYear,
                trackNumber,
                genre,
                duration,
                bitrate);
        
        MusicLibraryDB.insertOrThrow(SONG_TABLE, "", songTableValues);
        return true;
    }

    private ContentValues createSongTableValues(String filename, String title,
            String artist, String albumTitle, int albumYear, int trackNumber,
            String genre, int duration, int bitrate) {
        
        ContentValues contentValues = new ContentValues();
        contentValues.put("filename",filename);
        contentValues.put("title",title);
        contentValues.put("artist",artist);
        contentValues.put("albumTitle",albumTitle);
        contentValues.put("albumYear",albumYear);        
        contentValues.put("trackNumber",trackNumber);
        contentValues.put("genre",genre);
        contentValues.put("duration",duration);
        contentValues.put("bitrate",bitrate);        
        
        return contentValues;
    }

}
