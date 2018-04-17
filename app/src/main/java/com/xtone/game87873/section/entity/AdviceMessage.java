package com.xtone.game87873.section.entity;

public class AdviceMessage {
	public static final int MESSAGE_TYPE_LEFT = 1;
	public static final int MESSAGE_TYPE_RIGHT = 2;
	private String content;
	private int type;
	
	public AdviceMessage() {
		super();
	}
	
	public AdviceMessage(String content, int type) {
		super();
		this.content = content;
		this.type = type;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
}
