package com.android.kino.musiclibrary;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;

public class LastFmHandlerArtistImages extends LastfmHandler{
	private LinkedList<lastFMArtistImage> images =new LinkedList<lastFMArtistImage>();
	private lastFMArtistImage currentLastFMImage; 
	
	public String getArtistQueryURL(String mArtistTitle, int limit) {
		return "http://ws.audioscrobbler.com/2.0/?method=artist.getimages&artist="+URLEncoder.encode(mArtistTitle)+"&api_key="+APIKEY+"&limit="+limit;
	}
    
        
    @Override
    public void characters(char ch[], int start, int length) {
        super.characters(ch, start, length);
        
    	if (inTag("image") && localTag!=null && localTag.equals("image")){
            	currentLastFMImage=new lastFMArtistImage();
            	images.add(currentLastFMImage);
         }
    	
    	if (inTag("sizes") && localTag!=null && localTag.equals("size")){
    		currentLastFMImage.imageURLS.put(currentAtts.get("name"),new String(ch, start, length));
    	}          
   
            
      }    
    
    public LinkedList<lastFMArtistImage> getArtistImages(){
    	return images;
    }
    
	public class lastFMArtistImage{

		public HashMap<String, String> imageURLS;
		public HashMap<String, String> wiki;
		
		public lastFMArtistImage() {			
			imageURLS=new HashMap<String,String>();			
		}
		
	}

}
