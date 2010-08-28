package com.android.kino.musiclibrary;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
 
 
public class LastfmArtistImageHandler extends DefaultHandler{
 
        // ===========================================================
        // Fields
        // ===========================================================
              
        private String artistImageURL;
        private boolean inTag=false;

               
        /** Gets be called on opening tags like:
         * <tag>
         * Can provide attribute(s), when xml was like:
         * <tag attribute="attributeValue">*/
        @Override
        public void startElement(String namespaceURI, String localName,
                        String qName, Attributes atts) throws SAXException {
        	//Log.d("blah",localName.equals("size") +" "+)
        		if (localName.equals("size") && atts.getValue("name").equals("extralarge")) {
        			 inTag=true;
                }
        }
       
        /** Gets be called on closing tags like:
         * </tag> */
        @Override
        public void endElement(String namespaceURI, String localName, String qName)
                        throws SAXException {
        	if (localName.equals("size") && inTag==true){
        		inTag=false;
        	}
        }
       
        /** Gets be called on the following structure:
         * <tag>characters</tag> */
        @Override
    public void characters(char ch[], int start, int length) {
                if(inTag){
                	artistImageURL=new String(ch, start, length);
        }
    }

		public String getArtistImageURL() {
			return artistImageURL;
		}
}