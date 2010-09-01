package com.android.kino.logic.tasks;

import android.os.AsyncTask;

import com.android.kino.logic.TaskMasterService;

abstract public class KinoTask extends AsyncTask<Void,Void,Void>{
	
	protected int mTaskId;
	protected String mTaskTitle="Kino Task";
	protected String mTaskAction="Kino Action";
	protected int mTaskProgress=0;
	protected int mMax=0;
	protected int mDownloaded=0;
	protected TaskMasterService mTaskMaster=null;
	public static enum TASKLENGTH {KNOWN_LENGTH,UNKNOWN_LENGTH};
	protected TASKLENGTH mTaskLength=TASKLENGTH.UNKNOWN_LENGTH;
	
	
	
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

	
}
