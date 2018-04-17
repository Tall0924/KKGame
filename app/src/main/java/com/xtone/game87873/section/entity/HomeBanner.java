package com.xtone.game87873.section.entity;

/**
 * HomeBanner.java
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-7 下午3:42:26
 */
public class HomeBanner {

	public static final String TYPE_GAME = "game";
	public static final String TYPE_SUBJECT = "topic";
	private String bannerUrl;
	private String type;
	private long id;
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBannerUrl() {
		return bannerUrl;
	}

	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
