package com.android.kino.logic.tasks;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.os.Environment;
import android.os.Message;
import android.util.Log;

import com.android.kino.Kino;
import com.android.kino.logic.TaskMasterService;
import com.android.kino.musiclibrary.LastFmHandlerAlbum;
import com.android.kino.musiclibrary.LastFmHandlerAlbum.lastfmAlbumDetails;
import com.android.kino.utils.ConvertUtils;

public class FetchAlbumDetails extends KinoTask{		
	private String mArtistTitle;
	private String mAlbumTitle;	
	private URL downloadURL=null;
	private byte[] mDownloadedFile;
	
	private final int BUFFERSIZE = 1024*300;// Kb
	
	//TODO if at some point in the distant future we'll want differnet handlers (for musicbrains or something)
	// than we need to create a class QueryHandler that LastFmhandler implements
	private LastFmHandlerAlbum queryHandler = new LastFmHandlerAlbum();
	
	public FetchAlbumDetails(String artistTitle,String albumTitle){		
		mArtistTitle=artistTitle;
		mAlbumTitle=albumTitle;
		mTaskTitle="Fetching \"" +albumTitle + "\" from last.fm...";
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		//getXML();		
		
		try {
			downloadURL = new URL(queryHandler.getAlbumQueryURL(mArtistTitle, mAlbumTitle));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		URL xmlURL = buildURL(queryHandler.getAlbumQueryURL(mArtistTitle, mAlbumTitle));
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
		
		lastfmAlbumDetails albumDetails=queryHandler.getAlbumDetails();
		String imagePath=albumDetails.images.get("extralarge");
		URL imageURL=buildURL(imagePath);		
		downloadFile(imageURL, "downloading album image...", new onDownloadComplete() {
			
			@Override
			public void finishedDownload() {
				String albumImagePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Kino.ALBUM_DIR;
				String albumFileName=ConvertUtils.safeFileName(mArtistTitle)+"-"+ConvertUtils.safeFileName(mAlbumTitle)+".jpg";
				File albumImageFile = new File(albumImagePath,albumFileName);
				
				try {
					FileOutputStream fos = new FileOutputStream(albumImageFile);
					fos.write(mDownloadedFile);
				} catch (IOException  e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						   
				
				updateUI();
				Log.d("blah", "succesfully wrote "+albumImageFile.getAbsolutePath().toString());
			}
		});
		
		
	
		return null;
	}		
	
	private URL buildURL(String urlString){
		URL newURL=null;
		try {
			newURL = new URL(urlString);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return newURL;
	}
	
	private void downloadFile(URL fileURL, String taskAction, onDownloadComplete callback){
		mDownloaded=0;
		mTaskAction=taskAction;
		

		
        try {           
            URLConnection conn = fileURL.openConnection();
            conn.connect();

            int fileLength = conn.getContentLength();            
            if (fileLength>0){
            	mDownloadedFile=new byte[fileLength];
            	mTaskLength=TASKLENGTH.KNOWN_LENGTH;            	
            	mMax=fileLength;
            }
            else{
            	mTaskLength=TASKLENGTH.UNKNOWN_LENGTH;
            	mDownloadedFile=new byte[BUFFERSIZE];
            }
            
            updateUI();

            // download the file
            InputStream input = new BufferedInputStream(conn.getInputStream());            
            int count;
                       
            while ((count = input.read(mDownloadedFile)) != -1) {
            	mDownloaded += count;

                // publishing the progress....
                //some formats won't return file length
                if (fileLength>0){
                	publishProgress((int)count*100/fileLength);              
                }
            }
                       
            input.close();
            
            if (callback!=null){
            	callback.finishedDownload();	
            }
            
        } catch (Exception e) {
        	
        	if (e instanceof UnknownHostException){
        		Log.e("Kino task "+mTaskId, "Host not found... maybe networking is not working?");
        	}
        	else{
        		e.printStackTrace();
        	}
        	
        }
	}
	
	


	private void publishProgress(int progress) {
		Log.d("progress",downloadURL+" "+progress);
		
		mTaskProgress=progress;			
		updateUI();
	}	
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		mTaskMaster.taskDone(this);		
	}
	
	private void updateUI(){
		Message msg = new Message();
		msg.what=TaskMasterService.MSG_UPDATEVIEW;
		mTaskMaster.getMessageHandler().sendMessage(msg);	
	}
	
	public boolean equals(FetchAlbumDetails another){
		return (mAlbumTitle.equals(another.mAlbumTitle) &&
				mArtistTitle.equals(another.mArtistTitle));
	}

}
