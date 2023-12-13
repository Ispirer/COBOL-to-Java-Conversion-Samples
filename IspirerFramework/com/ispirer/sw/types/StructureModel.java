/*
	© 2021, Ispirer Systems OÜ. All rights reserved.
	NOTICE OF LICENSE
	This file\library is Ispirer Reusable Code (“IRC”) and you are granted a non-exclusive, worldwide, perpetual, irrevocable and fully paid up license to use, modify, adapt, sublicense and otherwise exploit this IRC as provided below and under the terms of Ispirer Systems OÜ. Reusable Code License Agreement (“License”), which can be found in supplementary LICENSE.txt file. By using this IRC, you acknowledge that you have read the License and agree with its terms as well as with the fact that IRC is the property of and belongs to Ispirer Systems OÜ only.
	IF YOU ARE NOT AGREE WITH THE TERMS OF THE LICENSE, PLEASE, STOP USING THIS IRC IMMEDIATELY! 
	PLEASE, NOTE, THAT IRC IS DISTRIBUTED “AS IS” AND WITHOUT ANY WARRANTY. IN NO EVENT WILL ISPIRER BE LIABLE FOR ANY DAMAGES, CLAIMS OR COSTS WHATSOEVER OR ANY CONSEQUENTIAL, INDIRECT, INCIDENTAL DAMAGES, OR ANY LOST PROFITS OR LOST SAVINGS. 
	Redistributions of this IRC must retain the above copyright notice and a list of significant changes made to this IRC with indication of its author(s) and date of changes.
	If you need more information, or you think that the License has been violated, please let us know by e-mail: legal.department@ispirer.com  
*/
package com.ispirer.sw.types;

import org.apache.commons.lang3.StringUtils;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ispirer.sw.types.PictureType.DefaultValue.*;

public abstract class StructureModel {

	protected List<StructureModel> redefobjs = new ArrayList<>();
	protected List<StructureModel> prevReds = new ArrayList<>();
	protected Boolean isRed = false;

	protected void initRedefineObjs(StructureModel... objs) {
		if (objs.length > 0) {
			redefobjs.addAll(Arrays.asList(objs));
			if (objs[0] != null) {
				objs[0].setPrevRed(this);
				isRed = true;
				setData(objs[0].toString().toCharArray());
				isRed = false;
			}
		}
	}

	private void setPrevRed(StructureModel prevRed) {
		this.prevReds.add(prevRed);
	}

	protected String getArrayString(List<?> models) {
		String result = "";
		for (Object model : models) {
			result = result.concat(model.toString());
		}
		return result;
	}

	protected String getArrayString(List<?> models, Integer index) {
		String result = "";
		for (int i = 0; i < index; i++) {
			result = result.concat(models.get(i).toString());
		}
		return result;
	}

	protected String repeat(String par, int size) {
		return StringUtils.repeat(par, size);
	}

	/**
	 * creates array of strings from array of chars. it collect chars to strings
	 * using length in size array
	 *
	 * @param data  char array to collect into strings
	 * @param sizes sizes of resulting strings
	 * @return
	 */
	public String[] getStringValues(char[] data, int[] sizes) {
		String[] result = new String[sizes.length];
		for (int i = 0; i < sizes.length; i++) {
			StringBuilder tmp = new StringBuilder();
			for (int j = 0; j < ((data.length < sizes[i]) ? data.length : sizes[i]) && data.length > 0; j++) {
				tmp.append(data[j]);
			}
			data = new String(data, ((data.length < sizes[i]) ? data.length : sizes[i]),
					data.length - ((data.length < sizes[i]) ? data.length : sizes[i])).toCharArray();
			result[i] = sizes[i] > tmp.length() ? tmp.toString() + StringUtils.repeat(" ", sizes[i] - tmp.length())
					: tmp.toString();
		}
		return result;
	}

	/**
	 * creates correct array of bytes Cuts useless or add usefull bytes
	 *
	 * @param data byte array to correct
	 * @return corrected byte array
	 */
	public byte[] getFullArray(byte[] data) {
		byte[] result = new byte[getSize()];
		for (int i = 0; i < result.length; i++) {
			result[i] = data.length > i ? data[i] : 32;
		}
		return result;
	}

	/**
	 * creates array from string. splits string by size
	 *
	 * @param data
	 * @param size
	 * @return array of splitted strings
	 */
	public String[] getArrayValues(String data, int size) {
		String[] result = new String[size];
		int pieceLength = data.length() / size;
		int offset = 0;
		for (int i = 0; i < size; i++) {
			result[i] = data.substring(offset, pieceLength * (i + 1));
			offset += pieceLength;
		}
		return result;
	}

	protected void redefine() {
		// if (prevReds != null && !prevReds.getRed()) {
		if (prevReds.size() > 0) {
			for (StructureModel obj : prevReds) {
				if (!obj.getRed()) {
					redefine(obj);
				}
			}
		}
		if (redefobjs.size() > 0) {
			if (redefobjs.get(0) != null) {
				if (!redefobjs.get(0).getRed()) {
					redefine(redefobjs.get(0));
				}
			}
			for (int i = 1; i < redefobjs.size(); i++) {
				if (redefobjs.get(i) != null) {
					if (!redefobjs.get(i).getRed()) {
						redefobjs.get(i).redefine();
					}
				}
			}
		}
	}

