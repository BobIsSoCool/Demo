package com.evecom.bean;

public class Data {

	private String source_url;

	private String nick_name;

	private String title;

	private String source_name;

	private String avatar;

	private String update_time;

	private int ups;

	private int comments;


	public void setSource_url(String source_url) {
		this.source_url = source_url;
	}

	public String getSource_url() {
		return this.source_url;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getNick_name() {
		return this.nick_name;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public void setSource_name(String source_name) {
		this.source_name = source_name;
	}

	public String getSource_name() {
		return this.source_name;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getAvatar() {
		return this.avatar;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getUpdate_time() {
		return this.update_time;
	}

	public void setUps(int ups) {
		this.ups = ups;
	}

	public int getUps() {
		return this.ups;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	public int getComments() {
		return this.comments;
	}

	/*@Override
	public String toString() {
		return "Data [desc=" + desc + ", tag=" + tag + ", forum_name="
				+ forum_name + ", source_url=" + source_url + ", source_type=" + source_type + ", type=" + type
				+ ", id=" + id + ", nick_name=" + nick_name + ", rank=" + rank
				+ ", title=" + title + ", created_at=" + created_at
				+ ", source_name=" + source_name + ", is_markdown="
				+ is_markdown + ", sub_source_name=" + sub_source_name
				+ ", category_id=" + category_id + ", pic=" + pic + ", avatar="
				+ avatar + ", operator=" + operator + ", url=" + url + ", ip="
				+ ip + ", is_expert=" + is_expert + ", user_name=" + user_name
				+ ", expert_username=" + expert_username + ", operate_time="
				+ operate_time + ", category_name=" + category_name
				+ ", fav_num=" + fav_num + ", update_time=" + update_time
				+ ", forum_id=" + forum_id + ", views=" + views
				+ ", from_type=" + from_type + ", downs=" + downs + ", ups="
				+ ups + ", in_white_list=" + in_white_list + ", comments="
				+ comments + "]";
	}*/
	
	
}