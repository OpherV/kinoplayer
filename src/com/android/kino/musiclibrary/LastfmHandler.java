package com.android.kino.musiclibrary;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.android.kino.musiclibrary.LastFmHandlerArtistImages.lastFMArtistImage;
 
 
public class LastfmHandler extends DefaultHandler{
       
        protected final String APIKEY = "2332b55ff029eb4c95a453ae370e16ae";        
        
        protected HashMap<String,Boolean> inTag = new HashMap<String,Boolean>();       
        
        protected String localTag=null;
        protected String characterTag=null;
        protected HashMap<String,String> currentAtts=null;
        
                                      
        @Override
        public void startElement(String namespaceURI, String localName,
                        String qName, Attributes atts) {        	
        	inTag.put(localName, true);        	
        	localTag=localName;
        	currentAtts=new HashMap<String,String>();
        	for (int x=0; x<atts.getLength();x++){
        		currentAtts.put(atts.getLocalName(x),atts.getValue(x));
        	}
        
        }
       

        @Override
        public void endElement(String namespaceURI, String localName, String qName){
        	inTag.put(localName, false);
        	localTag=null;
        	currentAtts=null;
        }
        
        @Override
        public void characters(char ch[], int start, int length) {               
           	//this is so if we've read a starting tag and then a bunch of chars afterwards, it won't still think we're in that tag        	
        	if (characterTag==localTag){
        		localTag=null;
        	}
        	
        	characterTag=localTag;        	
                
          }    
        
 
       
        protected boolean inTag(String tagName){
        	if (inTag.get(tagName)==null){
        		return false;
        	}
        	else{
        		return inTag.get(tagName);
        	}
        }
        
    	protected URL buildURL(String urlString){
    		URL newURL=null;
    		try {
    			newURL = new URL(urlString);
    		} catch (MalformedURLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		return newURL;
    	}

		

}