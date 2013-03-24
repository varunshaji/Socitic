package org.soc.shoppe.site.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.common.exception.DaoException;
import org.soc.common.gaeBlobstore.ChainedBlobstoreInputStream;
import org.soc.shoppe.site.dao.ThemeDao;
import org.soc.shoppe.site.exception.ThemeException;
import org.soc.shoppe.site.service.ThemeService;
import org.soc.shoppe.site.vo.ThemeVO;
import org.springframework.web.multipart.MultipartFile;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;

public class ThemeServiceImpl implements ThemeService {
	
	Logger log = Logger.getLogger(ThemeServiceImpl.class);
	
	private ThemeDao themeDao;
	
	@Override
	public Boolean addTheme(ThemeVO themeVO) throws ThemeException {
		boolean result = false;
		if(isNewTheme(themeVO.getThemeName())) {
			try{
				ThemeVO newThemeVO = upLoadToBlobstore(themeVO);
				if(newThemeVO !=null) {
					result = themeDao.saveTheme(newThemeVO );
				}
			}catch(DaoException daoException){
				log.log(Priority.DEBUG,"DaoException during addTheme",daoException);
				throw new ThemeException(ThemeException.ERROR_ADD, daoException);
			}catch(Exception exception){
				log.log(Priority.DEBUG,"Unknown exeption during add theme",exception);
				throw new ThemeException(ThemeException.ERROR_UNKNOWN, exception);
			}
		}
		return result;
	}
	
