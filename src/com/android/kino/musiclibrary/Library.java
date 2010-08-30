package com.android.kino.musiclibrary;

import java.io.File;
import java.util.LinkedList;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.android.kino.Kino;
import com.android.kino.logic.AlbumList;
import com.android.kino.logic.AlbumProperties;
import com.android.kino.logic.ArtistList;
import com.android.kino.logic.ArtistProperties;
import com.android.kino.logic.MediaProperties;
import com.android.kino.logic.Playlist;
import com.android.kino.logic.settings.SettingsContainer;
import com.android.kino.logic.settings.SettingsLoader;
import com.android.kino.logic.settings.SettingsContainer.Setting;

public class Library extends Service{
	Kino kino = null;
	LibraryDB db = null;
	IBinder libraryBinder=new LibraryBinder();
	
	final String DBNAME ="MusicLibrary";
	
	@Override
	public IBinder onBind(Intent intent) {						
				
		Log.d("Library","Please 2");
		
		//make sure sd card is mounted 
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Log.e("Library","SD card not mounted! media state: "+Environment.getExternalStorageState());
		}		
		else {
			//setup db
			db = new LibraryDB(this, DBNAME, null, 1);
			
			
			//TODO use kino preferences defined mp3 dir
			/*
			File rootDir = Environment.getExternalStorageDirectory();
			SettingsContainer settings = SettingsLoader.loadCurrentSettings();
			String mediaPath = settings.getConfiguredString(Setting.MEDIA_DIRECTORY);
			File mp3dir = new File(rootDir, mediaPath);
			scanDir(mp3dir, true);
			*/			
		}
		
		return libraryBinder;
	}
	
	
	//scan files, add them to music library
	private void scanDir(File dir, boolean recurse){
		File dirFiles[] = dir.listFiles();
					
		for (File file : dirFiles) {			
			//drill down if recursing
			if (file.isDirectory() && recurse){
				scanDir(file, true);
			}			
			else if (isMediaFile(file)){
				addFileToLibrary(file);
			}
		}
		
	
	}
	
	private boolean isMediaFile(File file){
		String fileName=file.getName();
		if (fileName.substring(fileName.length()-4,fileName.length()).equals(".mp3")){
			return true;
		}
		else{
			return false;
		}
	}
	
	private boolean addFileToLibrary(File file){
		if (!db.songInDb(file)){
		
		MediaProperties mp3file = new MediaProperties(file.getAbsolutePath());

		
		db.addSong(mp3file.Filename,				
				mp3file.Title,
				mp3file.Artist,
				mp3file.Album.Title,
				mp3file.Album.Year,
				mp3file.TrackNumber,
				mp3file.Genre,
				mp3file.Duration,
				mp3file.BitRate);
		//Log.d("Library",file.getAbsolutePath()+": SONG IS NOT IN DB");
		Log.d("Library","added to library: "+file.getAbsolutePath());
		}
		else{
			Log.d("Library",file.getAbsolutePath()+": song is in DB");
		}
		
		return true;
		
	}
			
	private void purgeLibrary(){		
		//TODO 	
	}
	
	private void updateLibray(){		
		//TODO 	
	}	
	
	public Playlist getAllSongs(){
		Playlist playlist = db.fetchAllSongs();
		return playlist;
	}
	
	public Playlist getPlaylistByAlbum(String artistTitle, String albumTitle){
		Playlist playlist = db.fetchSongsByAlbum(artistTitle, albumTitle);
		return playlist;
	}
	
	public ArtistList getAllArtists(){
		ArtistList artists = db.fetchAllArtists();
		return artists;
	}
	
	public AlbumList getAllAlbums(){
		AlbumList albums = db.fetchAllAlbums();
		return albums;
	}
	
	public AlbumList getAlbumsByArtist(String artistTitle){
		AlbumList albums = db.fetchArtistAlbums(artistTitle);
		return albums;
	}
	
	
	public class LibraryBinder extends Binder{
		public Library getLibrary(){
			return Library.this;
		}
	}
		
}
