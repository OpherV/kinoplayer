package com.android.kino.logic.tasks;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.MessageFormat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.android.kino.logic.TaskMasterService;

abstract public class KinoTask extends AsyncTask<Void,Void,Void>{
	
	protected int mTaskId;
	protected String mTaskTitle="Kino Task";
	protected String mTaskAction="Preparing...";
	protected int mTaskProgress=0;
	protected int mMax=0;
	protected int mDownloaded=0;
	protected byte[] mDownloadedFile;
	protected TaskMasterService mTaskMaster=null;
	public static enum TASKLENGTH {KNOWN_LENGTH,UNKNOWN_LENGTH};
	protected TASKLENGTH mTaskLength=TASKLENGTH.UNKNOWN_LENGTH;	
	
	private final String LOGTAG ="KinoTask";
	private final int BUFFERSIZE = 1024*800;// Kb
	
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
	
	
	public int getMax(){
		return mMax;
	}

	public int getDownloaded() { 
		return mDownloaded;
	}
	
	protected interface onDownloadComplete{
		void finishedDownload();
	}
	
	public void displayMessage(String text){
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("text", text);
		msg.setData(bundle);
		msg.what=TaskMasterService.MSG_SHOWMESSAGE;
		mTaskMaster.getMessageHandler().sendMessage(msg);
	}
	
	abstract protected void updateUI();
	
	protected void downloadFile(URL fileURL, String taskAction, onDownloadComplete callback){
		mDownloaded=0;
		mTaskAction=taskAction;
	
		
        try {           
            URLConnection conn = fileURL.openConnection();
            conn.connect();
            Log.d(LOGTAG,"fetching url "+fileURL);
            
            byte[] readBuffer=  new byte[BUFFERSIZE];
            
            int fileLength = conn.getContentLength();            
            if (fileLength>0){            	
            	mTaskLength=TASKLENGTH.KNOWN_LENGTH;            	
            	mMax=fileLength;
            }
            else{
            	mTaskLength=TASKLENGTH.UNKNOWN_LENGTH;            	
            }
            
            updateUI();

            // download the file
            InputStream input = new BufferedInputStream(conn.getInputStream());            
            int count;
            
            mDownloaded=0;
            
            while ( (count = input.read(readBuffer)) > 0) {
            	mDownloaded += count;
            	Log.d(LOGTAG,"downloading "+fileURL.toString()+" ("+mDownloaded+"/"+fileLength+" )");
                // publishing the progress....
                //some formats won't return file length
                if (fileLength>0){
                	publishProgress((int)mDownloaded*100/fileLength);              
                }
            }
            input.close();
            
            Log.e(LOGTAG,"downloaded "+mDownloaded+" filength "+fileLength);
            //copy the final file to the buffer 
            mDownloadedFile=new byte[mDownloaded];
            System.arraycopy(readBuffer,0,mDownloadedFile,0,mDownloaded);
                       
            input.close();
            
            if (callback!=null){
            	callback.finishedDownload();	
            }
            
        } catch (Exception e) {
        	
        	if (e instanceof UnknownHostException){        		
        		displayMessage("Oops! Unknown host! Maybe you're not connected to the internet?");
        		cancel(true);
        		Log.e("Kino task "+mTaskId, "Host not found... maybe networking is not working?");
        	}
        	else{
        		e.printStackTrace();
        	}
        	
        }
	}
	
	protected void downloadFile(URL fileURL, OutputStream output, String taskAction, onDownloadComplete callback){
		mDownloaded=0;
		mTaskAction=taskAction;
	
		
        try {           
            URLConnection conn = fileURL.openConnection();
            conn.connect();
            Log.d(LOGTAG,"fetching url "+fileURL);
            
            byte[] readBuffer=  new byte[BUFFERSIZE];
            
            int fileLength = conn.getContentLength();            
            if (fileLength>0){            	
            	mTaskLength=TASKLENGTH.KNOWN_LENGTH;            	
            	mMax=fileLength;
            }
            else{
            	mTaskLength=TASKLENGTH.UNKNOWN_LENGTH;            	
            }
            
            updateUI();

            // download the file
            InputStream input = new BufferedInputStream(conn.getInputStream());            
            int count;
            
            mDownloaded=0;
            byte data[] = new byte[1024];
            
            while ( (count = input.read(data)) != -1) {
            	mDownloaded += count;
            	Log.d(LOGTAG,"downloading "+fileURL.toString()+" ("+mDownloaded+"/"+fileLength+" )");
                // publishing the progress....
                //some formats won't return file length
                if (fileLength>0){
                	publishProgress((int)mDownloaded*100/fileLength);              
                }
                
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
            
            Log.e(LOGTAG,"downloaded "+mDownloaded+" filength "+fileLength);
            //copy the final file to the buffer 
            mDownloadedFile=new byte[mDownloaded];
            System.arraycopy(readBuffer,0,mDownloadedFile,0,mDownloaded);
                       
            input.close();
            
            if (callback!=null){
            	callback.finishedDownload();	
            }
            
        } catch (Exception e) {
        	
        	if (e instanceof UnknownHostException){        		
        		displayMessage("Oops! Unknown host! Maybe you're not connected to the internet?");
        		cancel(true);
        		Log.e("Kino task "+mTaskId, "Host not found... maybe networking is not working?");
        	}
        	else{
        		e.printStackTrace();
        	}
        	
        }
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
