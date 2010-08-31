package com.android.kino.logic;

import java.util.LinkedList;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.android.kino.Kino;
import com.android.kino.logic.tasks.FetchAlbumDetails;
import com.android.kino.logic.tasks.KinoTask;
import com.android.kino.ui.KinoUI;
import com.android.kino.ui.StatusUpdater;

public class TaskMasterService extends Service{
	
	public static final int MSG_UPDATEVIEW = 413; 
	
	Kino kino = null;	
	IBinder taskmasterBinder=new TaskMasterBinder();
	
	private final int MAXTASKS=1;
	
	private int taskCounter=1;
	
	private LinkedList<KinoTask> mRunningTasks= new LinkedList<KinoTask>();
	private LinkedList<KinoTask> mWaitingTasks= new LinkedList<KinoTask>();	
	private Handler messageHandler = new Handler(){
		public void handleMessage(Message msg) {
			
			switch(msg.what){
				case MSG_UPDATEVIEW:
				mDisplay.updateUI();
					
			}
		}
	};
	
	private KinoUI mDisplay=null;
	

	
	public LinkedList<KinoTask> getRunningTasks(){
		return mRunningTasks;
	}
	
	public void addTask(KinoTask task){
		task.setTaskId(taskCounter);
		task.setTaskMaster(this);
		
		if (!isDuplicateTask(task))
		{
			if (mRunningTasks.size()<MAXTASKS){
				mRunningTasks.add(task);
				task.execute((Void) null);
				Log.d("blah","executing task "+task.getTaskId());
				mDisplay.updateUI();
			}
			else{
				mWaitingTasks.add(task);
			}
			taskCounter++;
		}
				
	}
	
	private boolean isDuplicateTask(KinoTask task) {
		for (KinoTask anotherTask:mRunningTasks){			
			if (task instanceof FetchAlbumDetails && anotherTask instanceof FetchAlbumDetails ){
				if ( ((FetchAlbumDetails) task).equals( (FetchAlbumDetails) anotherTask ) ){
					return true;
				}
			}
		}
		
		for (KinoTask anotherTask:mWaitingTasks){			
			if (task instanceof FetchAlbumDetails && anotherTask instanceof FetchAlbumDetails ){
				if ( ((FetchAlbumDetails) task).equals( (FetchAlbumDetails) anotherTask ) ){
					return true;
				}
			}
		}
		
		return false;
	}

	public void taskDone(KinoTask task){
		mRunningTasks.remove(task);		
		mDisplay.updateUI();
		
		//run new tasks if waiting
		if (mWaitingTasks.size()>0){
			addTask(mWaitingTasks.poll());
		}
	}
	
	
	
	
	
	@Override
	public IBinder onBind(Intent intent) {						
										
		return taskmasterBinder;
	}
	
	public Handler getMessageHandler() {
		return messageHandler;
	}

	public class TaskMasterBinder extends Binder{
		public TaskMasterService getTaskMaster(){
			return TaskMasterService.this;
		}
	}
	

	public void setDisplay(KinoUI kinoUI) {
		mDisplay=kinoUI;
		
	}

}
