package com.xtone.game87873.section.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author huangzx
 * 账户共享类
 * **/
@DatabaseTable(tableName="tb_user_account")
public class UserAccount {
	
	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(columnName="mobile")
	private String mobile;
	@DatabaseField(columnName="pwd")
	private String pwd;
	
	public UserAccount() {
		
	}

	public UserAccount(String mobile, String pwd) {
		super();
		this.mobile = mobile;
		this.pwd = pwd;
	}
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
}
