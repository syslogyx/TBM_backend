package com.vyako.smarttbm.do_other;

import java.util.ArrayList;

public class NotificationCountDo {
	private int count = 0;
	private ArrayList<String> turned_off_machine_names = null;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public ArrayList<String> getTurned_off_machine_names() {
		return turned_off_machine_names;
	}

	public void setTurned_off_machine_names(
			ArrayList<String> turned_off_machine_names) {
		this.turned_off_machine_names = turned_off_machine_names;
	}
}
