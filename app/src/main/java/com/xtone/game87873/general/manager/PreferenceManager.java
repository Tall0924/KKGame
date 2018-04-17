package com.xtone.game87873.general.manager;

import com.xtone.game87873.general.utils.SharedPreferencesUtils;
import com.xtone.game87873.section.entity.TypeTagEntity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceManager {
	private Context mContext;

	public PreferenceManager(Context context) {
		this.mContext = context;
	}

	public SharedPreferences getSharedPreferences() {
		return mContext.getSharedPreferences("game87873", Context.MODE_PRIVATE);
	}

	private Editor getEditer() {
		return getSharedPreferences().edit();
	}
	
	public long getUserId() {
		SharedPreferences spf = getSharedPreferences();
		return spf.getLong("user_id", -1);
	}

	public void setUserId(long user_id) {
		Editor editor = getEditer();
		editor.putLong("user_id", user_id);
		editor.commit();
	}
	
	public String getUserNick() {
		SharedPreferences spf = getSharedPreferences();
		return spf.getString("nick", null);
	}

	public void setUserNick(String nick) {
		Editor editor = getEditer();
		editor.putString("nick", nick);
		editor.commit();
	}
	
	public String getUserMobile() {
		SharedPreferences spf = getSharedPreferences();
		return spf.getString("mobile", null);
	}

	public void setUserMobile(String mobile) {
		Editor editor = getEditer();
		editor.putString("mobile", mobile);
		editor.commit();
	}
	
	public String getUserFigureUrl() {
		SharedPreferences spf = getSharedPreferences();
		return spf.getString("figureUrl", null);
	}

	public void setUserFigureUrl(String figureUrl) {
		Editor editor = getEditer();
		editor.putString("figureUrl", figureUrl);
		editor.commit();
	}
	
	public void setTypeTagEntity(TypeTagEntity entity){
		SharedPreferencesUtils.saveLong("TypeTagEntityId", entity.getId());
		SharedPreferencesUtils.saveString("TypeTagEntityImg", entity.getImg());
		SharedPreferencesUtils.saveString("TypeTagEntityName", entity.getName());
	}
	
	public TypeTagEntity getTypeTagEntity(){
		TypeTagEntity entity = new TypeTagEntity();
		entity.setId(SharedPreferencesUtils.getLong("TypeTagEntityId", 0));
		entity.setImg(SharedPreferencesUtils.getString("TypeTagEntityImg", null));
		entity.setName(SharedPreferencesUtils.getString("TypeTagEntityName", null));
		return entity;
	}
	
}
