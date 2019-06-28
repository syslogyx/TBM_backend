package com.vyako.smarttbm.constants;

public interface Constants {
	// keys to provide response
	public String KEY_MACHINE_ID = "MACHINE_ID";
	public String KEY_MAIN_COUNT = "MAIN_COUNT";
	public String KEY_CURRENT_COUNT = "CURRENT_COUNT";
	public String KEY_ALERT_COUNT = "ALERT_COUNT";
	public String KEY_CMD = "CMD";
	public String KEY_UNIX_TIME = "TIME";
	public int VALUE_ZERO = 0;

	// dcd to machine
	public String KEY_MACHINE_STATUS = "MACHINE_STATUS";
	public String KEY_ALERT = "ALERT";
	public String KEY_OLD = "OLD";

	public String KEY_MACHINE_ON = "ON";
	public String KEY_MACHINE_OFF = "OFF";

	// string constants
	public String STRING_NEW_PART_INSTALLED = "New Part Installed";
	public String STRING_ALERT_GENERATED = "Alert Generated";
	public String STRING_LIFE_EXTENDED = "Life Extended";
	public String STRING_LIFE_OVER = "Life Over";
	
	// notification constants
	public int STATUS_ACTIVE = 1;
	public int STATUS_DEACTIVE = 0;
	public int STATUS_INPROGRESS = 2;

}
