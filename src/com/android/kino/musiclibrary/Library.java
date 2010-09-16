package com.android.kino.musiclibrary;

import java.io.Externalizable;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
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
	IBinder libraryBinder = new LibraryBinder();
	
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
			verifyDirs();
		}
		
		return libraryBinder;
	}
	
	private void verifyDirs(){
		File kinoDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+Kino.KINODIR);
		if (!kinoDir.exists()){
			Log.d(this.getClass().getName(),kinoDir.getAbsolutePath()+" does not exist. creating.");
			kinoDir.mkdir();
		}
		
		File ImagesDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+Kino.IMAGES_DIR);
		if (!ImagesDir.exists()){
			Log.d(this.getClass().getName(),ImagesDir.getAbsolutePath()+" does not exist. creating.");
			ImagesDir.mkdir();
		}
		
		File ArtistDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+Kino.ALBUM_DIR);
		if (!ArtistDir.exists()){
			Log.d(this.getClass().getName(),ArtistDir.getAbsolutePath()+" does not exist. creating.");
			ArtistDir.mkdir();
		}
		
		File AlbumDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+Kino.ARTIST_DIR);
		if (!AlbumDir.exists()){
			Log.d(this.getClass().getName(),AlbumDir.getAbsolutePath()+" does not exist. creating.");
			AlbumDir.mkdir();
		}
	}
	
	public void updateLibrary(LibraryStatusUpdater updater){
		cleanLibrary(updater);
		
		
		//TODO or's settings are broken. use the android prefs.
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);        
        String musicDir = prefs.getString("musicDir", Environment.getExternalStorageDirectory().getPath()+"/"+ Kino.MUSIC_DIR);
		
		String mediaPath = musicDir;
		File mp3dir = new File(mediaPath);
		scanDir(mp3dir, true, updater);		
	}
	
	//removes files that are longer present on the media
	public void cleanLibrary(LibraryStatusUpdater updater){
		LinkedList<String> filenames= db.fetchAllSongFilenames();
		
		for (String filename: filenames){
			File fileToCheck = new File(filename);
			if (updater!=null){
				updater.updateProgress("verifying "+filename);
			}
			if (!fileToCheck.exists()){
				Log.e(this.getClass().getName(),"file "+fileToCheck.getAbsolutePath()+" in DB but not on SD!");
				db.removeSong(filename);
			}
		}
		
	}
	
	//clears the album dir of images
	public void cleanAlbumDir(LibraryStatusUpdater updater){
		File rootDir = Environment.getExternalStorageDirectory();		
		String albumImagesPath = rootDir.getAbsolutePath()+"/"+Kino.ALBUM_DIR;
		File albumImagesDir = new File(albumImagesPath);
		
		for (File file : albumImagesDir.listFiles()){
			updater.updateProgress("Deleting: "+file.getAbsolutePath());
			file.delete();
		}
		
		Collection<AlbumProperties> albums = albumCache.values();
		for (AlbumProperties album : albums){
			updater.updateProgress("Removing image from cache: "+album.getAlbumName()+" - "+album.getArtistName());
			album.setImage(null);
		}
	}
	
	//clears the artist dir of images
	public void cleanArtistDir(LibraryStatusUpdater updater){
		File rootDir = Environment.getExternalStorageDirectory();		
		String artistImagesPath = rootDir.getAbsolutePath()+"/"+Kino.ARTIST_DIR;
		File artistImagesDir = new File(artistImagesPath);
		
		for (File file : artistImagesDir.listFiles()){
			updater.updateProgress("Deleting: "+file.getAbsolutePath());
			file.delete();
		}
		
		Collection<ArtistProperties> artists= artistCache.values();
		for (ArtistProperties artist: artists){
			updater.updateProgress("Removing image from cache: "+artist.getName());
			artist.setImage(null);
		}
	}
	
	public void purgeLibrary(){
		db.removeAllSongs();
	}
	
	
	//scan files, add them to music library
	private void scanDir(File dir, boolean recurse, LibraryStatusUpdater updater){
		File dirFiles[] = dir.listFiles();

		if (updater!=null){
			updater.updateProgress("scanning "+dir.getAbsolutePath().toString());
		}
		
		for (File file : dirFiles) {			
			//drill down if recursing
			if (file.isDirectory() && recurse){
				scanDir(file, true, updater);
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
		if (mp3file.Title==null){
			mp3file.Title="Unknown Title";
			Log.e(this.getClass().getName(),"Title null on "+file.getAbsolutePath());
		}
		
		if (mp3file.Artist==null){
			mp3file.Artist="Unknown Artist";
			Log.e(this.getClass().getName(),"Artist null on "+file.getAbsolutePath());
		}
		
		if (mp3file.Album.getAlbumName()==null){
			mp3file.Album.setTitle("Unknown Album");
			Log.e(this.getClass().getName(),"Album null on "+file.getAbsolutePath());
		}
		
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
			return false;
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
	
	public static abstract class LibraryStatusUpdater{		
		abstract public void updateProgress(String progress);
		
	}

		
}
