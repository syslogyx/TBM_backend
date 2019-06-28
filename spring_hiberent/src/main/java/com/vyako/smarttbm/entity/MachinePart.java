package com.vyako.smarttbm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "machine_parts")
public class MachinePart {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	public int id;

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

	@Column(name = "part_replace_count")
	public long part_replace_count;

	@Column(name = "part_life_extend_count")
	public long part_life_extend_count;

	@Column(name = "life_extended")
	public boolean life_extended;

	@Column(name = "predicted_life")
	public long predicted_life;

	@Column(name = "temp_life")
	public long temp_life;

	@Column(name = "excl_part")
	public int excl_part;
	
	@Column(name = "p1")
	public int p1;
	
	@Column(name = "p2")
	public int p2;
	
	@Column(name = "p3")
	public int p3;
	
	@Column(name = "p4")
	public int p4;
	
	@Column(name = "p5")
	public int p5;
	
	@Column(name = "p6")
	public int p6;
	
	@Column(name = "r1")
	public int r1;
	
	@Column(name = "r2")
	public int r2;
	
	@Column(name = "r3")
	public int r3;
	
	@Column(name = "r4")
	public int r4;
	
	@Column(name = "r5")
	public int r5;
	
	@Column(name = "r6")
	public int r6;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public long getPart_replace_count() {
		return part_replace_count;
	}

	public void setPart_replace_count(long part_replace_count) {
		this.part_replace_count = part_replace_count;
	}

	public boolean isLife_extended() {
		return life_extended;
	}

	public void setLife_extended(boolean life_extended) {
		this.life_extended = life_extended;
	}

	public long getPart_life_extend_count() {
		return part_life_extend_count;
	}

	public void setPart_life_extend_count(long part_life_extend_count) {
		this.part_life_extend_count = part_life_extend_count;
	}

	public long getPredicted_life() {
		return predicted_life;
	}

	public void setPredicted_life(long predicted_life) {
		this.predicted_life = predicted_life;
	}

	public long getTemp_life() {
		return temp_life;
	}

	public void setTemp_life(long temp_life) {
		this.temp_life = temp_life;
	}

	public int getExcl_part() {
		return excl_part;
	}

	public void setExcl_part(int excl_part) {
		this.excl_part = excl_part;
	}

	public int getP1() {
		return p1;
	}

	public void setP1(int p1) {
		this.p1 = p1;
	}

	public int getP2() {
		return p2;
	}

	public void setP2(int p2) {
		this.p2 = p2;
	}

	public int getP3() {
		return p3;
	}

	public void setP3(int p3) {
		this.p3 = p3;
	}

	public int getP4() {
		return p4;
	}

	public void setP4(int p4) {
		this.p4 = p4;
	}

	public int getP5() {
		return p5;
	}

	public void setP5(int p5) {
		this.p5 = p5;
	}

	public int getP6() {
		return p6;
	}

	public void setP6(int p6) {
		this.p6 = p6;
	}

	public int getR1() {
		return r1;
	}

	public void setR1(int r1) {
		this.r1 = r1;
	}

	public int getR2() {
		return r2;
	}

	public void setR2(int r2) {
		this.r2 = r2;
	}

	public int getR3() {
		return r3;
	}

	public void setR3(int r3) {
		this.r3 = r3;
	}

	public int getR4() {
		return r4;
	}

	public void setR4(int r4) {
		this.r4 = r4;
	}

	public int getR5() {
		return r5;
	}

	public void setR5(int r5) {
		this.r5 = r5;
	}

	public int getR6() {
		return r6;
	}

	public void setR6(int r6) {
		this.r6 = r6;
	}

}
