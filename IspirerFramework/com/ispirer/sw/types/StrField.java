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

import com.ispirer.sw.strings.AlphanumericFormat;

public class StrField extends StructureModel {
	public PictureType<String> value;

	/**
	 * constructor of StrField
	 * 
	 * @param format format of variable
	 * @param objs   redefine object. It doesn't have to be specified.
	 */
	public StrField(String format, StructureModel... objs) {
		value = new PictureType<>(PictureType.Type.String, new AlphanumericFormat(format));
		initRedefineObjs(objs);
	}

	/**
	 * constructor of StrField for COMP variables
	 * 
	 * @param format format of variable
	 * @param str    value to initialize this object
	 * @param objs   redefine object. It doesn't have to be specified.
	 */
	public StrField(String format, String str, StructureModel... objs) {
		this(format);
		this.value = new PictureType<>(new AlphanumericFormat(format), str);
		initRedefineObjs(objs);
	}

	public StrField(String format, PictureType.DefaultValue str, StructureModel... objs) {
		this(format);
		this.value = new PictureType<>(PictureType.Type.String, new AlphanumericFormat(format), str);
		initRedefineObjs(objs);
	}

	@Override
	public String toString() {
		return "" + value;
	}

	/**
	 * set value to this object
	 * 
	 * @param data data to set
	 */
	@Override
	public void setData(char[] data) {
		value.setValue(String.valueOf(data));
		redefine();
	}

	/**
	 * set value to this object
	 * 
	 * @param data data to set
	 */
	public void setData(Object data) {
		value.setValue(data);
		redefine();
	}

	/**
	 * set value to this object
	 * 
	 * @param data data to set
	 * @param type type of column in DB
	 */
	public void setData(String data, int type) {
		value.setValue(data, type);
		redefine();

	}

	/**
	 * set value to this object
	 * 
	 * @param var data to set
	 */
	public void setData(PictureType<?> var) {
		setValue(var);
		redefine();
	}

	/**
	 * set value to this object
	 * 
	 * @param data data to set
	 */
	@Override
	public void setData(String[] data) {
		value.setValue(data[0]);
		redefine();
	}

	@Override
	public int getSize() {
		return value.getSize();
	}

	@Override
	public void initialize() {
		value.initialize();
	}

	@Override
	public byte[] toFile() {
		return value.toFile();
	}

	@Override
	public void setDataFromFile(byte[] bytes) {
		value.setDataFromFile(bytes);
	}

	@Override
	public void setDefaultValue(PictureType.DefaultValue value) {
		this.value.setDefaultValue(value);
		redefineDef(value);
	}

	public String getValue() {
		return this.value.getValue();
	}

	public void setValue(Object value) {
		this.value.setValue(value);
	}

	public void setValue(String data, int type) {
		this.value.setValue(data, type);
	}

	public String getTrimmedValue() {
		return this.value.getTrimmedValue();
	}

}
