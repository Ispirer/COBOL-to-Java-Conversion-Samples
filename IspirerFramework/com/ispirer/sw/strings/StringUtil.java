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

import com.ispirer.sw.types.PictureType;
import com.ispirer.sw.types.StructureModel;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

public class StringUtil {

	/**
	 * replases searchString to replacement in the str first time
	 * 
	 * @param str
	 * @param searchString
	 * @param replacement
	 * @return result of replacing
	 */
	public static String replaceLeading(String str, String searchString, String replacement) {
		if (str.indexOf(searchString) == 0) {
			String newStr = "";
			while (str.indexOf(searchString) == 0) {
				newStr += str.substring(0, searchString.length());
				str = str.substring(searchString.length(), str.length());
			}
			newStr = newStr.replaceAll(searchString, replacement);
			return newStr + str;
		}
		return str;
	}

	public static Integer tallyingLeading(String str, String searchString) {
		Integer res = 0;
		while (str.indexOf(searchString) == 0) {
			res++;
			str = str.substring(searchString.length());
		}
		return res;
	}

	/**
	 * fills List variable by String value
	 * 
	 * @param array to fill
	 * @param str   to fill by
	 * @return filled list
	 */
	public static List<?> initializeArray(List<?> array, String str) {
		Object firstel = array.get(0);
		int sizeOneVariable = 0;
		if (firstel instanceof StructureModel) {
			sizeOneVariable = ((StructureModel) firstel).getSize();
		} else if (firstel instanceof PictureType) {
			sizeOneVariable = ((PictureType<?>) firstel).getSize();
		}

		if (str.length() < sizeOneVariable * array.size()) {
			str = str + StringUtils.repeat(" ", sizeOneVariable * array.size() - str.length());
		}
		for (int i = 0; i < array.size(); i++) {
			if (firstel instanceof StructureModel) {
				((StructureModel) array.get(i))
						.setData(str.substring(i * sizeOneVariable, (i + 1) * sizeOneVariable).toCharArray());
			} else if (firstel instanceof PictureType) {
				((PictureType<?>) array.get(i)).setValue(str.substring(i * sizeOneVariable, (i + 1) * sizeOneVariable));
			}
		}
		return array;
	}

	public static List<Object> initializeArray(List<Object> array, String str, int size) {
		Object firstel = array.get(0);
		if (str.length() < size * array.size()) {
			str = str + StringUtils.repeat(" ", size * array.size() - str.length());
		}
		for (int i = 0; i < array.size(); i++) {
			if (firstel instanceof String) {
				array.set(i, str.substring(i * size, (i + 1) * size));
			} else if (firstel instanceof Integer) {
				array.set(i, Integer.parseInt(str.substring(i * size, (i + 1) * size)));
			} else if (firstel instanceof BigDecimal) {
				array.set(i, new BigDecimal(str.substring(i * size, (i + 1) * size)));
			} else if (firstel instanceof Long) {
				array.set(i, Long.parseLong(str.substring(i * size, (i + 1) * size)));
			}
		}
		return array;
	}

	/**
	 * fills array by DefaultValue
	 * 
	 * @param array to fill
	 * @param str   DefaultValue object
	 * @return
	 */
	public static List<?> initializeArray(List<?> array, PictureType.DefaultValue str) {
		Object firstel = array.get(0);
		for (int i = 0; i < array.size(); i++) {
			if (firstel instanceof StructureModel) {
				((StructureModel) array.get(i)).setDefaultValue(str);
			} else if (firstel instanceof PictureType) {
				((PictureType<?>) array.get(i)).setValue(str);
			}
		}
		return array;
	}

	/**
	 * splits identifier by delimiter
	 * 
	 * @param identifier
	 * @param delimiter
	 * @return result of splitting
	 */
	public static String[] delimitedBy(Object identifier, Object delimiter) {
		String idenvar = identifier.toString();
		String delimvar = delimiter.toString();
		if (delimvar.equals("$")) {
			delimvar = "\\" + delimvar;
		}
		return idenvar.split(delimvar).length > 0 ? idenvar.split(delimvar) : new String[] { "" };
	}

	/**
	 * creates String from List
	 * 
	 * @param models List that need to convert to String
	 * @return result of conversion
	 */
	public static PictureType<?> arrayToString(List<?> models) {
		String result = "";
		for (Object model : models) {
			result = result.concat(model.toString());
		}
		return new PictureType<>(new AlphanumericFormat("x(" + result.length() + ")"), result);
	}
}
