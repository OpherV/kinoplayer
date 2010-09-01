package com.android.kino.logic;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.kino.Kino;
import com.android.kino.ui.KinoUI;
import com.android.kino.utils.ConvertUtils;

public class ArtistProperties implements Parcelable{
	private String mArtistName=null;
	private int mTotalSongs;
	private Bitmap mArtistImage=null;
	
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
	
	 public static final Parcelable.Creator<ArtistProperties> CREATOR = new Parcelable.Creator<ArtistProperties>() {
	        public ArtistProperties createFromParcel(Parcel in) {
	            return new ArtistProperties(in);
	        }

	        public ArtistProperties[] newArray(int size) {
	            return new ArtistProperties[size];
	        }
	    };

	@Override
	public int describeContents() {		
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mArtistName);
		dest.writeInt(mTotalSongs);		
		
	}
	
    public Bitmap getArtistImage(KinoUI kinoui){
		if (mArtistImage!=null){
			return mArtistImage;
		}

		//TODO make sure that the SDcard is properly mounted    	
		String artistImagePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Kino.ARTIST_DIR;
		String artistFileName=ConvertUtils.safeFileName(mArtistName)+".jpg";
		File artistImageFile = new File(artistImagePath,artistFileName);
		
		//TODO obviously, change this
		if (artistImageFile.exists()){
			mArtistImage = BitmapFactory.decodeFile(artistImageFile.getAbsolutePath());
		}
		else{
		//TODO fetch from lastfm	
		
		/*	if (!searchingForAlbumImage){				
				Log.e(kinoui.getClass().getName(),"no artist image file: "+albumImagePath+"/"+albumFileName);
				TaskMasterService taskmaster=kinoui.getTaskMaster();
				taskmaster.addTask(new FetchAlbumDetails(Artist, Album.Title));
				searchingForAlbumImage=true;
			}
			*/
		}
		
		return mArtistImage;
    }
	
}
