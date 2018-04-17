package com.xtone.game87873.section.entity;

public class UserInfo {
	private long id;
	private String nick;
	private String figureUrl;
	private String email;
	private String mobile;
	private int sex;
	private String birthday;
	private String regtime;
	private String check_email;
	private String check_mobile;
	public static final String ID = "id";
	public static final String FINATURE_URL = "figureurl";
	public static final String MOBILE = "mobile";
	public static final String SEX = "sex";
	public static final String BIRTH_DAY = "birthday";
	public static final String NICK = "nick";
	
	public UserInfo() {
		super();
	}
	
	public UserInfo(long id, String nick, String figureUrl) {
		super();
		this.id = id;
		this.nick = nick;
		this.figureUrl = figureUrl;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getFigureUrl() {
		return figureUrl;
	}
	public void setFigureUrl(String figureUrl) {
		this.figureUrl = figureUrl;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getRegtime() {
		return regtime;
	}

	public void setRegtime(String regtime) {
		this.regtime = regtime;
	}

	public String getCheck_email() {
		return check_email;
	}

	public void setCheck_email(String check_email) {
		this.check_email = check_email;
	}

	public String getCheck_mobile() {
		return check_mobile;
	}

	public void setCheck_mobile(String check_mobile) {
		this.check_mobile = check_mobile;
	}

	@Override
	public String toString() {
		return "UserInfo [id=" + id + ", nick=" + nick + ", figureUrl="
				+ figureUrl + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((birthday == null) ? 0 : birthday.hashCode());
		result = prime * result + ((nick == null) ? 0 : nick.hashCode());
		result = prime * result + sex;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserInfo other = (UserInfo) obj;
		if (birthday == null) {
			if (other.birthday != null)
				return false;
		} else if (!birthday.equals(other.birthday))
			return false;
		if (nick == null) {
			if (other.nick != null)
				return false;
		} else if (!nick.equals(other.nick))
			return false;
		if (sex != other.sex)
			return false;
		return true;
	}
	
}
