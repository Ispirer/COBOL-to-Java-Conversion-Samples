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

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public abstract class Format {
	protected String format;
	protected String pattern;
	protected int size;

	/**
	 * creates Format object parses format and calculates size
	 * 
	 * @param format in the COBOL syntax rules
	 */
	Format(String format) {
		this.format = format.toLowerCase();
		pattern = format.toLowerCase();
		parseFormat();
		calcSize();
	}

	/**
	 * parses format change cases like x(3) to xxx also indicates sign
	 */
	private void parseFormat() {
		String p = "(?i)(.*?)([azx9+-]){1}(\\((.+?)\\))";
		while (Pattern.compile(p).matcher(pattern).find()) {
			String number = pattern.replaceAll(p + ".*", "$4");
			String type = pattern.replaceAll(p + ".*", "$2");
			type = type.equals("+") || type.equals("-") ? "z" : type;
			String n = StringUtils.repeat(type, Integer.valueOf(number));
			pattern = pattern.replaceFirst(p, "$1" + n);
		}
		if (pattern.charAt(pattern.length() - 1) == '-' || pattern.charAt(pattern.length() - 1) == '+') {
			pattern = pattern.replaceAll("[-+]", "");
		} else {
			pattern = pattern.replaceAll("[-+]", "z");
		}
	}

	/**
	 * calculate size by format
	 */
	private void calcSize() {
		size = pattern.length() - StringUtils.countMatches(pattern, "s") - StringUtils.countMatches(pattern, "v")
				+ (format.charAt(format.length() - 1) == '+' || format.charAt(format.length() - 1) == '-' ? 1 : 0);
	}

	public abstract String toStringWithFormat(Object value);

	public abstract byte[] toFileString(Object value);

	public int getSize() {
		return size;
	}

	public int getLength() {
		return size;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
}
