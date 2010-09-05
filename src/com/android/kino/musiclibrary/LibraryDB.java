package com.android.kino.musiclibrary;

import java.io.File;
import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.android.kino.logic.AlbumList;
import com.android.kino.logic.AlbumProperties;
import com.android.kino.logic.ArtistList;
import com.android.kino.logic.ArtistProperties;
import com.android.kino.logic.MediaProperties;
import com.android.kino.logic.Playlist;
import com.android.kino.musiclibrary.Library.LibraryBinder;

public class LibraryDB extends SQLiteOpenHelper {

    final String SONG_TABLE="songs";
    final String LIBRARY_DB_CREATE =    
    "CREATE TABLE ["+SONG_TABLE+"] ("+
            "[filename] TEXT  UNIQUE NOT NULL PRIMARY KEY,"+
            "[title] TEXT  NULL,"+
            "[artist] teXT  NULL,"+
            "[albumTitle] TEXT  NULL,"+
            "[albumYear] INTEGER  NULL,"+
            "[trackNumber] INTEGER  NULL,"+
            "[genre] TEXT  NULL,"+
            "[duration] INTEGER  NULL,"+
            "[bitrate] INTEGER  NULL"+
            ")";    
    
    Library library=null;
    SQLiteDatabase MusicLibraryDB=null;
    
    public LibraryDB(Library lib, String name, CursorFactory factory,
            int version) {
        super((Context) lib, name, factory, version);
        MusicLibraryDB=getWritableDatabase();
        Log.d(this.getClass().toString(),"musiclibrary DB fetched");
        library=lib;
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
    }

