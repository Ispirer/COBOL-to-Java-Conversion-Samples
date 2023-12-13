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
import javax.xml.bind.DatatypeConverter;
import java.math.BigDecimal;

public class DecimalFormat extends Format {
	private char sign = ' ';
	private char addSign = ' ';
	private int decimalSize = 0;
	private CompType compType;

	public static final boolean EBCDIC = true;

	public DecimalFormat(String format) {
		super(format);
		setSign(); // indicates signed type
		setDecimalSize(); // calculates decimal size
	}

	public DecimalFormat(String format, CompType compType) {
		this(format);
		this.compType = compType;
	}

	/**
	 * creates string representation of value using format
	 * 
	 * @param value to represent
	 * @return string representation of value
	 */
	@Override
	public String toStringWithFormat(Object value) {
		StringBuilder res = new StringBuilder();
		String val = value.toString();
		while (val.length() > 1 && val.charAt(0) == '0') {
			val = val.substring(1);
		}
		char[] valueArr = val.toString().replaceAll("-", "").replaceAll("\\.", "").toCharArray();
		int j = valueArr.length - 1;
		boolean hasDol = false;
		for (int i = pattern.length() - 1; i >= 0; i--) {
			if (pattern.charAt(i) == 'z' || pattern.charAt(i) == '9' || pattern.charAt(i) == '$') {
				// fils while numbers are
				if (j >= 0) {
					res.insert(0, valueArr[j--]);
				} else {
					if (isSigned() && sign != 's' && pattern.charAt(i) != '9' && j == -1) {
						if (new BigDecimal(value.toString()).compareTo(new BigDecimal(0d)) >= 0) {
							res.insert(0, sign == '+' ? '+' : ' ');
						} else {
							res.insert(0, '-');
						}
						continue;
					}
					if (pattern.charAt(i) == '$' && j-- == -1 && !hasDol) {
						res.insert(0, pattern.charAt(i));
						continue;
					}
					res.insert(0, pattern.charAt(i) == '9' ? "0" : " ");
				}
				// fills when numbers are finished
			} else {
				if (j < 0 && pattern.charAt(i) != 'v' && pattern.charAt(i) != '.' && pattern.charAt(i) != ',') {
					res.insert(0, " ");
					continue;
				} else if (j < 0 && pattern.charAt(i) == ',' && pattern.charAt(i - 1) == '$') {
					res.insert(0, j == -1 ? "$" : " ");
					hasDol = true;
					continue;
				} else if (j < 0 && pattern.charAt(i) == ',' && pattern.charAt(i - 1) == 'z') {
					res.insert(0, " ");
					continue;
				}
				res.insert(0, pattern.charAt(i) == 'v' ? "" : pattern.charAt(i));
			}
		}
		// adds correct sign
		if (isSigned() && sign == 's') {
			if (compType != null) {
				res.insert(0, new BigDecimal(value.toString()).compareTo(new BigDecimal(0d)) >= 0 ? "+" : "-");
			} else {
				if (addSign == ' ') {
					res.append(new BigDecimal(value.toString()).compareTo(new BigDecimal(0d)) >= 0 ? "+" : "-");
				}
				if (addSign == '-') {
					res.append(new BigDecimal(value.toString()).compareTo(new BigDecimal(0d)) >= 0 ? " " : "-");
				}
				if (addSign == '+') {
					res.append(new BigDecimal(value.toString()).compareTo(new BigDecimal(0d)) >= 0 ? "+" : "-");
				}
			}
		}
		if (j >= 0) {
			if (isSigned() && sign == '-') {
				res.replace(0, 1, new BigDecimal(value.toString()).compareTo(new BigDecimal(0d)) >= 0 ? " " : "-");
			}
			if (isSigned() && sign == '+') {
				res.replace(0, 1, new BigDecimal(value.toString()).compareTo(new BigDecimal(0d)) >= 0 ? "+" : "-");
			}
		}
		res = checkForZeroSus(res);
		return res.toString();
	}

	private StringBuilder checkForZeroSus(StringBuilder str) {
		if (str.toString().matches("^[0 $.,]+$") && pattern.matches("^[z$.,]+$")) {
			return new StringBuilder(StringUtils.repeat(" ", str.length()));
		}
		for (int i = 0; i < pattern.length(); i++) {
			if ((pattern.charAt(i) == 'z' || pattern.charAt(i) == ',') && (str.charAt(i) == '0')) {
				str.setCharAt(i, ' ');
			} else {
				return str;
			}
		}
		return str;
	}

	/**
	 * creates byte representation of value using format
	 * 
	 * @param value to represent
	 * @return byte representation of value
	 */
	@Override
	public byte[] toFileString(Object value) {
		if (compType == CompType.Comp) {
			return getCompString(value); // specific method for COMP variables
		}
		if (compType == CompType.Comp3) {
			return getComp3String(value); // specific method for COMP3 variables
		}
		StringBuilder res = new StringBuilder(toStringWithFormat(value));
		// if signed need to change last character
		if (sign == 's') {
			res = new StringBuilder(toStringWithFormat(value).replaceAll("-", "").replaceAll("\\+", ""));
			if (res.substring(res.length() - 1).equals(" "))
				res = res.delete(res.length() - 1, res.length());
			if (isSigned()) {
				res = res.replace(res.length() - 1, res.length(),
						"" + getNegativeChar(res.charAt(res.length() - 1), value.toString().charAt(0) == '-'));
			}
		}

		return res.toString().getBytes();
	}

