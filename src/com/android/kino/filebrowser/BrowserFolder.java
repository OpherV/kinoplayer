package com.android.kino.filebrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.kino.R;

public class BrowserFolder extends Activity{
	private Intent aIntent;
	
    private List<String> directoryEntries = new ArrayList<String>();
    private File currentDirectory = Environment.getExternalStorageDirectory();
    private enum DISPLAYMODE{ ABSOLUTE, RELATIVE };
    private final DISPLAYMODE displayMode = DISPLAYMODE.ABSOLUTE;
    private ListView mListView;
    private TextView mTitle;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {    
    	super.onCreate(savedInstanceState);
    	    	    	    	    	
		setContentView(R.layout.filebrowser);
		
		mListView=(ListView) findViewById(R.id.filebrowser_list);
		mTitle = (TextView) findViewById(R.id.filebrowser_title);				
		
		Button btn_ok=(Button) findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				   Intent intent = getIntent();
				   Uri uri = Uri.parse("folder://"+currentDirectory.getPath());				   
				   intent.setData(uri);
				   setResult(RESULT_OK,intent);
				   finish();
			}
		});
		
		Button btn_cancel=(Button) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				   Intent intent = getIntent();				   
				   setResult(RESULT_CANCELED,intent);
				   finish();
				
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                String selectedFileString = l.getItemAtPosition(position).toString();  
                if (selectedFileString.equals(".")) {
                        // Refresh
                		BrowserFolder.this.browseTo(BrowserFolder.this.currentDirectory);
                } else if(selectedFileString.equals("..")){
                		BrowserFolder.this.upOneLevel();
                } else {
                	BrowserFolder.this.browseTo(new File(selectedFileString));
                }
			}
		});
		
		aIntent=getIntent();    	
    	processExternalRequest();
    }
	
    private void upOneLevel(){
        if(this.currentDirectory.getParent() != null)
            this.browseTo(this.currentDirectory.getParentFile());
    }
    
    private void fill(ArrayList<File> files) {
        this.directoryEntries.clear();
       
        // Add the "." and the ".." == 'Up one level'
        try {
            Thread.sleep(10);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        //i feel as if this is not needed
        //this.directoryEntries.add(".");
       
        if(this.currentDirectory.getParent() != null)
                this.directoryEntries.add("..");
       
        switch (this.displayMode) {
        case ABSOLUTE:
            for (File file : files){
                    this.directoryEntries.add(file.getPath());
            }
            break;
        case RELATIVE: // On relative Mode, we have to add the current-path to the beginning
            int currentPathStringLength = this.currentDirectory.getAbsolutePath().length();
            for (File file : files){
                this.directoryEntries.add(file.getAbsolutePath().substring(currentPathStringLength));
            }
            break;
        }
       
        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, this.directoryEntries);
       
        mListView.setAdapter(directoryList);
}
    
    private void browseTo(final File aDirectory) {
        if (aDirectory.isDirectory()) {
            this.currentDirectory = aDirectory;
            mTitle.setText(currentDirectory.getAbsolutePath().toString());
            fill(getSubDirs(aDirectory));
        }
    }
    
    private ArrayList<File> getSubDirs(final File dir) {    
    	ArrayList<File> subdirs = new ArrayList<File>();
    	for (File file : dir.listFiles()){
    		if (file.isDirectory()){
    			subdirs.add(file);
    		}
    	}
    	
    	return subdirs;
    }
	
	private void processExternalRequest() {
	    String theAction = null;
	    String theScheme = null;
	    if (getIntent()!=null) {
	        theAction = getIntent().getAction();
	        theScheme = getIntent().getScheme();
	    }
	    if (theAction!=null && theScheme!=null && 
	            theAction.equals(Intent.ACTION_PICK) && theScheme.equalsIgnoreCase("folder")) {
	        //pick a folder intent
	        if (aIntent.getData()!=null && aIntent.getData().getPath()!=null) {
	        	currentDirectory = new File(aIntent.getData().getPath());
	        }
	        
	        browseTo(currentDirectory);	       
	        
	    }
	}
}
