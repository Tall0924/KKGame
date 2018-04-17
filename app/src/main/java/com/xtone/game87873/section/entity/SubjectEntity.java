package com.xtone.game87873.section.entity;

/**
 * 专题
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-15 上午9:30:10
 */
public class SubjectEntity {

	private long id;
	private String title;
	// 图片地址
	private String thumb;
	private String content;
	private String publishTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

}
