package com.xtone.game87873.section.entity;

import java.io.Serializable;
/***
 * @author huangzx
 * ***/
public class GameGiftEntity implements Serializable{
	private static final long serialVersionUID = 5405405955827232905L;
	public static final int IS_RECEIVE_YES = 1;
	public static final int IS_RECEIVE_NO = 0;
	private long id;
	private String name;
	private long game_id;
	private String content;//礼包内容
	private int code_num;//礼包码总数
	private int status;//礼包状态 1、领取、2淘号、3结束、4预约
	private String icon;
	private int surplus_num;//礼包码剩余数
	private String range;//
	private String explain;//使用说明
	private String tips;//温馨提示
	private String remarks;
	private String start_time;
	private String end_time;
	private String apk_package_name;
	private String apk_version_code;
	private String code;
	private int is_download = 1;//是否需要下载 1需要 0不需要
	private int is_receive = 0;//指定用户是否领取过该礼包（0未领取，1领取）
	
	private int alreadyForNo;
	private boolean delete;
	
	public GameGiftEntity() {
		super();
	}
	
	//用于礼包专区列表
	public GameGiftEntity(long id, String name, String content,
			int code_num, int status, String icon, int surplus_num, String end_time) {
		super();
		this.id = id;
		this.name = name;
		this.content = content;
		this.code_num = code_num;
		this.status = status;
		this.icon = icon;
		this.surplus_num = surplus_num;
		this.end_time = end_time;
	}
	
	//用于礼包专区详情
	public GameGiftEntity(long id, String name, long game_id, String content,
			int code_num, int status, String icon, int surplus_num,
			String range, String explain, String tips,
			String remarks, String start_time, String end_time,
			String apk_package_name, String apk_version_code) {
		super();
		this.id = id;
		this.name = name;
		this.game_id = game_id;
		this.content = content;
		this.code_num = code_num;
		this.status = status;
		this.icon = icon;
		this.surplus_num = surplus_num;
		this.range = range;
		this.explain = explain;
		this.tips = tips;
		this.remarks = remarks;
		this.start_time = start_time;
		this.end_time = end_time;
		this.apk_package_name = apk_package_name;
		this.apk_version_code = apk_version_code;
	}
	
	//用于我的礼包列表
	public GameGiftEntity(long id, String name, String icon, String code,int code_num, int surplus_num) {
		super();
		this.id = id;
		this.name = name;
		this.icon = icon;
		this.code = code;
		this.code_num = code_num;
		this.surplus_num = surplus_num;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getExplain() {
		return explain;
	}

	public void setExplain(String explain) {
		this.explain = explain;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

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
	public long getGame_id() {
		return game_id;
	}
	public void setGame_id(long game_id) {
		this.game_id = game_id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getCode_num() {
		return code_num;
	}
	public void setCode_num(int code_num) {
		this.code_num = code_num;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public int getSurplus_num() {
		return surplus_num;
	}
	public void setSurplus_num(int surplus_num) {
		this.surplus_num = surplus_num;
	}

	public int getAlreadyForNo() {
		return alreadyForNo;
	}

	public void setAlreadyForNo(int alreadyForNo) {
		this.alreadyForNo = alreadyForNo;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getApk_package_name() {
		return apk_package_name;
	}

	public void setApk_package_name(String apk_package_name) {
		this.apk_package_name = apk_package_name;
	}

	public String getApk_version_code() {
		return apk_version_code;
	}

	public void setApk_version_code(String apk_version_code) {
		this.apk_version_code = apk_version_code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public int getIs_download() {
		return is_download;
	}

	public void setIs_download(int is_download) {
		this.is_download = is_download;
	}

	public int getIs_receive() {
		return is_receive;
	}

	public void setIs_receive(int is_receive) {
		this.is_receive = is_receive;
	}

	@Override
	public String toString() {
		return "GameGiftEntity [id=" + id + ", name=" + name + ", game_id="
				+ game_id + ", content=" + content + ", code_num=" + code_num
				+ ", status=" + status + ", icon=" + icon + ", surplus_num="
				+ surplus_num + ", range=" + range + ", explain=" + explain
				+ ", tips=" + tips + ", alreadyForNo=" + alreadyForNo
				+ ", remarks=" + remarks + ", start_time=" + start_time
				+ ", end_time=" + end_time + ", apk_package_name="
				+ apk_package_name + ", apk_version_code=" + apk_version_code
				+ ", code=" + code + "]";
	}

}
