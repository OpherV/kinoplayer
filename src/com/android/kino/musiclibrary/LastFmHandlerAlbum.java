package com.android.kino.musiclibrary;

import java.net.URLEncoder;
import java.util.HashMap;

public class LastFmHandlerAlbum extends LastfmHandler{

	private lastfmAlbumDetails albumDetails;
	
    public String getAlbumQueryURL(String artistTitle, String albumTitle){
    	return "http://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key="+APIKEY+"&artist="+URLEncoder.encode(artistTitle)+"&album="+URLEncoder.encode(albumTitle);
    }
    
    public LastFmHandlerAlbum(){
    	albumDetails=new lastfmAlbumDetails();
    }
    
    public lastfmAlbumDetails getAlbumDetails(){
    	return albumDetails;
    }
        
    @Override
    public void characters(char ch[], int start, int length) {
            
    	if (inTag("name") && !inTag("tracks")){
            	albumDetails.name=new String(ch, start, length);
         }
    	
    	if (inTag("artist")){
        	albumDetails.artist=new String(ch, start, length);
    	}
          
    	if (inTag("releasedate")){
        	albumDetails.releaseDate=new String(ch, start, length);
    	}
    	if (inTag("image")){
        	albumDetails.images.put(currentAtts.get("size"),new String(ch, start, length));
    	}
    	
    	if (inTag("wiki") && localTag!=null && !localTag.equals("wiki")){
        	albumDetails.wiki.put(localTag,new String(ch, start, length));
    	}
            
      }    
    
	public class lastfmAlbumDetails{
		public String name;
		public String artist;
		public String releaseDate;
		public HashMap<String, String> images;
		public HashMap<String, String> wiki;
		
		public lastfmAlbumDetails() {			
			images=new HashMap<String,String>();
			wiki=new HashMap<String,String>();
		}
		
	}
}
