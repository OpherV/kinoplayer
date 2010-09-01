package com.android.kino.ui;
import java.util.LinkedList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.kino.R;
import com.android.kino.logic.tasks.KinoTask;
import com.android.kino.ui.listAdapters.StatusAdapter;

public class StatusUpdater extends LinearLayout{
	private Context mContext;	
	private StatusAdapter statusUpdateAdapter;
	private LinkedList<KinoTask> statusUpdates;
	ListView statusView ;
	
	 public StatusUpdater(Context context, AttributeSet attrs){	 
	    super(context, attrs);
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null){       
            inflater.inflate(R.layout.statusupdater, this);
        }
        
        mContext=context;              
                
	}	 	
	 
	public void addStatusUpdate(KinoTask status){
		statusUpdates.add(0, status);				
	}
	
	public void updateData(){
		statusUpdateAdapter.notifyDataSetChanged();
	}
	
	public void initStatusUpdater(){
		statusUpdates= ((KinoUI) mContext).getTaskMaster().getRunningTasks();
        statusUpdateAdapter=new StatusAdapter(mContext, 0, statusUpdates);
		
		statusView = (ListView)findViewById(R.id.statusupdater_list);
		statusView.setAdapter(statusUpdateAdapter);
	}
	

}