	@Override
	public ThemeVO loadTheme(String themename) throws ThemeException {
		ThemeVO themeVO = null;
		try{
			themeVO = themeDao.getTheme(themename);
		}catch(DaoException daoException){
			log.log(Priority.DEBUG,"DaoException while loading theme:"+themename,daoException);
			throw new ThemeException(ThemeException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Unknown exeption while loading theme:"+themename,exception);
			throw new ThemeException(ThemeException.ERROR_UNKNOWN, exception);
		}
		return themeVO;
	}
	
	@Override
	public Boolean removeTheme(String themename) throws ThemeException {
		boolean result = false;
		try{
			result = themeDao.deleteTheme(themename);
		}catch(DaoException daoException){
			log.log(Priority.DEBUG,"DaoException occured during theme delete:"+themename,daoException);
			throw new ThemeException(ThemeException.ERROR_DELETE, daoException);
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Unknown exception occured during theme delete:"+themename,exception);
			throw new ThemeException(ThemeException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public List<ThemeVO> loadAllThemes() throws ThemeException {
		List<ThemeVO> result = new ArrayList<ThemeVO>();
		try{
			result = themeDao.getAllThemes();
			log.log(Priority.DEBUG,"successfully load all themes");
		}catch(DaoException daoException){
			log.log(Priority.DEBUG,"unable to load all themes",daoException);
			throw new ThemeException(ThemeException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception){
			log.log(Priority.DEBUG,"an unknown exception while loading all themes",exception);
			throw new ThemeException(ThemeException.ERROR_UNKNOWN, exception);
		}
		return result;
	}

	@Override
	public Boolean validateTheme(String themename) {
		return isNewTheme(themename);
	}
	
	@Override
	public String getThemeFile(String themename) {
		String result = null;
		try {
			ThemeVO themeVO = loadTheme(themename);
			if(themeVO!=null){
				 
			    BlobKey blobKey = themeVO.getBlobKey1();
			    ChainedBlobstoreInputStream inputStream = new ChainedBlobstoreInputStream(blobKey);
			    byte[] data = IOUtils.toByteArray(inputStream);
				log.log(Priority.DEBUG, "file bllobKey="+blobKey);
				FileService fileService = FileServiceFactory.getFileService();
				AppEngineFile file = fileService.getBlobFile(blobKey);
				log.log(Priority.DEBUG, "appengine file="+file);
				result = file.getFullPath();
				log.log(Priority.DEBUG, "result filepath:"+result);
			}
		}catch (ThemeException themeException) {
			themeException.printStackTrace();
			log.log(Priority.DEBUG, "themeException",themeException);
		}catch(Exception exception){
			log.log(Priority.DEBUG, "exception",exception);
		}
		return result;
	}
	
	@Override
	public String getThemeImage(String themename) {
		String result = null;
		try {
			ThemeVO themeVO = loadTheme(themename);
			if(themeVO!=null)
				result = themeVO.getImageUrl();
		}catch (ThemeException themeException) {
			themeException.printStackTrace();
			log.log(Priority.DEBUG, "themeException",themeException);
		}catch(Exception exception){
			log.log(Priority.DEBUG, "exception",exception);
		}
		return result;
	}
	
	@Override
	public BlobKey getFileBlobKeyFromImageUrl(String imageurl) throws ThemeException {
		BlobKey result = null;
		try{
			ThemeVO themeVO = themeDao.getThemeByImageUrl(imageurl);
			if(themeVO!=null)
				result = themeVO.getBlobKey1();
		}catch(DaoException daoException){
			log.log(Priority.DEBUG,"DaoException while loading theme with imageurl:"+imageurl,daoException);
			throw new ThemeException(ThemeException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Unknown exeption while loading theme  with imageurl:"+imageurl,exception);
			throw new ThemeException(ThemeException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	private Boolean isNewTheme(String themename) {
		boolean result = false;
		try{
			ThemeVO themeVO = themeDao.getTheme(themename);
			if(themeVO==null)
				result = true;
		}catch(DaoException daoException){
			log.log(Priority.DEBUG,"DaoException while validating theme:"+themename,daoException);
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Unknown exeption while validating theme:"+themename,exception);
		}
		return result;
	}
	
	private ThemeVO upLoadToBlobstore(ThemeVO themeVO) {
		ThemeVO newThemeVO = null;
		try{
			MultipartFile fileData1 = themeVO.getFile1();
			MultipartFile fileData2 = themeVO.getFile2();
		
		    BlobKey blobKey = null;
		    //..........file1......
		    System.out.println("___________writing file______________");
		    blobKey = putInBlobStore(fileData1);
		    log.log(Priority.DEBUG,"BlobKey1:"+blobKey);
		    themeVO.setBlobKey1(blobKey);
		    blobKey = null;
		    //........../file1......
		    //..........file2......
		    System.out.println("___________writing image______________");
		    blobKey = putInBlobStore(fileData2);
		    log.log(Priority.DEBUG,"BlobKey2:"+blobKey);
		    themeVO.setBlobKey2(blobKey);
		    System.out.println("___________getting imageUrl______________");
		    ImagesService imagesService = ImagesServiceFactory.getImagesService();
		    String imageurl = imagesService.getServingUrl(blobKey);
		    log.log(Priority.DEBUG,"imageUrl ="+imageurl);
		    themeVO.setImageUrl(imageurl.trim());
		    newThemeVO = themeVO;
		    //........../file2......
		}catch (Exception exception) {
			log.log(Priority.DEBUG,"catch exception: "+exception);
		}	
		return newThemeVO;
	}
	
    public BlobKey putInBlobStore(MultipartFile fileData) throws IOException {
    	FileService fileService = FileServiceFactory.getFileService();
	   	AppEngineFile file = fileService.createNewBlobFile(fileData.getContentType(),fileData.getOriginalFilename());
	    boolean lock = true;
	    byte[] tempbuffer = new byte[8192];
	    int readBytes = 0;
	    FileWriteChannel writeChannel = null;
	    BlobKey blobKey = null;
	    writeChannel = fileService.openWriteChannel(file, lock);
	    InputStream inputStream2 = fileData.getInputStream();
	    while ((readBytes = inputStream2.read(tempbuffer, 0, 8192)) != -1) {
			System.out.println("===ddd=======");
			writeChannel.write(ByteBuffer.wrap(tempbuffer), null);
		}
	    writeChannel.closeFinally();
	    log.log(Priority.DEBUG,"complete image write");
	    while(blobKey==null)
	    	blobKey = fileService.getBlobKey(file);
	    return blobKey;
    }
	public ThemeDao getThemeDao() {
		return themeDao;
	}

	public void setThemeDao(ThemeDao themeDao) {
		this.themeDao = themeDao;
	}
}
