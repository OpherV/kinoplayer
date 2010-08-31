package com.android.kino.musiclibrary;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
 
 
public class LastfmHandler extends DefaultHandler{
       
        protected final String APIKEY = "2332b55ff029eb4c95a453ae370e16ae";        
        
        protected HashMap<String,Boolean> inTag = new HashMap<String,Boolean>();       
        
        protected String localTag=null;
        protected HashMap<String,String> currentAtts=null;
        
                                         
        @Override
        public void startElement(String namespaceURI, String localName,
                        String qName, Attributes atts) throws SAXException {
        	
        	inTag.put(localName, true);
        	localTag=localName;
        	currentAtts=new HashMap<String,String>();
        	for (int x=0; x<atts.getLength();x++){
        		currentAtts.put(atts.getLocalName(x),atts.getValue(x));
        	}
        
        }
       
        /** Gets be called on closing tags like:
         * </tag> */
        @Override
        public void endElement(String namespaceURI, String localName, String qName)
                        throws SAXException {
        	inTag.put(localName, false);
        	localTag=null;
        	currentAtts=null;
        }
       
        protected boolean inTag(String tagName){
        	if (inTag.get(tagName)==null){
        		return false;
        	}
        	else{
        		return inTag.get(tagName);
        	}
        }

		

}