	private byte[] getCompString(Object value) {
		int size = getCompSize();
		boolean isnegative = value.toString().contains("-");
		Long longVal = Long.valueOf(value.toString().replaceAll("\\.", ""));
		String hexString = Long.toHexString(longVal);
		if (isnegative && hexString.length() > size * 2) {
			hexString = hexString.substring(hexString.length() - size * 2);
		}
		hexString = StringUtils.repeat("0", size * 2 - hexString.length()) + hexString;
		return DatatypeConverter.parseHexBinary(hexString);
	}

	private byte[] getComp3String(Object value) {
		long size = Math.round((double) (getSize() + getDecimalSize()) / 2 + 0.5);
		boolean isnegative = value.toString().contains("-");
		Long longVal = Math.abs(Long.valueOf(value.toString().replaceAll("[-+.]", "")));
		String hexString = "" + longVal + (isSigned() ? (isnegative ? "D" : "C") : "F");
		if ((double) hexString.length() % 2 > 0) {
			hexString = "0" + hexString;
		}
		hexString = StringUtils.repeat("0", (int) size * 2 - hexString.length()) + hexString;
		return DatatypeConverter.parseHexBinary(hexString);
	}

	/**
	 * Calculate size of COMP variable
	 * 
	 * @return size of COMP variable
	 */
	public int getCompSize() {
		switch (getSize() + getDecimalSize()) {
		case 1:
		case 2:
			return 1;
		case 3:
		case 4:
			return 2;
		case 5:
		case 6:
			return 3;
		case 7:
			return isSigned() ? 4 : 3;
		case 8:
		case 9:
			return 4;
		case 10:
		case 11:
			return 5;
		case 12:
			return isSigned() ? 6 : 5;
		case 13:
		case 14:
			return 6;
		case 15:
		case 16:
			return 7;
		case 17:
		case 18:
			return 8;
		}
		return 0;
	}

	/**
	 * change number to character to save sign
	 * 
	 * @param ch number character
	 * @param bl is negative
	 * @return
	 */
	private char getNegativeChar(char ch, boolean bl) {
		if (EBCDIC) {
			// rule for EBCDIC
			if (bl) {
				switch (ch) {
				case '1':
					return 'J';
				case '2':
					return 'K';
				case '3':
					return 'L';
				case '4':
					return 'M';
				case '5':
					return 'N';
				case '6':
					return 'O';
				case '7':
					return 'P';
				case '8':
					return 'Q';
				case '9':
					return 'R';
				case '0':
					return '}';
				}
			} else {
				switch (ch) {
				case '1':
					return 'A';
				case '2':
					return 'B';
				case '3':
					return 'C';
				case '4':
					return 'D';
				case '5':
					return 'E';
				case '6':
					return 'F';
				case '7':
					return 'G';
				case '8':
					return 'H';
				case '9':
					return 'I';
				case '0':
					return '{';
				}
			}
			// Rules for ASCII
		} else {
			switch (ch) {
			case '1':
				return 'q';
			case '2':
				return 'r';
			case '3':
				return 's';
			case '4':
				return 't';
			case '5':
				return 'u';
			case '6':
				return 'v';
			case '7':
				return 'w';
			case '8':
				return 'x';
			case '9':
				return 'y';
			case '0':
				return 'p';
			}
		}
		return ch;
	}

	/**
	 * convert character to number with -
	 * 
	 * @param ch character to convert
	 * @return number character
	 */
	public char getNegativeInt(char ch) {
		switch (ch) {
		case 'q':
		case 'A':
		case 'J':
			return '1';
		case 'r':
		case 'B':
		case 'K':
			return '2';
		case 's':
		case 'C':
		case 'L':
			return '3';
		case 't':
		case 'D':
		case 'M':
			return '4';
		case 'u':
		case 'E':
		case 'N':
			return '5';
		case 'v':
		case 'F':
		case 'O':
			return '6';
		case 'w':
		case 'G':
		case 'P':
			return '7';
		case 'x':
		case 'H':
		case 'Q':
			return '8';
		case 'y':
		case 'I':
		case 'R':
			return '9';
		case 'p':
		case '{':
		case '}':
			return '0';
		}
		return ch;
	}

	private void setSign() {
		sign = format.charAt(0);
		addSign = format.charAt(format.length() - 1);
		if (addSign == '-' || addSign == '+') {
			sign = 's';
		} else {
			addSign = ' ';
		}
		pattern = pattern.replace("s", "").replace("-", "").replace("s", "+");
	}

	private void setDecimalSize() {
		if (pattern.split("[v.]").length > 1) {
			decimalSize = pattern.split("[v.]")[1].length();
		}
	}

	public boolean isSigned() {
		return sign == '+' || sign == '-' || sign == 's';
	}

	public int getDecimalSize() {
		return decimalSize;
	}

	public int getSize() {
		return super.getSize() - getDecimalSize() - (getDecimalSize() != 0 && format.contains(".") ? 1 : 0)
				- (isSigned() && sign != 's' ? 1 : 0) - (isSigned() && sign == 's' && addSign != ' ' ? 1 : 0)
				- (pattern.contains("$") ? 1 : 0) - StringUtils.countMatches(pattern, ",");
	}

	/**
	 * This enum declares COMP types that are implemented in the framework
	 */
	public enum CompType {
		Comp, Comp3,
	}

	public CompType getCompType() {
		return compType;
	}

	public char getSign() {
		if (addSign != ' ') {
			return addSign;
		}
		return sign;
	}
}
