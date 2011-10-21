package com.android.kino.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.kino.Kino;
import com.android.kino.R;
import com.android.kino.logic.KinoMediaPlayer;
import com.android.kino.logic.KinoUser;
import com.android.kino.logic.MediaProperties;
import com.android.kino.logic.TaskMasterService;
import com.android.kino.logic.settings.SettingsContainer;
import com.android.kino.logic.settings.SettingsLoader;
import com.android.kino.logic.settings.SettingsContainer.Setting;
import com.android.kino.logic.tasks.UpdateLibrary;
import com.android.kino.musiclibrary.Library;
import com.android.kino.utils.ConvertUtils;

public class KinoUI extends Activity implements KinoUser {
    protected Kino kino = null;
    protected KinoMediaPlayer mPlayer;
    
    protected MediaProperties song=null;
    protected TextView timeElapsed=null;
    protected ProgressBar songProgress=null;
    protected PlayerMini playermini;
    public Library library;  
    protected TaskMasterService mTaskMaster;
    protected StatusUpdater updaterView;

    protected Handler guiUpdater;    
    final int GUI_UPDATE_INTERVAL = 1000;
    public static enum VIEWMODE {visible,invisible};
    
    private Animation aFadeinpartial;
    private Animation aFadeoutpartial;
    private Animation aFadeinfull;
    
    private boolean miniplayerSwipe=false;
    
    private SharedPreferences mPrefs;
        
    protected static final int SWIPE_MIN_DISTANCE = 120;
    protected static final int SWIPE_MAX_OFF_PATH = 250;
    protected static final int SWIPE_THRESHOLD_VELOCITY = 1500;
    
    private Dialog dialog;
    
    // updates the gui every second
    protected Runnable guiUpdateTask = new Runnable() {
          public void run() {
              final long start = GUI_UPDATE_INTERVAL;
              long millis = SystemClock.uptimeMillis() - start;
              int seconds = (int) (millis / 1000);
              int minutes = seconds / 60;
              seconds     = seconds % 60;
                                                       
              //if song was changed, reload all details
              if (song == mPlayer.getCurrentMedia()){
                  updateSongDetails();
              }
              else{
                  initSongDetails();
              }
            
             guiUpdater.postAtTime(this,
                      start + (((minutes * 60) + seconds + 1) * 1000));
          }
       };
       
    public void onKinoInit(Kino kino) {
        mPlayer = kino.getPlayer();        
        library = kino.getLibrary();
        mTaskMaster = kino.getTaskMaster();
        
        // TODO: The shutdown doesn't kill the application so when starting the
        // application after exiting it, the interception doesn't restart, so
        // start it here
        SettingsContainer settings = SettingsLoader.loadCurrentSettings(this);
//        if (settings.getConfiguredBoolean(Setting.ENABLE_DOUBLE_TAP)) {
//            kino.getInputTranslator().enableDoubleTap();
//        }
        
        initSongDetails();                
        
        //start the gui update timer
        guiUpdater = new Handler();
        guiUpdater.postAtTime(guiUpdateTask, GUI_UPDATE_INTERVAL);
        
        // set the status updater object for the taskmaster

        updaterView = (StatusUpdater) findViewById(R.id.statusupdater);
        updaterView.initStatusUpdater();        
        mTaskMaster.setDisplay(this);        
                
    }      
    
