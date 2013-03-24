package org.soc.shoppe.site.service;

import java.util.List;
import org.soc.shoppe.site.exception.ThemeException;
import org.soc.shoppe.site.vo.ThemeVO;

import com.google.appengine.api.blobstore.BlobKey;

public interface ThemeService {

	public Boolean addTheme(ThemeVO themeVO) throws ThemeException;

	public ThemeVO loadTheme(String themename) throws ThemeException;

	public Boolean removeTheme(String themename) throws ThemeException;

	public Boolean validateTheme(String themename);

	public List<ThemeVO> loadAllThemes() throws ThemeException;

	public String getThemeImage(String themename);

	public String getThemeFile(String themename);

	public BlobKey getFileBlobKeyFromImageUrl(String imageurl) throws ThemeException;

}
