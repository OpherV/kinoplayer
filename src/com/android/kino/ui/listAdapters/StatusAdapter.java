package com.android.kino.ui.listAdapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.kino.R;
import com.android.kino.logic.tasks.KinoTask;

public class StatusAdapter extends ArrayAdapter<KinoTask>{

	Context mContext=null;
	List<KinoTask> mStatusUpdates=null;
	
	public StatusAdapter(Context context, int textViewResourceId,
			List<KinoTask> objects) {
		super(context, textViewResourceId, (List<KinoTask>) objects);
		mContext=context;
		mStatusUpdates=objects;
	}	
	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		KinoTask task=mStatusUpdates.get(position);		 
        
		View v=null;  
  		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);              
        v = inflater.inflate(R.layout.item_status, null);
        
        TextView titleView = (TextView) v.findViewById(R.id.statusitem_taskTitle);
		titleView.setText(task.getTaskTitle());
		
		TextView actionView = (TextView) v.findViewById(R.id.statusitem_taskAction);		

		
		ProgressBar progressViewKnownLength = (ProgressBar) v.findViewById(R.id.statusitem_progressKnownLength);
		ProgressBar progressViewunKnownLength = (ProgressBar) v.findViewById(R.id.statusitem_progressUnknownLength);
		
		if (task.getTaskLength()==KinoTask.TASKLENGTH.KNOWN_LENGTH){									
			progressViewKnownLength.setVisibility(View.VISIBLE);
			progressViewunKnownLength.setVisibility(View.GONE);
			
			
			actionView.setText(task.getTaskAction()+" ("+task.getTaskProgress()+"%)");
			progressViewKnownLength.setMax(task.getMax());
			progressViewKnownLength.setProgress(task.getDownloaded());
			
		}
		else{
			progressViewKnownLength.setVisibility(View.GONE);			
			progressViewunKnownLength.setVisibility(View.VISIBLE);
			actionView.setText(task.getTaskAction());
		}
				
		
		return v;
        
	}

}
