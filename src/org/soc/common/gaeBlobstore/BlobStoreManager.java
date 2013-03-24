package org.soc.common.gaeBlobstore;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;

public class BlobStoreManager {
	
	Logger log = Logger.getLogger(BlobStoreManager.class);
	
	public String saveFile(String filename,String contentType,byte[] bytes) {
		BlobKey blobKey = null;
		try{
		    boolean lock = true;		    
		    FileWriteChannel writeChannel = null;
		    FileService fileService = FileServiceFactory.getFileService();
		    AppEngineFile file = fileService.createNewBlobFile(contentType,filename);
		    writeChannel = fileService.openWriteChannel(file, lock);
		    writeChannel.write(ByteBuffer.wrap(bytes));
		    writeChannel.closeFinally();
		    while(blobKey==null)
		    	blobKey = fileService.getBlobKey(file);
		}catch(IOException ioException){
			log.log(Priority.DEBUG, "Error in save to blobstore,cause: "+ioException);
		}catch (Exception exception) {
			log.log(Priority.DEBUG, "Error in save to blobstore,cause: "+exception);
		}		
	    return blobKey==null ? null : blobKey.getKeyString();
	}
	
	public InputStream getFile(String blobKey) {
		InputStream iStream = null;
		try{
			BlobKey blobkey = new BlobKey(blobKey);
			ChainedBlobstoreInputStream inputStream = new ChainedBlobstoreInputStream(blobkey);
			iStream = inputStream;
		}catch(IOException ioException){
			log.log(Priority.DEBUG, "Error in get file from blobstore,cause: "+ioException);
		}catch(Exception exception){
			log.log(Priority.DEBUG, "Error in get file from blobstore,cause: "+exception);
		}
		return iStream;
	}
}
