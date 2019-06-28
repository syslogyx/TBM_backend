package com.vyako.smarttbm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "machine_parts_history")
public class MachinePartHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	public int id;

	@Column(name = "part_id")
	public int part_id;

	@Column(name = "machine_id")
	public int machine_id;

	@Column(name = "spare_part_name")
	public String spare_part_name;

	@Column(name = "life")
	public long life;

	@Column(name = "multiplying_factor")
	public int multiplying_factor;

	@Column(name = "alert_gen_count")
	public long alert_gen_count;

	@Column(name = "provision_of_life_exten")
	public String provision_of_life_exten;

	@Column(name = "life_exhausted_till_date")
	public long life_exhausted_till_date;

	@Column(name = "life_exten_limit")
	public long life_exten_limit;

	@Column(name = "exten_life_alert_count")
	public long exten_life_alert_count;

	@Column(name = "final_life")
	public long final_life;

	@Column(name = "off_at_count")
	public long off_at_count;

	@Column(name = "alert_at_count")
	public long alert_at_count;

	@Column(name = "activity_details")
	public String activity_details;

	@Column(name = "added_on")
	public String added_on;

	@Column(name = "part_replace_count")
	public long part_replace_count;

	@Column(name = "part_life_extend_count")
	public long part_life_extend_count;

	@Column(name = "life_extended")
	public boolean life_extended;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPart_id() {
		return part_id;
	}

	public void setPart_id(int part_id) {
		this.part_id = part_id;
	}

	public int getMachine_id() {
		return machine_id;
	}

	public void setMachine_id(int machine_id) {
		this.machine_id = machine_id;
	}

	public String getSpare_part_name() {
		return spare_part_name;
	}

	public void setSpare_part_name(String spare_part_name) {
		this.spare_part_name = spare_part_name;
	}

	public long getLife() {
		return life;
	}

	public void setLife(long life) {
		this.life = life;
	}

	public int getMultiplying_factor() {
		return multiplying_factor;
	}

	public void setMultiplying_factor(int multiplying_factor) {
		this.multiplying_factor = multiplying_factor;
	}

	public long getAlert_gen_count() {
		return alert_gen_count;
	}

	public void setAlert_gen_count(long alert_gen_count) {
		this.alert_gen_count = alert_gen_count;
	}

	public String getProvision_of_life_exten() {
		return provision_of_life_exten;
	}

	public void setProvision_of_life_exten(String provision_of_life_exten) {
		this.provision_of_life_exten = provision_of_life_exten;
	}

	public long getLife_exhausted_till_date() {
		return life_exhausted_till_date;
	}

	public void setLife_exhausted_till_date(long life_exhausted_till_date) {
		this.life_exhausted_till_date = life_exhausted_till_date;
	}

	public long getLife_exten_limit() {
		return life_exten_limit;
	}

	public void setLife_exten_limit(long life_exten_limit) {
		this.life_exten_limit = life_exten_limit;
	}

	public long getExten_life_alert_count() {
		return exten_life_alert_count;
	}

	public void setExten_life_alert_count(long exten_life_alert_count) {
		this.exten_life_alert_count = exten_life_alert_count;
	}

	public long getFinal_life() {
		return final_life;
	}

	public void setFinal_life(long final_life) {
		this.final_life = final_life;
	}

	public long getOff_at_count() {
		return off_at_count;
	}

	public void setOff_at_count(long off_at_count) {
		this.off_at_count = off_at_count;
	}

	public long getAlert_at_count() {
		return alert_at_count;
	}

	public void setAlert_at_count(long alert_at_count) {
		this.alert_at_count = alert_at_count;
	}

	public String getActivity_details() {
		return activity_details;
	}

	public void setActivity_details(String activity_details) {
		this.activity_details = activity_details;
	}

	public String getAdded_on() {
		return added_on;
	}

	public void setAdded_on(String added_on) {
		this.added_on = added_on;
	}

	public long getPart_replace_count() {
		return part_replace_count;
	}

	public void setPart_replace_count(long part_replace_count) {
		this.part_replace_count = part_replace_count;
	}

	public long getPart_life_extend_count() {
		return part_life_extend_count;
	}

	public void setPart_life_extend_count(long part_life_extend_count) {
		this.part_life_extend_count = part_life_extend_count;
	}

	public boolean isLife_extended() {
		return life_extended;
	}

	public void setLife_extended(boolean life_extended) {
		this.life_extended = life_extended;
	}

}
