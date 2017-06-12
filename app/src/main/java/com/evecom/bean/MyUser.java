package com.evecom.bean;

import cn.bmob.v3.BmobObject;


/**
 * 实体类，封装用户信息
 * 用户对象要保存到Bmob服务器的数据库中
 *
 * 因此必须继承自BmobObject
 *
 * Bmob服务器数据库不支持Java的8大基本数据类型
 * 所以，要使用基本类型的装箱类
 *
 * @author pjy
 *
 */
public class MyUser extends BmobObject{

	//属性
	private String avatar;		//头像（头像图片在服务器上的地址）
	private String username;	//用户名
	private String password;	//密码
	private Boolean gender;		//true男    false女
	/**
	 * 签名
	 */
	private String qianming;
	/**
	 * 密保问题
	 */
	private String qustion;
	/**
	 * 密保答案
	 */
	private String answer;

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getGender() {
		return gender;
	}

	public void setGender(Boolean gender) {
		this.gender = gender;
	}

	public String getQianming() {
		return qianming;
	}

	public void setQianming(String qianming) {
		this.qianming = qianming;
	}

	public String getQustion() {
		return qustion;
	}

	public void setQustion(String qustion) {
		this.qustion = qustion;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@Override
	public String toString() {
		return "MyUser{" +
				"avatar='" + avatar + '\'' +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", gender=" + gender +
				", qianming='" + qianming + '\'' +
				", qustion='" + qustion + '\'' +
				", answer='" + answer + '\'' +
				'}';
	}
}
