package com.vyako.smarttbm.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.internal.util.IgnoreJava6Requirement;

@Entity
@Table(name = "machine")
public class Machine {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	public int id;

	@Column(name = "machine_name")
	public String machine_name;

	@Column(name = "machine_code")
	public String machine_code;

	@Column(name = "current_count")
	public long current_count;

	@Column(name = "department")
	public String department;

	@Column(name = "dcd_linked_ip")
	public String dcd_linked_ip;

	@Column(name = "status")
	public String status;

	@Column(name = "alert_gen_perc")
	public int alert_gen_perc;

	@Column(name = "life_extesn_perc")
	public int life_extesn_perc;
	
	@Column(name = "handshake_status")
	public int handshake_status;

	@Column(name = "alert_gen_perc_of_extens_life")
	public int alert_gen_perc_of_extens_life;

	@Transient
	public String[] emails = null;

	// need to ignore this field for hiberent autolinking
	@Transient
	public List<MachinePart> parts = new ArrayList<>();
	
	@Transient
	public List<MachinePartCopy> partsCopy = new ArrayList<>();

	@Transient
	public List<MachinePartHistory> part_history = new ArrayList<>();

	@Transient
	public String alert_details = null;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMachine_name() {
		return machine_name;
	}

	public void setMachine_name(String machine_name) {
		this.machine_name = machine_name;
	}

	public String getMachine_code() {
		return machine_code;
	}

	public void setMachine_code(String machine_code) {
		this.machine_code = machine_code;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDcd_linked_ip() {
		return dcd_linked_ip;
	}

	public void setDcd_linked_ip(String dcd_linked_ip) {
		this.dcd_linked_ip = dcd_linked_ip;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getCurrent_count() {
		return current_count;
	}

	public void setCurrent_count(long current_count) {
		this.current_count = current_count;
	}

	public int getAlert_gen_perc() {
		return alert_gen_perc;
	}

	public void setAlert_gen_perc(int alert_gen_perc) {
		this.alert_gen_perc = alert_gen_perc;
	}

	public int getLife_extesn_perc() {
		return life_extesn_perc;
	}

	public void setLife_extesn_perc(int life_extesn_perc) {
		this.life_extesn_perc = life_extesn_perc;
	}

	public int getAlert_gen_perc_of_extens_life() {
		return alert_gen_perc_of_extens_life;
	}

	public void setAlert_gen_perc_of_extens_life(int alert_gen_perc_of_extens_life) {
		this.alert_gen_perc_of_extens_life = alert_gen_perc_of_extens_life;
	}

	public List<MachinePart> getParts() {
		return parts;
	}

	public void setParts(List<MachinePart> parts) {
		this.parts = parts;
	}
	
	public List<MachinePartCopy> getPartsCopy() {
		return partsCopy;
	}

	public void setPartsCopy(List<MachinePartCopy> partsCopy) {
		this.partsCopy = partsCopy;
	}

	public String[] getEmails() {
		return emails;
	}

	public void setEmails(String[] emails) {
		this.emails = emails;
	}

	public List<MachinePartHistory> getPart_history() {
		return part_history;
	}

	public void setPart_history(List<MachinePartHistory> part_history) {
		this.part_history = part_history;
	}

	public String getAlert_details() {
		return alert_details;
	}

	public void setAlert_details(String alert_details) {
		this.alert_details = alert_details;
	}

	public int getHandshake_status() {
		return handshake_status;
	}

	public void setHandshake_status(int handshake_status) {
		this.handshake_status = handshake_status;
	}
	
	 

}
