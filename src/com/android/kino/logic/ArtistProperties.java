package com.android.kino.logic;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.android.kino.Kino;
import com.android.kino.logic.tasks.FetchArtistImages;
import com.android.kino.ui.KinoUI;
import com.android.kino.utils.ConvertUtils;

public class ArtistProperties{
	private String mArtistName=null;
	private int mTotalSongs;
	private Bitmap mArtistImage=null;
	private boolean mImageDisabled=false;
	private boolean searchingForArtistImage=false;
	
	public ArtistProperties(String artistName,int totalSongs){
		mArtistName=artistName;
		mTotalSongs=totalSongs;
	}
	
    public ArtistProperties(Parcel in) {        
        mArtistName = in.readString();
        mTotalSongs = in.readInt();
        mArtistImage=null;
    }
	
	public ArtistProperties(String artistTitle) {
		mArtistName=artistTitle;
	}

	public String getName(){
		 return mArtistName;
	}
	
	public int getTotalSongs(){
		return mTotalSongs;
	}
	
	public void setTotalSongs(int totalSongs){
		mTotalSongs=totalSongs;
	}
	
	 public static final Parcelable.Creator<ArtistProperties> CREATOR = new Parcelable.Creator<ArtistProperties>() {
	        public ArtistProperties createFromParcel(Parcel in) {
	            return new ArtistProperties(in);
	        }

	        public ArtistProperties[] newArray(int size) {
	            return new ArtistProperties[size];
	        }
	    };

	
    public Bitmap getArtistImage(KinoUI kinoui){
		if (mImageDisabled){
			return null;
		}
    	
		if (mArtistImage!=null){
			return mArtistImage;
		}

		//TODO make sure that the SDcard is properly mounted    	
		String artistImagePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Kino.ARTIST_DIR;
		String artistFileNameJPG=ConvertUtils.safeFileName(mArtistName)+".jpg";
		String artistFileNamePNG=ConvertUtils.safeFileName(mArtistName)+".png";
		File artistImageFileJPG = new File(artistImagePath,artistFileNameJPG);
		File artistImageFilePNG = new File(artistImagePath,artistFileNamePNG);
		
		if (artistImageFilePNG.exists()){			
			mArtistImage = BitmapFactory.decodeFile(artistImageFilePNG.getAbsolutePath());
		}
		else if (artistImageFileJPG.exists()){			
			mArtistImage = BitmapFactory.decodeFile(artistImageFileJPG.getAbsolutePath());
		}
		else{	
		
			if (!searchingForArtistImage){				
				Log.e(kinoui.getClass().getName(),"no artist image file: "+artistImagePath+"/"+ConvertUtils.safeFileName(mArtistName)+".*");
				TaskMasterService taskmaster=kinoui.getTaskMaster();
				taskmaster.addTask(new FetchArtistImages(this));
				searchingForArtistImage=true;
			}
			
		}
		
		return mArtistImage;
    }
    
	public void setImage(Bitmap image) {
		mArtistImage=image;
		
	}
    
	public void disableImage() {
		mImageDisabled=true;
		
	}

	public void stopSearching() {
		searchingForArtistImage=false;		
	}
	
}
