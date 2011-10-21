package com.android.kino.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class ImageUtils {

	//decodes image and scales it to reduce memory consumption
	public static Bitmap decodeFile(String path, int newsize){
	    try {
	    	File f=new File(path);
	        //Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

	        //The new size we want to scale to
	        final int REQUIRED_SIZE=newsize;

	        //Find the correct scale value. It should be the power of 2.
	        int width_tmp=o.outWidth, height_tmp=o.outHeight;
	        int scale=1;
	        while(true){
	            if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
	                break;
	            width_tmp/=2;
	            height_tmp/=2;
	            scale*=2;
	        }

	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize=scale;
	        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	    } catch (FileNotFoundException e) {}
	    return null;
	}
	
}
