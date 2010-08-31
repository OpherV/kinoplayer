package com.android.kino.ui;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.android.kino.logic.MediaProperties;
import com.android.kino.musiclibrary.ImageScraper;
import com.android.kino.utils.ConvertUtils;

public class MediaImageContainer {
	
	final String ALBUM_DIR="kino/images/albums";
	final String ARTIST_DIR="kino/images/artists";
	
	private Bitmap albumImage=null;
	private Bitmap artistImage=null;
	private ImageScraper scraper = new ImageScraper();
	
	public MediaImageContainer(MediaProperties song){
		//TODO make sure that the SDcard is properly mounted
		
		String artistImagePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ARTIST_DIR;
			
		File artistImageFile = new File(artistImagePath,ConvertUtils.safeFileName(song.Artist)+".jpg");
		if (artistImageFile.exists()){
			artistImage = BitmapFactory.decodeFile(artistImageFile.getAbsolutePath());
		}
		else{
			Log.e(getClass().getName(),"no artist image file: "+artistImagePath);
		//	artistImage= scraper.getArtistImage(song.Artist);					
		}
	}
	
	public Bitmap getArtistImage(){
		return artistImage;
	}
	
}
