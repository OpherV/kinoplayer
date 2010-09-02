package com.android.kino.logic.tasks;

import java.io.File;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.android.kino.Kino;
import com.android.kino.logic.AlbumProperties;
import com.android.kino.musiclibrary.LastFmHandlerAlbum;
import com.android.kino.musiclibrary.LastFmHandlerAlbum.lastfmAlbumDetails;
import com.android.kino.utils.ConvertUtils;

public class FetchAlbumDetails extends KinoTask{		
	private String mArtistTitle;
	private String mAlbumTitle;			
	private AlbumProperties mAlbum;
		
	private final String LOGTAG="FetchAlbumDetails";
	private LastFmHandlerAlbum queryHandler = new LastFmHandlerAlbum();
	
	public FetchAlbumDetails(AlbumProperties album){	
		mAlbum=album;
		mArtistTitle=album.getArtistName();
		mAlbumTitle=album.getAlbumName();
		mTaskTitle="Fetching \"" +mAlbumTitle + "\" from last.fm...";
	}
	
	private String getTempXMLFilename(){
		String xmlFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/" + Kino.KINODIR+"/"+ConvertUtils.safeFileName(mArtistTitle)+"-"+ConvertUtils.safeFileName(mAlbumTitle)+".xml";
		return xmlFileName;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		//getXML();		
							
		URL xmlURL = buildURL(queryHandler.getAlbumQueryURL(mArtistTitle, mAlbumTitle));

		File xmlFile = downloadFile(xmlURL, getTempXMLFilename(), "fetching XML");
		
		if(xmlFile==null){
			Log.e(LOGTAG, "problem downloading file! "+xmlURL);						
			return null;
		}
		
	
		
		if (!parseAndDeleteXMLFile(queryHandler,xmlFile)){
			Log.e(LOGTAG, "problem parsing xml! "+xmlFile.getAbsolutePath());
			return null;
		}	         
		
		lastfmAlbumDetails albumDetails=queryHandler.getAlbumDetails();
		String imagePath=albumDetails.images.get("large");
		
		if (imagePath==null){
			mAlbum.disableImage();
			displayMessage("No image on LastFM for "+mArtistTitle+"-"+mAlbumTitle);
			
			return null;
		}
		else{
			
			URL imageURL=buildURL(imagePath);			
			String postfix=imagePath.substring(imagePath.length()-4,imagePath.length());
			
			String albumImagePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Kino.ALBUM_DIR;
			String albumFileName=ConvertUtils.safeFileName(mArtistTitle)+"-"+ConvertUtils.safeFileName(mAlbumTitle)+postfix;
			File albumImageFile= downloadFile(imageURL, albumImagePath+"/"+albumFileName, "downloading album image...");				
		 				
			if (albumImageFile==null){
				Log.e(LOGTAG, "couldn't get album image! "+imageURL);
				return null;
			}					

			Bitmap albumImage = BitmapFactory.decodeFile(albumImageFile.getAbsolutePath());
			if (albumImage!=null){
				mAlbum.setImage(albumImage);
				mAlbum.stopSearching();
				updateUI();
			}
			else{
				Log.e("Kino task "+mTaskId, "couldn't decode downloaded image!");		
			}

		}
	
		return null;
	}
			
	protected void publishProgress(int progress) {
		//Log.d("progress",downloadURL+" "+progress);
		
		mTaskProgress=progress;			
		updateUI();
	}	
		
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		mTaskMaster.taskDone(this);		
	}
	
	public boolean equals(FetchAlbumDetails another){
		return (mAlbumTitle.equals(another.mAlbumTitle) &&
				mArtistTitle.equals(another.mArtistTitle));
	}

}
