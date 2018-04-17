package com.xtone.game87873.section.entity;

import java.util.List;

/**
 * 类型
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-14 上午9:51:20
 */
public class TypeEntity {

	private long id;
	private String name;
	private int type;
	private String imgUrl;
	private List<TypeTagEntity> tags;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<TypeTagEntity> getTags() {
		return tags;
	}

	public void setTags(List<TypeTagEntity> tags) {
		this.tags = tags;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

}
