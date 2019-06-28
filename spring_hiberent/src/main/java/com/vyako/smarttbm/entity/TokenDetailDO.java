//package com.vyako.smarttbm.entity;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.Table;
//import javax.persistence.Transient;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonProperty;
//
///**
// * Entity class for Token table
// * 
// * @author sid
// * 
// */
//@Entity
//@Table(name = "token")
//public class TokenDetailDO {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
//	@Column
//	@JsonIgnore
//	public int id;
//
//	@Id
//	@Column(name = "user_id")
//	@JsonIgnore
//	public int user_id;
//
//	@Column(name = "token")
//	public String token;
//
//	@Column(name = "expiry")
//	@JsonIgnore
//	public long expiry;
//
//	@JsonProperty("user")
//	@Transient
//	public UserDetailDO user;
//
//	public int getId() {
//		return id;
//	}
//
//	public void setId(int id) {
//		this.id = id;
//	}
//
//	public String getToken() {
//		return token;
//	}
//
//	public void setToken(String token) {
//		this.token = token;
//	}
//
//	public long getExpiry() {
//		return expiry;
//	}
//
//	public void setExpiry(long expiry) {
//		this.expiry = expiry;
//	}
//
//	public UserDetailDO getUser() {
//		return user;
//	}
//
//	public void setUser(UserDetailDO user) {
//		this.user = user;
//	}
//
//	public int getUser_id() {
//		return user_id;
//	}
//
//	public void setUser_id(int user_id) {
//		this.user_id = user_id;
//	}
//
//}
