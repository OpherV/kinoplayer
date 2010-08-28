package com.android.kino.musiclibrary;

import java.io.File;
import java.io.IOException;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;
import com.android.kino.logic.MediaProperties;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.android.kino.Kino;

public class Library extends Service{
	Kino kino = null;
	LibraryDB db = null;
	
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
			File rootDir = Environment.getExternalStorageDirectory();
			File mp3dir = new File(rootDir.getAbsoluteFile()+"/mp3");
			scanDir(mp3dir, true);			
		}
		
		return null;
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
				mp3file.Album.Artist,
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

}