    public void updateUI(){
        updaterView.updateData();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {    
        super.onCreate(savedInstanceState);        
        aFadeinpartial = AnimationUtils.loadAnimation(this, R.anim.fadein_partial);
        aFadeoutpartial = AnimationUtils.loadAnimation(this, R.anim.fadeout_partial);
        aFadeinfull = AnimationUtils.loadAnimation(this, R.anim.fadein_full);
        
        kino = Kino.getKino(this);
        
        initUI();
    
        // miniplayer Gesture detection
        GestureActions bgActions = new GestureActions(){
            @Override
            public void swipeLeft() {
                mPlayer.previous();
                initSongDetails();                
            }
            
            @Override
            public void swipeRight() {                
                    mPlayer.next();
                    initSongDetails();                    
            }
        };
        
        final GestureDetector gestureDetector = new GestureDetector(new GenericGestureDetector(bgActions));       
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
             public boolean onTouch(View v, MotionEvent event) {                 
                 if (gestureDetector.onTouchEvent(event)) {
                     miniplayerSwipe = true;
                     return true;
                 }                 
                 miniplayerSwipe = false;
                 return false;
             }          
         };
         
         playermini = (PlayerMini) this.findViewById(R.id.player_mini);
         if (playermini!=null){
             playermini.setOnTouchListener(gestureListener);
             playermini.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(KinoUI.this,PlayerMain.class));                        
                }
            });
             
             playermini.setOnLongClickListener(new OnLongClickListener() {
                
                @Override
                public boolean onLongClick(View v) {
                    if (!miniplayerSwipe){
                        mPlayer.togglePlayPause();
                        return true;
                    }
                    return false;
                }
            });
         }
         
        kino.registerUser(this);
    }

    @Override
    protected void onResume() {
        super.onResume();        
        if (playermini!=null){
            if (song!=null){
               playermini.setVisibility(View.VISIBLE);
            }
               else{
                   playermini.setVisibility(View.GONE);
            }
        }        
        
        if (mTaskMaster!=null){
            mTaskMaster.setDisplay(this);
        }
        
    }

    @Override
    protected void onPause() {
        Log.d("KinoUI", "onPause");
        super.onPause();
        if (!isFinishing()) {
            kino.showNotification();
        }
    }
    
    @Override
    protected void onDestroy() {
        Log.d("KinoUI", "onDestroy");
        super.onDestroy();
        //stop the gui updater
        guiUpdater.removeCallbacks(guiUpdateTask);
    }
    
    protected void initSongDetails(){
        song = mPlayer.getCurrentMedia();            
        
        //make sure that a song is indeed playing
        if (song!=null){
        
            //details
            TextView titleCaption = (TextView) this.findViewById(R.id.miniplayer_title);    
            titleCaption.setText(song.Title);
            
            TextView artistCaption = (TextView) this.findViewById(R.id.miniplayer_artist);    
            artistCaption.setText(song.Artist);
            
            TextView albumCaption = (TextView) this.findViewById(R.id.miniplayer_album);    
            albumCaption.setText(song.Album.getAlbumName());
            
            ImageView image = (ImageView) this.findViewById(R.id.miniplayer_image); 
            image.setImageBitmap(song.getAlbumImage(this));
            
            //seekbar
            songProgress = (ProgressBar) this.findViewById(R.id.miniplayer_progress);        
            songProgress.setMax(  mPlayer.getCurrentTrackDurationInSeconds() );                        
            
            timeElapsed = (TextView) this.findViewById(R.id.miniplayer_timeElapsed);    
            timeElapsed.setText(ConvertUtils.formatTime(mPlayer.getCurrentTrackDurationInSeconds()));
        
        }
        else{
               playermini.setVisibility(View.GONE);
        }
    }
    
    protected void updateSongDetails(){    
        //make sure this won't happen while dragging
        if (song!=null){
            timeElapsed.setText(ConvertUtils.formatTime(mPlayer.getPlaybackPosition().getPosition()/1000));
            songProgress.setProgress(mPlayer.getPlaybackPosition().getPosition()/1000);
        }
    }
                    
    protected void initUI() {
    	firstRun1();
    }
    
    private void firstRun1() {
    	firstRunPreferences();
    	if (getFirstRun()) {
            dialog = new Dialog(this);            
            dialog.setContentView(R.layout.firstrun);
            dialog.setTitle(R.string.firstrun1_title);
            dialog.show();
            
            Button btn_selectrootdir = (Button) dialog.findViewById(R.id.btn_firstrun);            
            btn_selectrootdir.setText("Select root dir");
            
            btn_selectrootdir.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
	            	Intent pickFolder = new Intent();
	                pickFolder.setAction(Intent.ACTION_PICK );
	                Uri theUri = Uri.parse("folder://" + Environment.getExternalStorageDirectory().getPath() ) ;
	                pickFolder.setData(theUri);
	                startActivityForResult(pickFolder, KinoPreferences.PICKMUSICDIR);
				}
			});
    	}
    }
    
    private void firstRun2(){
    	mTaskMaster.addTask(new UpdateLibrary(library));

        dialog = new Dialog(this);            
        dialog.setContentView(R.layout.firstrun);
        dialog.setTitle(R.string.firstrun1_title);
        dialog.show();
        
        Button btn_ok = (Button) dialog.findViewById(R.id.btn_firstrun);            
        btn_ok.setText("Ok");

        TextView txt_firstrun = (TextView) dialog.findViewById(R.id.txt_firstrun);
        txt_firstrun.setText(R.string.firstrun2);
        
        btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
            	dialog.dismiss();			
			}
		});
    	
    	setRunned();
    }
    
    // Listen for results.
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
        switch (requestCode) {
            case KinoPreferences.PICKMUSICDIR:
                // This is the standard resultCode that is sent back if the
                // activity crashed or didn't doesn't supply an explicit result.
                if (resultCode == RESULT_CANCELED){
                    //
                } 
                else if (resultCode == RESULT_OK) {             
                	String mp3dir = data.getData().getPath();
                    
                	//Toast.makeText(this, mp3dir ,Toast.LENGTH_LONG).show();
                	SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
                	prefs.putString("musicDir", mp3dir).commit();
                	dialog.dismiss();
                	firstRun2();
                }
            default:
                break;
        }
    }
    
    public TaskMasterService getTaskMaster(){
        return mTaskMaster;
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.kinoui_menu, menu);
        return true;
    }
        
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.menu_options_updatelibrary:
            mTaskMaster.addTask(new UpdateLibrary(library));
            return true;
        case R.id.menu_options_exit:
            // Ask the user if they want to quit
            new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Exit")
            .setMessage("Are you sure you want to quit?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    //moveTaskToBack(true);
                    kino.shutDown();
                }
            })
            .setNegativeButton("No", null).show();
            return true;
            //TODO opher- add the cancel to the settings
            /*
        case R.id.menu_options_removeimages:
            // Ask the user if they want to remove images
            new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Exit")
            .setMessage("Are you sure you want to remove all media images?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,
                                    int which) {
                    mTaskMaster.addTask(new CleanMediaFiles(
                            library));
                }
            })
            .setNegativeButton("No", null).show();
            return true;
            */
            
        case R.id.menu_options_preferences:
            Intent prefsIntent=new Intent(KinoUI.this,KinoPreferences.class);
            startActivity(prefsIntent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    public void fadein_full(View v) {        
        v.startAnimation(aFadeinfull);
    }
    
    protected void fadein_partial(View v) {        
        v.startAnimation(aFadeinpartial);
    }
    
    protected void fadeout_partial(View v) {        
        v.startAnimation(aFadeoutpartial);
    }
    
    public void scheduleTask(Runnable task, int millisecs){                
                  guiUpdater.postDelayed(task,millisecs);  
    }
   
    public class GestureActions{        
        public void swipeRight() {}        
        public void swipeLeft() {}     
        public  void touch() {}
    }
    
    public class GenericGestureDetector extends SimpleOnGestureListener {
        private GestureActions mGestureActions;
        
        GenericGestureDetector(GestureActions gestureActions){
            mGestureActions=gestureActions;
        }
        
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {            
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mGestureActions.swipeLeft();
                    return true;
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mGestureActions.swipeRight();
                    return true;
                }
      
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }
    
    
    /**
     * get if this is the first run
     *
     * @return returns true, if this is the first run
     */
        public boolean getFirstRun() {
        return mPrefs.getBoolean("firstRun", true);
     }
     
     /**
     * store the first run
     */
     public void setRunned() {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putBoolean("firstRun", false);
        edit.commit();
     }
          
     
     /**
     * setting up preferences storage
     */
     public void firstRunPreferences() {
        Context mContext = this.getApplicationContext();
        mPrefs = mContext.getSharedPreferences("myAppPrefs", 0); //0 = mode private. only this app can read these preferences
     }
}
