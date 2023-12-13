/*
	© 2021, Ispirer Systems OÜ. All rights reserved.
	NOTICE OF LICENSE
	This file\library is Ispirer Reusable Code (“IRC”) and you are granted a non-exclusive, worldwide, perpetual, irrevocable and fully paid up license to use, modify, adapt, sublicense and otherwise exploit this IRC as provided below and under the terms of Ispirer Systems OÜ. Reusable Code License Agreement (“License”), which can be found in supplementary LICENSE.txt file. By using this IRC, you acknowledge that you have read the License and agree with its terms as well as with the fact that IRC is the property of and belongs to Ispirer Systems OÜ only.
	IF YOU ARE NOT AGREE WITH THE TERMS OF THE LICENSE, PLEASE, STOP USING THIS IRC IMMEDIATELY! 
	PLEASE, NOTE, THAT IRC IS DISTRIBUTED “AS IS” AND WITHOUT ANY WARRANTY. IN NO EVENT WILL ISPIRER BE LIABLE FOR ANY DAMAGES, CLAIMS OR COSTS WHATSOEVER OR ANY CONSEQUENTIAL, INDIRECT, INCIDENTAL DAMAGES, OR ANY LOST PROFITS OR LOST SAVINGS. 
	Redistributions of this IRC must retain the above copyright notice and a list of significant changes made to this IRC with indication of its author(s) and date of changes.
	If you need more information, or you think that the License has been violated, please let us know by e-mail: legal.department@ispirer.com  
*/
package com.ispirer.sw.strings;

/**
 * This format is used for string PT variables
 */
public class AlphanumericFormat extends Format {

	public AlphanumericFormat(String format) {
		super(format);
		pattern = pattern.replaceAll("b", " ");
	}

	/**
	 * Creates string representation of value using format
	 * 
	 * @param value to represent
	 * @return string representation of value
	 */
	@Override
	public String toStringWithFormat(Object value) {
		StringBuilder res = new StringBuilder();
		char[] valueArr = value.toString().toCharArray();
		int j = 0;
		int i = 0;
		while (j < pattern.length()) {
			if (pattern.charAt(i) == 'x' || pattern.charAt(i) == 'a' || pattern.charAt(i) == '9') {
				int byteOfSymbol = 0;
				if (i != valueArr.length) {
					byteOfSymbol = String.valueOf(valueArr[i]).getBytes().length;
					res.append(valueArr[i++]);
				} else {
					byteOfSymbol = 1;
					res.append(" ");
				}
				j += byteOfSymbol;
			} else {
				res.append(pattern.charAt(i));
			}
		}
		/*
		 * for (int i = 0; i< pattern.length(); i++){ if (pattern.charAt(i) == 'x' ||
		 * pattern.charAt(i) == 'a' || pattern.charAt(i) == '9'){ if (j!=
		 * valueArr.length){ res.append(valueArr[j++]); } else{ res.append(" "); }
		 * }else{ res.append(pattern.charAt(i)); } }
		 */
		return res.toString();
	}

	/**
	 * Creates byte representation of value using format
	 * 
	 * @param value to represent
	 * @return byte representation of value
	 */
	@Override
	public byte[] toFileString(Object value) {
		return toStringWithFormat(value).getBytes();
	}
}
