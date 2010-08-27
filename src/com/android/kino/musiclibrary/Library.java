package com.android.kino.musiclibrary;

import java.io.File;
import java.io.IOException;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.android.kino.Kino;

public class Library extends Service{
	Kino kino = null;
	
	@Override
	public IBinder onBind(Intent intent) {						
		
		Log.d("Library","Please 2");
		
		//make sure sd card is mounted 
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Log.e("Library","SD card not mounted! media state: "+Environment.getExternalStorageState());
		}		
		else {			
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
		MusicMetadataSet src_set;
		IMusicMetadata metadata;
		try {
			//read metadata
			src_set = new MyID3().read(file);
			metadata = src_set.getSimplified();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}			
		
		Log.d("Library","adding "+metadata.getArtist()+" - "+metadata.getSongTitle());
		
		return true;
		
	}

}
