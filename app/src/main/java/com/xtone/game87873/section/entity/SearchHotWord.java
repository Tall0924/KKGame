package com.xtone.game87873.section.entity;

/**
 * 搜索热词
 * @author huangzx
 * 
 * **/
public class SearchHotWord {

	private long game_id;
	private String name_zh;
	private String icon;
	public long getGame_id() {
		return game_id;
	}
	
	public SearchHotWord() {
		super();
	}

	public SearchHotWord(long game_id, String name_zh, String icon) {
		super();
		this.game_id = game_id;
		this.name_zh = name_zh;
		this.icon = icon;
	}

	public void setGame_id(long game_id) {
		this.game_id = game_id;
	}
	public String getName_zh() {
		return name_zh;
	}
	public void setName_zh(String name_zh) {
		this.name_zh = name_zh;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Override
	public String toString() {
		return "SearchHotWord [game_id=" + game_id + ", name_zh=" + name_zh
				+ "]";
	}
	
}
