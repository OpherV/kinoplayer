package com.android.kino.ui;

import android.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MenuMain extends ListActivity {

	static final String[] MENU_ITMES = new String[] { "All Songs", "Music Library",
			"Settings" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter(new ArrayAdapter<String>(this,
				R.layout.simple_list_item_1, MENU_ITMES));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				//this is ugly, but for the moment...
				CharSequence menuText=((TextView) view).getText();
				if  (menuText=="All Songs"){			
						Intent playlistIntent=new Intent(parent.getContext(),MenuPlaylist.class);
						//TODO fetch the allsongs playlist from the library and pass it
						//playlistIntent.putExtra("playlist",
			    		startActivity(playlistIntent);			    		
			    }
				
			}

		});
	}

}
