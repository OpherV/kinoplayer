package com.android.kino.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.kino.Kino;
import com.android.kino.R;
import com.android.kino.logic.KinoMediaPlayer;
import com.android.kino.logic.KinoUser;
import com.android.kino.logic.MediaProperties;
import com.android.kino.logic.TaskMasterService;
import com.android.kino.logic.tasks.UpdateLibrary;
import com.android.kino.musiclibrary.Library;
import com.android.kino.utils.ConvertUtils;

public class KinoUI extends Activity implements KinoUser{
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
	private Animation aFadeoutfull;
		
	protected static final int SWIPE_MIN_DISTANCE = 120;
	protected static final int SWIPE_MAX_OFF_PATH = 250;
	protected static final int SWIPE_THRESHOLD_VELOCITY = 1500;      	
	
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
        mPlayer= kino.getPlayer();        
        library = kino.getLibrary();
        mTaskMaster= kino.getTaskMaster();
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
    protected void onDestroy() {    
    	super.onDestroy();
    	//stop the gui updater
    	guiUpdater.removeCallbacks(guiUpdateTask);
    }
    
    protected void initSongDetails(){
	    
		song = mPlayer.getCurrentMedia();
		
		playermini= (PlayerMini) this.findViewById(R.id.player_mini);		
		
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
					
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);		
		aFadeinpartial=AnimationUtils.loadAnimation(this, R.anim.fadein_partial);
		aFadeoutpartial=AnimationUtils.loadAnimation(this, R.anim.fadeout_partial);
		aFadeoutfull=AnimationUtils.loadAnimation(this, R.anim.fadein_full);			
		
		kino=Kino.getKino(this);		
		
		initUI();
		        			          
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

	
	protected void initUI(){}	
	
	
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
	    default:
	        return super.onOptionsItemSelected(item);
	    }
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
	
}
