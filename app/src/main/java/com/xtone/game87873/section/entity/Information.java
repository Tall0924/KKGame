package com.xtone.game87873.section.entity;

/**
 * 资讯
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-9-11 下午4:41:03
 */
public class Information {

	public static final int TYPE_NEWS = 1;
	public static final int TYPE_INDUSTRY = 2;
	public static final int TYPE_STRATEGY = 3;
	public static final int TYPE_ACTIVITY = 4;
	public static final int TYPE_EVALUATING = 5;
	private long id;
	private int type;// 1:新闻 ,2:行业,3:攻略,4:活动,5:评测
	private String title;
	private String quotes;//引用
	private String thumbnail;//缩略图
	private String publishTime;
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getQuotes() {
		return quotes;
	}

	public void setQuotes(String quotes) {
		this.quotes = quotes;
	}


	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}


}
