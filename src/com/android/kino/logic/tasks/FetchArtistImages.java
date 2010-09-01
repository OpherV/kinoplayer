package com.android.kino.logic.tasks;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;

import com.android.kino.Kino;
import com.android.kino.logic.ArtistProperties;
import com.android.kino.logic.TaskMasterService;
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
	
	@Override
	protected Void doInBackground(Void... params) {
		//getXML();		
							
		URL xmlURL = buildURL(queryHandler.getArtistQueryURL(mArtistTitle,1));
		downloadFile(xmlURL, "fetching XML",null);
		
		//parse XML
      SAXParserFactory spf = SAXParserFactory.newInstance();
      SAXParser sp=null;
	
    try {
		sp = spf.newSAXParser();
	} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
      XMLReader xr=null;
	try {
		xr = sp.getXMLReader();
	} catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}           
      xr.setContentHandler(queryHandler);
      
      ByteArrayInputStream xmlStream = new ByteArrayInputStream(mDownloadedFile);      
      InputSource input=new InputSource(xmlStream);
      try {
		xr.parse(input);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
		LinkedList<lastFMArtistImage> albumDetails=queryHandler.getArtistImages();
		
		String imagePath=albumDetails.get(0).imageURLS.get("extralarge");
		
		if (imagePath==null){
			mArtist.disableImage();
			displayMessage("No image on LastFM for "+mArtistTitle);
			
			cancel(true);
		}
		else{
			
			URL imageURL=buildURL(imagePath);			
			String postfix=imagePath.substring(imagePath.length()-4,imagePath.length());
			
			final String artistImagePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Kino.ARTIST_DIR;
			final String artistFileName=ConvertUtils.safeFileName(mArtistTitle)+postfix;
			File artistImageFile = new File(artistImagePath,artistFileName);
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(artistImageFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//check for available sotrage size
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath()); 
            long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getBlockCount(); 
			
            if (mDownloaded> bytesAvailable){
        		displayMessage("Oops! not enough free space on SD card!");
       		cancel(true);
       		Log.e("Kino task "+mTaskId, "not enough free space on SD card. needed "+mDownloaded+" available "+bytesAvailable);
			}
			
			downloadFile(imageURL, fos, "downloading artist image...", new onDownloadComplete() {
				
				@Override
				public void finishedDownload() {
													 				
					Log.d(LOGTAG, "succesfully wrote "+artistImagePath+"/"+artistFileName);
					

					Bitmap albumImage = BitmapFactory.decodeFile(artistImagePath+"/"+artistFileName);
					if (albumImage!=null){
						mArtist.setImage(albumImage);
						mArtist.stopSearching();
						updateUI();
					}
					else{
						Log.e("Kino task "+mTaskId, "couldn't decode downloaded image!");		
					}

					
				}
			});
		
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
	
	protected void updateUI(){
		Message msg = new Message();
		msg.what=TaskMasterService.MSG_UPDATEVIEW;
		mTaskMaster.getMessageHandler().sendMessage(msg);	
	}
	
	public boolean equals(FetchArtistImages another){
		return (mArtistTitle.equals(another.mArtistTitle));				
	}

}
