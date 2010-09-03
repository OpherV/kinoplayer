package com.android.kino.ui;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.kino.Kino;
import com.android.kino.R;
import com.android.kino.logic.TaskMasterService;
import com.android.kino.logic.tasks.UpdateLibrary;
import com.android.kino.musiclibrary.Library;

public class LibraryActionsMenu extends ListActivity implements OnItemClickListener {
    
    private static final String TAG = "LibraryActionsMenu";
    
    private static final int UPDATE = 0;
    private static final int PURGE = 1;
    private static final int CLEAR_IMAGES = 2;
    
    private Library mLibrary;
    private TaskMasterService mTaskMaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Kino kino = Kino.getKino(this);
        mLibrary = kino.getLibrary();
        mTaskMaster = kino.getTaskMaster();
        
        setListAdapter(ArrayAdapter.createFromResource(this,
                R.array.libraryActions,
                android.R.layout.simple_list_item_1));
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(this);
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       switch (position) {
       case UPDATE: {
           Log.d(TAG, "Update clicked");
           mTaskMaster.addTask(new UpdateLibrary(mLibrary));
           break;
       }
       case PURGE: {
           Log.d(TAG, "Purge clicked");
           new AlertDialog.Builder(this)
           .setMessage("Clear library?")
           .setPositiveButton("Delete", new ClearLibrary())
           .show();
           break;
       }
       case CLEAR_IMAGES: {
           Log.d(TAG, "Clear clicked");
           new AlertDialog.Builder(this)
           .setMessage("Delete all images?")
           .setPositiveButton("Delete", new ClearImages())
           .show();
           break;
       }
       }
    }
    
    private class ClearImages implements OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // TODO (implement) Delete images
        }
    }
    
    private class ClearLibrary implements OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // TODO (implement) Purge library
        }
    }
    
}
