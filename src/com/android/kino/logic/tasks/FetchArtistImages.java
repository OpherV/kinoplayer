package com.android.kino.logic.tasks;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.android.kino.Kino;
import com.android.kino.logic.ArtistProperties;
import com.android.kino.musiclibrary.LastFmHandlerArtistImages;
import com.android.kino.musiclibrary.LastFmHandlerArtistImages.lastFMArtistImage;
import com.android.kino.utils.ConvertUtils;

public class FetchArtistImages extends KinoTask{		
	private String mArtistTitle;			
	private ArtistProperties mArtist;
		
	private final String LOGTAG="FetchAlbumDetails";
	private LastFmHandlerArtistImages queryHandler = new LastFmHandlerArtistImages();
	
	public FetchArtistImages(ArtistProperties artist){	
		mArtist=artist;
		mArtistTitle=artist.getName();		
		mTaskTitle="Fetching \"" +mArtistTitle + "\" from last.fm...";
	}
	
	private String getTempXMLFilename(){
		String xmlFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/" + Kino.KINODIR+"/"+ConvertUtils.safeFileName(mArtistTitle)+".xml";
		return xmlFileName;
	}		
	
	@Override
	protected Void doInBackground(Void... params) {
		//getXML();		
							
		URL xmlURL = buildURL(queryHandler.getArtistQueryURL(mArtistTitle,1));
		
	
		
		File xmlFile = downloadFile(xmlURL, getTempXMLFilename(), "fetching XML");
		
		if(xmlFile==null){
			Log.e(LOGTAG, "problem downloading file! "+xmlURL);						
			return null;
		}
		
	
		
		if (!parseAndDeleteXMLFile(queryHandler,xmlFile)){
			Log.e(LOGTAG, "problem parsing xml! "+xmlFile.getAbsolutePath());
			return null;
		}	         
		
		LinkedList<lastFMArtistImage> albumDetails=queryHandler.getArtistImages();
				
		String imagePath=null;
		if (albumDetails.size()>0){
			imagePath=albumDetails.get(0).imageURLS.get("original");
		};
		
		if (imagePath==null){
			mArtist.disableImage();
			displayMessage("No image on LastFM for "+mArtistTitle);
			
			return null;
		}
		else{
			
			URL imageURL=buildURL(imagePath);			
			String postfix=imagePath.substring(imagePath.length()-4,imagePath.length());
			
			final String artistImagePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Kino.ARTIST_DIR;
			final String artistFileName=ConvertUtils.safeFileName(mArtistTitle)+postfix;
							
			File artistImageFile= downloadFile(imageURL, artistImagePath+"/"+artistFileName, "downloading artist image...");				
		 				
			if (artistImageFile==null){
				Log.e(LOGTAG, "couldn't get artist image! "+imageURL);
				return null;
			}					

			Bitmap artistImage = BitmapFactory.decodeFile(artistImageFile.getAbsolutePath());
			if (artistImage!=null){
				mArtist.setImage(artistImage);
				mArtist.stopSearching();
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
		
	public boolean equals(FetchArtistImages another){
		return (mArtistTitle.equals(another.mArtistTitle));				
	}

}