    //check if song is in DB
    public boolean songInDb(File song) {        
        Cursor cursor = MusicLibraryDB.query(SONG_TABLE,
                                            new String[]{"filename"},
                                            "filename=\""+song.getAbsolutePath()+"\"",
                                            null, null, null, null);
        boolean inDB=cursor.getCount()>0;
        cursor.close();
        return (inDB);        
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
    
    private Playlist playlistFromCursor(Cursor cursor){
    	Playlist playlist = new Playlist();
    	while (!cursor.isAfterLast()){
    		playlist.add(songFromCursor(cursor));
    		cursor.moveToNext();
    	}
    	
    	return playlist;
    }
    private MediaProperties songFromCursor(Cursor cursor){        
		MediaProperties song = new MediaProperties((LibraryBinder) library.libraryBinder,
								cursor.getString(cursor.getColumnIndex("filename")),
								cursor.getString(cursor.getColumnIndex("title")),
								cursor.getString(cursor.getColumnIndex("artist")), 
								cursor.getString(cursor.getColumnIndex("albumTitle")), 
								cursor.getInt(cursor.getColumnIndex("albumYear")), 
								cursor.getInt(cursor.getColumnIndex("trackNumber")), 
								cursor.getString(cursor.getColumnIndex("genre")),
								cursor.getInt(cursor.getColumnIndex("duration")), 
								cursor.getInt(cursor.getColumnIndex("bitrate")) );		
		return song;
    }
    
    public Playlist fetchAllSongs(){
    	Cursor cursor = MusicLibraryDB.query(SONG_TABLE,
                null,
                null,
                null, null, null,
                "title ASC");
    	cursor.moveToFirst();
    	
    	Playlist playlist = playlistFromCursor(cursor);
    	
    	cursor.close();
    	
    	return playlist;
    }
    
    public LinkedList<String> fetchAllSongFilenames(){
    	String[] queryColumns = {"filename"};
    	
    	LinkedList<String> songFilenames = new LinkedList<String>();
    	
    	Cursor cursor = MusicLibraryDB.query(SONG_TABLE, //from
                queryColumns, //select
                null,
                null,
                null,
                null,
                null,
                null);
    	cursor.moveToFirst();
  
       	while (!cursor.isAfterLast()){    		
       		songFilenames.add(cursor.getString(cursor.getColumnIndex("filename")));
    		cursor.moveToNext();
    	}
       	
       	cursor.close();
  
    	return songFilenames;
    }
    
    public void removeSong(String filename){    	
    	MusicLibraryDB.delete (SONG_TABLE, //from
    				"filename=\""+filename+"\"", //where
    				null //args ?
    				);
    	Log.d(this.getClass().toString(),"removed song from library: "+filename);
    }
    
    public void removeAllSongs(){
    	MusicLibraryDB.delete (SONG_TABLE, //from
				null, //where
				null //args ?
				);
	Log.d(this.getClass().toString(),"removed all songs from library!");
    }
    
    public Playlist fetchSongsByAlbum(String artistTitle,String albumTitle){
    	Cursor cursor = MusicLibraryDB.query(SONG_TABLE,
                null,
                "artist=\""+artistTitle+"\" AND "+"albumTitle=\""+albumTitle+"\"",//where
                null, null, null,
                "trackNumber ASC");
    	cursor.moveToFirst();
    	
    	Playlist playlist = playlistFromCursor(cursor);    	    
    	cursor.close();
    	
    	playlist.setAlbumTitle(albumTitle);
    	playlist.setArtistTitle(artistTitle);
    	//TODO ugly... find a better solution
    	playlist.setAlbumYear(playlist.getFirst().Album.getAlbumYear());
    	
    	return playlist;
    }
     
    
    public ArtistList fetchAllArtists(){
    	ArtistList artists=new ArtistList();
    	String[] queryColumns = {"artist","COUNT(*) as totalSongs"};
    	
    	Cursor cursor = MusicLibraryDB.query(SONG_TABLE, //select
    			queryColumns, //from
                null, //where
                null, //args?
                "artist", //groupby
                null, //having
                "artist ASC" //orderby
                );
    	cursor.moveToFirst();
    	
    	while (!cursor.isAfterLast()){
    		ArtistProperties artist=library.getArtistFromCache(
    														cursor.getString(cursor.getColumnIndex("artist")),
    														Integer.parseInt(cursor.getString(cursor.getColumnIndex("totalSongs")))
    														);
    		artists.add(artist);
    		cursor.moveToNext();
    	}
    	
    	cursor.close();
    	
    	return artists;
    }
    
    public AlbumList fetchAllAlbums(){
    	AlbumList albums=new AlbumList();
    	String[] queryColumns = {"albumTitle","artist","albumYear"};
    	
    	Cursor cursor = MusicLibraryDB.query(SONG_TABLE, //select
    			queryColumns, //from
                null, //where
                null, //args?
                "albumTitle", //groupby
                null, //having
                "albumTitle ASC" //orderby
                );
    	cursor.moveToFirst();
    	
    	while (!cursor.isAfterLast()){
    		AlbumProperties album=library.getAlbumFromCache(
    									cursor.getString(cursor.getColumnIndex("artist")),
					    				cursor.getString(cursor.getColumnIndex("albumTitle")),					    				
					    				Integer.parseInt(cursor.getString(cursor.getColumnIndex("albumYear")))
					    				);
    		albums.add(album);
    		cursor.moveToNext();
    	}
    	
    	cursor.close();
    	
    	return albums;
    }
    
    public AlbumList fetchArtistAlbums(String artistTitle){
    	AlbumList albums=new AlbumList(artistTitle);
    	String[] queryColumns = {"albumTitle","artist","albumYear"};
    	
    	Cursor cursor = MusicLibraryDB.query(SONG_TABLE, //select
    			queryColumns, //from
                "artist=\""+artistTitle+"\"", //where
                null, //args?
                "albumTitle", //groupby
                null, //having
                "albumTitle ASC" //orderby
                );
    	cursor.moveToFirst();
    	
    	while (!cursor.isAfterLast()){
    		AlbumProperties album=library.getAlbumFromCache(
    									cursor.getString(cursor.getColumnIndex("artist")),
					    				cursor.getString(cursor.getColumnIndex("albumTitle")),					    				
					    				Integer.parseInt(cursor.getString(cursor.getColumnIndex("albumYear")))
					    				);
    		albums.add(album);
    		cursor.moveToNext();
    	}
    	
    	cursor.close();
    	
    	return albums;
    }

}
