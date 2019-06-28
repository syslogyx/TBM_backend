package com.vyako.smarttbm.constants;

public interface CredentialConstants {

	/**
	 * NOTE: if you are going to use gmail Id then TURN ON
	 * "Access for less secure apps" from below link
	 * "https://www.google.com/settings/security/lesssecureapps"
	 * 
	 * First login to your gmail using below credentials then click above link
	 **/
	// email configuration of sender
//	public static String EMAIL_HOST = "smtp.gmail.com";
//	public static int EMAIL_PORT = 587;
//	public static String ADMIN_EMAIL_ID = "smarttbm.vyako@gmail.com";
//	public static String ADMIN_EMAIL_PWD = "k@vudmnt";

	/**
	 * Email Subject and body
	 */
	public static String EMAIL_SUBJECT_FOR_ALERT = "[SmartTBM] Warning from #machineName#";
	public static String EMAIL_SUBJECT_FOR_MACHINE_OFF = "[SmartTBM] #machineName# Turned Off";

	public static String EMAIL_BODY_FOR_ALERT = "Hi, </br>This is to inform you that one of the part in the Machine (#machineName#) "
			+ "is about to expire.</br>Details of part is listed below: </br></br><u>Machine Name:</u>  <b>#machineName#</b> "
			+ "</br><u>Part Name:</u> <b>#partName#</b> </br> <u>Remaining Life:</u> <b>#lifePercentage#</b> "
			+ "</br></br>Please extend the life of the part if applicable or Replace the part."
			+ " </br></br>From,</br><b>SmartTBM Admin</b>";

	public static String EMAIL_BODY_FOR_MACHINE_OFF = "Hi, </br>This is to inform you that one of the part in the Machine (#machineName#) "
			+ " is over and it is turned off.</br>Details of part is listed below: </br></br><u>Machine Name:</u>  <b>#machineName#</b> "
			+ "</br><u>Part Name:</u> <b>#partName#</b> </br> <u>Possible Life Extension:</u> <b>#possibleExtension#</b> "
			+ "</br></br>Please extend the life of the part if applicable or Replace the part to start the Machine."
			+ " </br></br>From,</br><b>SmartTBM Admin</b>";

}
