package com.android.kino.logic.tasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;

import com.android.kino.logic.TaskMasterService;

abstract public class KinoTask extends AsyncTask<Void,Void,Void>{
	
	protected int mTaskId;
	protected String mTaskTitle="Kino Task";
	protected String mTaskAction="Preparing...";
	protected int mTaskProgress=0;	
	protected TaskMasterService mTaskMaster=null;
	public static enum TASKLENGTH {KNOWN_LENGTH,UNKNOWN_LENGTH};
	protected TASKLENGTH mTaskLength=TASKLENGTH.UNKNOWN_LENGTH;	
	
	private final String LOGTAG ="KinoTask";
	
	public void setTaskMaster(TaskMasterService taskMaster){
		mTaskMaster=taskMaster;
	}
	
	public TASKLENGTH getTaskLength(){
		return mTaskLength;
	}
	
	public int getTaskId(){
		return mTaskId;
	}
	
	public void setTaskId(int taskId){
		mTaskId=taskId;
	}
	
	public String getTaskTitle(){
		return mTaskTitle;
	}
	
	public String getTaskAction(){
		return mTaskAction;
	}
	
	public int getTaskProgress(){
		return mTaskProgress;
	}	
	
	public void displayMessage(String text){
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("text", text);
		msg.setData(bundle);
		msg.what=TaskMasterService.MSG_SHOWMESSAGE;
		mTaskMaster.getMessageHandler().sendMessage(msg);
	}
	
	protected void updateUI(){
		Message msg = new Message();
		msg.what=TaskMasterService.MSG_UPDATEVIEW;
		mTaskMaster.getMessageHandler().sendMessage(msg);	
	}	
	
	protected File downloadFile(URL fileURL, String localFileName, String taskAction){
		int downloadedBytes=0;
		mTaskAction=taskAction;
		
		File file= new File(localFileName);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}		
		
		Log.d(LOGTAG,"trying to fetch url "+fileURL);
	    URLConnection conn =null;
        try {           
            conn = fileURL.openConnection();
            conn.connect();
        }
        catch (Exception e) {
        	Log.e(LOGTAG,"could not open connection "+fileURL);
        	try {
				fos.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		}
        
            int fileLength = conn.getContentLength();            
            if (fileLength>0){            	
            	mTaskLength=TASKLENGTH.KNOWN_LENGTH;            	
            	
            	
    			//check for available storage size
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath()); 
                long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getBlockCount(); 
    			
                if (fileLength> bytesAvailable){
            		displayMessage("Oops! not enough free space on SD card!");            		
            		Log.e("Kino task "+mTaskId, "not enough free space on SD card. needed "+fileLength+" available "+bytesAvailable);
                	try {
        				fos.close();
        			} catch (IOException e1) {
        				// TODO Auto-generated catch block
        				e1.printStackTrace();
        			}
        			return null;
    			}
            }
            else{
            	mTaskLength=TASKLENGTH.UNKNOWN_LENGTH;            	
            }
            
            updateUI();
                        
            
        InputStream input=null;
            
        try{
            // download the file
            input = new BufferedInputStream(conn.getInputStream() ,8*1024);            
            int count;
                        
            byte data[] = new byte[1024];            
            while ( (count = input.read(data)) != -1) {
            	downloadedBytes += count;
            	Log.d(LOGTAG,"downloading "+fileURL.toString()+" ("+downloadedBytes+"/"+fileLength+" )");
                // publishing the progress....
                //some formats won't return file length
                if (fileLength>0){
                	int progress=downloadedBytes*100/fileLength;
                	mTaskProgress=progress;
                	publishProgress(progress);              
                }
                
                fos.write(data, 0, count);
            }
            fos.flush();
                                 
        } catch (Exception e) {
        	
        	if (e instanceof UnknownHostException){        		
        		displayMessage("Oops! Unknown host! Maybe you're not connected to the internet?");        		
        		Log.e("Kino task "+mTaskId, "Host not found... maybe networking is not working?");        		
        	}        	
        	
        	e.printStackTrace();
        	        	
        	//don't leave file behind
        	if (file.exists()){
        		file.delete();
        	}
        	
        	return null;
        	
        }
        finally{            
        	
        	try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if (input!=null){
            	try {
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }                        
        }
        
        if (downloadedBytes==0){
        	Log.e(LOGTAG, "downloaded empty file! "+file.getAbsolutePath());
        	file.delete();
        	return null;
        }
        
        Log.d(LOGTAG, "succesfully wrote "+file.getAbsolutePath()+" size: "+downloadedBytes);        
        return file;
	}
	
	protected boolean parseAndDeleteXMLFile(DefaultHandler queryHandler, File xmlFile){
		FileInputStream xmlfis=null;
	    try {
			//parse XML
	        SAXParserFactory spf = SAXParserFactory.newInstance();
	        SAXParser sp=null;	        
			sp = spf.newSAXParser();	
	        XMLReader xr=null;	
			xr = sp.getXMLReader();
		    xr.setContentHandler(queryHandler);   	    
		    xmlfis = new FileInputStream(xmlFile);	    
	        InputSource input=new InputSource(xmlfis);      
			xr.parse(input);			
			
		} catch (Exception e) {	
			e.printStackTrace();
			return false;
		}
		finally{
			if (xmlfis!=null){
				try {
					xmlfis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//delete temp file
			xmlFile.delete();
		
		}
		
		return true;
	}
	
	abstract protected void publishProgress(int progress);
	

	public URL buildURL(String urlString){
		URL newURL=null;
		try {
			newURL = new URL(urlString);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return newURL;
	}
	


	
}
