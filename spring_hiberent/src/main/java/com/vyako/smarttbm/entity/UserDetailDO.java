//package com.vyako.smarttbm.entity;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.Table;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonProperty;
//
////[id,name,username, email, password, role_id (ref key), status, created, updated]
//
///**
// * Entity class for User table
// * 
// * @author sid
// * 
// */
//@Entity
//@Table(name = "users")
//public class UserDetailDO {
//
//	@JsonProperty("id")
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "id")
//	public int id;
//
//	@JsonProperty("name")
//	@Column(name = "name")
//	public String name;
//
//	@JsonProperty("username")
//	@Column(name = "username")
//	public String username;
//
//	@JsonProperty("email")
//	@Column(name = "email")
//	public String email;
//
//	@JsonIgnore
//	@Column(name = "password")
//	public String password;
//
//	@JsonProperty("role_id")
//	@Column(name = "role_id")
//	public int role_id;
//
//	@JsonProperty("login_status")
//	@Column(name = "login_status")
//	public int login_status;
//
//	@JsonProperty("status")
//	@Column(name = "status")
//	public int status;
//
//	@JsonProperty("created")
//	@Column(name = "created")
//	public String created;
//
//	@JsonProperty("updated")
//	@Column(name = "updated")
//	public String updated;
//
//	public int getId() {
//		return id;
//	}
//
//	public void setId(int id) {
//		this.id = id;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}
//
//	public int getRole_id() {
//		return role_id;
//	}
//
//	public void setRole_id(int role_id) {
//		this.role_id = role_id;
//	}
//
//	public int getStatus() {
//		return status;
//	}
//
//	public void setStatus(int status) {
//		this.status = status;
//	}
//
//	public String getUsername() {
//		return username;
//	}
//
//	public void setUsername(String username) {
//		this.username = username;
//	}
//
//	public String getCreated() {
//		return created;
//	}
//
//	public void setCreated(String created) {
//		this.created = created;
//	}
//
//	public String getUpdated() {
//		return updated;
//	}
//
//	public void setUpdated(String updated) {
//		this.updated = updated;
//	}
//
//	public String getEmail() {
//		return email;
//	}
//
//	public void setEmail(String email) {
//		this.email = email;
//	}
//
//	public int getLogin_status() {
//		return login_status;
//	}
//
//	public void setLogin_status(int login_status) {
//		this.login_status = login_status;
//	}
//
//	// @JsonProperty("full_name")
//	// @Column
//	// public String full_name;
//	//
//	// @JsonProperty("address")
//	// @Column
//	// public String address;
//	//
//	// @JsonProperty("mobile_no")
//	// @Column
//	// public String mobile_no;
//	//
//	// @JsonProperty("created_on")
//	// @Column
//	// public String created_on;
//	// //
//	// @JsonProperty("updated_on")
//	// @Column
//	// public String updated_on;
//	//
//	// @JsonProperty("gender")
//	// @Column
//	// public String gender;
//	//
//	// @JsonProperty("dob")
//	// @Column
//	// public String dob;
//	//
//	// @JsonProperty("profile_pic_url")
//	// @Column
//	// public String profile_pic_url;
//	//
//	// public int getId() {
//	// return id;
//	// }
//	//
//	// public void setId(int id) {
//	// this.id = id;
//	// }
//	//
//	// public String getUname() {
//	// return uname;
//	// }
//	//
//	// public void setUname(String uname) {
//	// this.uname = uname;
//	// }
//	//
//	// public String getFull_name() {
//	// return full_name;
//	// }
//	//
//	// public void setFull_name(String full_name) {
//	// this.full_name = full_name;
//	// }
//	//
//	// public String getAddress() {
//	// return address;
//	// }
//	//
//	// public void setAddress(String address) {
//	// this.address = address;
//	// }
//	//
//	// public String getPwd() {
//	// return pwd;
//	// }
//	//
//	// public void setPwd(String pwd) {
//	// this.pwd = pwd;
//	// }
//	//
//	// public String getMobile_no() {
//	// return mobile_no;
//	// }
//	//
//	// public void setMobile_no(String mobile_no) {
//	// this.mobile_no = mobile_no;
//	// }
//	// //
//	// public String getCreated_on() {
//	// return created_on;
//	// }
//	//
//	// public void setCreated_on(String created_on) {
//	// this.created_on = created_on;
//	// }
//	// //
//	// public String getUpdated_on() {
//	// return updated_on;
//	// }
//	//
//	// public void setUpdated_on(String updated_on) {
//	// this.updated_on = updated_on;
//	// }
//	//
//	// public String getGender() {
//	// return gender;
//	// }
//	//
//	// public void setGender(String gender) {
//	// this.gender = gender;
//	// }
//	//
//	// public String getDob() {
//	// return dob;
//	// }
//	//
//	// public void setDob(String dob) {
//	// this.dob = dob;
//	// }
//	//
//	// public String getProfile_pic_url() {
//	// return profile_pic_url;
//	// }
//	//
//	// public void setProfile_pic_url(String profile_pic_url) {
//	// this.profile_pic_url = profile_pic_url;
//	// }
//
//}
