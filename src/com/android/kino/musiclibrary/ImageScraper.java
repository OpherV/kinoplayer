package com.android.kino.musiclibrary;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.android.kino.utils.ConvertUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;

public class ImageScraper {
	final String APIKEY="b25b959554ed76058ac220b7b2e0a026";	
	final String ALBUM_DIR="kino/images/albums";
	final String ARTIST_DIR="kino/images/artists";
	
	// TODO change this to get the dirs from a proper central place
//	
//	
//	public Bitmap getArtistImage(String artistName){
//		Bitmap artistImage=null;
//		String artistImageURL=fetchAndParseArtistXML(artistName);
//		if (artistImageURL!=null){			
//			artistImage=downloadImage(artistImageURL, artistName);
//		}
//		else{
//			Log.e("blah","artistimageurl is null!");
//		}
//		
//		return artistImage;
//	}
//	
//	private String fetchAndParseArtistXML(String artistName){
//		String artistImageURL=null;
//		
//        /* Create a new TextView to display the parsingresult later. */
//        try {
//                /* Create a URL we want to load some xml-data from. */
//        		String URLString="http://ws.audioscrobbler.com/2.0/?method=artist.getimages&artist="+URLEncoder.encode(artistName,"UTF-8")+"&api_key="+APIKEY+"&limit=1";
//        		Log.d("blah",URLString);
//                URL url = new URL(URLString);
//
//                /* Get a SAXParser from the SAXPArserFactory. */
//                SAXParserFactory spf = SAXParserFactory.newInstance();
//                SAXParser sp = spf.newSAXParser();
//
//                /* Get the XMLReader of the SAXParser we created. */
//                XMLReader xr = sp.getXMLReader();
//                /* Create a new ContentHandler and apply it to the XML-Reader*/
//                LastfmArtistImageHandler imageHandler = new LastfmArtistImageHandler();
//                xr.setContentHandler(imageHandler);
//               
//                InputSource inputSource=new InputSource(url.openStream());
//                
//                /* Parse the xml-data from our URL. */
//                xr.parse(inputSource);
//                /* Parsing has finished. */
//
//                /* Our ExampleHandler now provides the parsed data to us. */                
//                artistImageURL = imageHandler.getArtistImageURL();
//                Log.e("xml error", "fetched artistimageurl for "+artistName+" :"+artistImageURL);
//               
//               
//        } catch (Exception e) {
//                /* Display any Error to the GUI. */                
//                Log.e("xml error", "xml error", e);
//        }
//		
//        return artistImageURL;
//		
//	}
//	
//	private Bitmap downloadImage(String fileUrl, String artistName) {	    
//		URL myFileUrl = null;
//		Bitmap image = null;
//	    try {
//	        myFileUrl = new URL(fileUrl);
//	    } catch (MalformedURLException e) {
//	        // TODO Auto-generated catch block
//	        e.printStackTrace();
//	    }
//	    
//	    
//	    try {
//	        HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
//	        conn.setDoInput(true);
//	        conn.connect();
//	        
//	        InputStream is = conn.getInputStream();
//
//	        image = BitmapFactory.decodeStream(is);
//	        // this.imView.setImageBitmap(bmImg);
//	    } catch (IOException e) {
//	        // TODO Auto-generated catch block
//	        e.printStackTrace();
//	    }
//	    
//	    
//	    try {
//	        String filepath=Environment.getExternalStorageDirectory().getAbsolutePath(); 
//	        FileOutputStream fos = new FileOutputStream(filepath + "/" + ARTIST_DIR + "/"+ ConvertUtils.safeFileName(artistName) +".jpg"); 
//	        image.compress(CompressFormat.JPEG, 75, fos);
//	        fos.flush();
//	        fos.close();
//
//
//	    } catch (Exception e) {
//	        Log.e("MyLog", e.toString());
//	        
//	    }
//
//	    return image;
//	}	
	
}
