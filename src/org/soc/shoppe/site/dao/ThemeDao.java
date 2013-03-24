package org.soc.shoppe.site.dao;

import java.util.List;

import org.soc.common.exception.DaoException;
import org.soc.shoppe.site.vo.ThemeVO;

public interface ThemeDao {

	public Boolean saveTheme(ThemeVO themeVO) throws DaoException;

	public ThemeVO getTheme(String themename) throws DaoException;

	public Boolean deleteTheme(String themename) throws DaoException;

	public List<ThemeVO> getAllThemes() throws DaoException;

	public ThemeVO getThemeByImageUrl(String imageurl) throws DaoException;

}