	protected void redefineDef(PictureType.DefaultValue defaultValue) {
//        if (prevReds != null && !prevReds.getRed()) {
//            redefineDef(prevReds, defaultValue);
//            return;
//        }
		if (prevReds.size() > 0) {
			for (StructureModel obj : prevReds) {
				if (obj != null && !obj.getRed()) {
					redefineDef(obj, defaultValue);
				}
			}
		}
		for (StructureModel obj : redefobjs) {
			redefineDef(obj, defaultValue);
		}
	}

	void redefineDef(StructureModel obj, PictureType.DefaultValue defaultValue) {
		setRed(true);
		obj.setDefaultValue(defaultValue);
		setRed(false);
	}

	protected void redefine(StructureModel obj) {
		if (obj.getRed()) {
			return;
		}
		String data = "";
		if ((obj.toString().length() >= this.toString().toCharArray().length) && !(this instanceof IntField)) {
			data = this.toString() + obj.toString().substring(this.toString().toCharArray().length);
		} else {
			data = this.toString();
		}
		if (obj instanceof StrField
				&& (this instanceof IntField || this instanceof LongField || this instanceof BigDecimalField)) {
			setRed(true);
			if (this instanceof IntField) {
				obj.setData(((IntField) this).value);
			} else if (this instanceof LongField) {
				obj.setData(((LongField) this).value);
			} else {
				obj.setData(((BigDecimalField) this).value);

			}
			setRed(false);
		} else {
			setRed(true);
			obj.setData(data.toCharArray());
			setRed(false);

		}
	}

	/**
	 * set data from PT variable
	 *
	 * @param var PT variable
	 */
	public void setData(PictureType<?> var) {
		if (var.hasDefVal && (var.compareTo(Spaces) == 0 || var.compareTo(HighValues) == 0
				|| var.compareTo(LowValues) == 0 || var.compareTo(Zeroes) == 0)) {
			setDefaultValue(var.getDefValue());
			return;
		}
		setData(var.toString().toCharArray());
		redefine();
	}

	public abstract String toString();

	public abstract void setData(char[] data);

	public abstract void setData(String[] data);

	public abstract int getSize();

	public abstract void initialize();

	public abstract byte[] toFile();

	public abstract void setDataFromFile(byte[] bytes);

	public abstract void setDefaultValue(PictureType.DefaultValue value);

	// next methods implements compareTo licics with different types
	/**
	 * implements compareTo logic with DefaultValue
	 *
	 * @param value DefaultValue to compare
	 * @return result of comparison
	 */
	public int compareTo(PictureType.DefaultValue value) {
		switch (value) {
		case Spaces:
		case HighValues:
			return toString().replaceAll("[+-.,/ ]", "").equals("") ? 0 : 1;
		case Zeroes:
			try {
				return new BigInteger(toString().replaceAll("[ +-.,]", "").trim()).compareTo(new BigInteger("0"));
			} catch (NumberFormatException e) {
				return -1;
			}

		case LowValues:
			return toString().equals("") ? 0 : 1;
		case Quotes:
			return toString().compareTo(StringUtils.repeat("\"", getSize()));
		}
		return 0;
	}

	/**
	 * implements compareTo logic with PT
	 *
	 * @param value PT variable
	 * @return result of comparison
	 */
	public int compareTo(PictureType<?> value) {
		return value.compareTo(this) * -1;
	}

	/**
	 * implements compareTo logic with other structure
	 *
	 * @param value structure variable
	 * @return result of comparison
	 */
	public int compareTo(StructureModel value) {
		return compareTo(value.toString());
	}

	/**
	 * implements compareTo logic with int variable
	 *
	 * @param value int variable
	 * @return result of comparison
	 */
	public int compareTo(int value) {
		int res = toString().trim().compareTo(String.valueOf(value));
		if (res > 0) {
			return 1;
		} else if (res < 0) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * implements compareTo logic with String variable
	 *
	 * @param value String variable
	 * @return result of comparison
	 */
	public int compareTo(String value) {
		int res = toString().trim().compareTo(value);
		if (res > 0) {
			return 1;
		} else if (res < 0) {
			return -1;
		} else {
			return 0;
		}
	}

	protected StructureModel[] extendArray(StructureModel[] array, StructureModel... addElements) {
		ArrayList<StructureModel> redList;
		redList = new ArrayList<>(Arrays.asList(array));
		if (!redList.isEmpty()) {
			redList.remove(0);
		}
		for (StructureModel element : addElements) {
			redList.add(0, element);
		}
		return redList.toArray(array);
	}

	private Boolean getRed() {
		return isRed;
	}

	private void setRed(Boolean red) {
		isRed = red;
	}
}
