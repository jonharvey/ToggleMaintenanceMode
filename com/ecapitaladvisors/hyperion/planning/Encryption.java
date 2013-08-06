 /***********************************************
 *  Author: Oracle, Inc.                        *
 *          LogOnUtils.js                       *
 *  Ported: Jon Harvey                          *
 *  		eCapital Advisors, LLC.				*
 *  		jharvey@ecapitaladvisors.com        *
 *  Date: 	04/06/10                            *
 ***********************************************/

package com.ecapitaladvisors.hyperion.planning;

public class Encryption {

	private static String StringToFourBit(String value, char baseByte)
	{
		if (value.equalsIgnoreCase(""))
			return "";
		String newValue = "";
		int loop1 = 0;
		for (loop1 = 0;loop1 < value.length(); loop1++){
			char b = (value.charAt(loop1));
			int hiBits = (((b & 0xF0)>> 4) + baseByte);
			int lowBits = ((b & 0x0F) + baseByte);
			newValue += (char)(hiBits);
			newValue += (char)(lowBits);
		}
		return newValue;
	}
	
	private static String ApplyXORKey(String value, String key)
	{
		if (value.equalsIgnoreCase(""))
			return "";
		String newValue = "";
		int loop1 = 0;
		for (loop1 = 0;loop1<value.length();loop1++)
			newValue += (char)(value.charAt(loop1) ^ key.charAt(loop1 % key.length()));
		return newValue;
	}
	
	public static String EncryptPassword(String password)
	{
		char baseByte = 65; //'A'
		String key = "ol94hQz2h5Nb4zuWj";
		password = (StringToFourBit(ApplyXORKey(password, key), baseByte));
		return password;
	}

}
