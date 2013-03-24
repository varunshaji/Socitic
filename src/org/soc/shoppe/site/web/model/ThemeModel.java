package org.soc.shoppe.site.web.model;

import java.util.List;

import org.soc.shoppe.site.vo.ThemeVO;

public class ThemeModel {
	
	private ThemeVO themeVO;
	
	private List<ThemeVO> themes;

	public ThemeVO getThemeVO() {
		return themeVO;
	}

	public void setThemeVO(ThemeVO themeVO) {
		this.themeVO = themeVO;
	}

	public List<ThemeVO> getThemes() {
		return themes;
	}

	public void setThemes(List<ThemeVO> themes) {
		this.themes = themes;
	}
}
