package com.android.kino.musiclibrary;

import java.io.File;
import java.util.HashMap;

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
import com.android.kino.utils.ConvertUtils;

public class Library extends Service{
	Kino kino = null;
	LibraryDB db = null;
	IBinder libraryBinder=new LibraryBinder();
	
	HashMap<String,ArtistProperties> artistCache = new HashMap<String,ArtistProperties>();
	HashMap<String,AlbumProperties> albumCache = new HashMap<String,AlbumProperties>();
			
	final String DBNAME ="MusicLibrary";
	
	public ArtistProperties getArtistFromCache(String artistTitle){		
		ArtistProperties artist=null;
		if (!artistCache.containsKey(artistTitle)){		
			artistCache.put(artistTitle, new ArtistProperties(artistTitle) );
		}
		artist = artistCache.get(artistTitle);		
		return artist;
	}
	
	public ArtistProperties getArtistFromCache(String artistTitle, int totalSongs) {
		ArtistProperties artist = getArtistFromCache(artistTitle);
		artist.setTotalSongs(totalSongs);
		
		return artist;
	}
	
	public AlbumProperties getAlbumFromCache(String artistTitle, String albumTitle, int albumYear){
		AlbumProperties album=null;
		if (!albumCache.containsKey(getAlbumCacheKey(artistTitle,albumTitle) )){		
			albumCache.put(getAlbumCacheKey(artistTitle,albumTitle), new AlbumProperties(albumTitle,artistTitle,albumYear) );
		}
		album = albumCache.get(getAlbumCacheKey(artistTitle,albumTitle));
		return album;
	}	
	
	private String getAlbumCacheKey(String artistTitle, String albumTitle){
		return artistTitle+"-"+albumTitle;
	}
	
	public static String getAlbumFileName(String artistTitle, String albumTitle){
		return ConvertUtils.safeFileName(artistTitle)+"-"+ConvertUtils.safeFileName(albumTitle);
	}
	
	@Override
	public IBinder onBind(Intent intent) {						
						
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
		
		MediaProperties mp3file = new MediaProperties(file.getAbsolutePath(),(LibraryBinder) this.libraryBinder);

		
		db.addSong(mp3file.Filename,				
				mp3file.Title,
				mp3file.Artist,
				mp3file.Album.getAlbumName(),
				mp3file.Album.getAlbumYear(),
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
