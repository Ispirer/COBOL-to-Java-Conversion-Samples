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

import com.ispirer.sw.strings.DecimalFormat;

import java.math.BigDecimal;

public class BigDecimalField extends StructureModel {

	public PictureType<BigDecimal> value;

	/**
	 * constructor of BigDecimalField
	 * 
	 * @param format format of variable
	 * @param objs   redefine object. It doesn't have to be specified.
	 */
	public BigDecimalField(String format, StructureModel... objs) {
		value = new PictureType<>(PictureType.Type.BigDecimal, new DecimalFormat(format));
		initRedefineObjs(objs);
	}

	/**
	 * constructor of BigDecimalField for COMP variables
	 * 
	 * @param format   format of variable
	 * @param compType specify compType
	 * @param objs     redefine object. It doesn't have to be specified.
	 */
	public BigDecimalField(String format, DecimalFormat.CompType compType, StructureModel... objs) {
		value = new PictureType<>(PictureType.Type.BigDecimal, new DecimalFormat(format, compType));
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
	@Override
	public void setData(String[] data) {
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
	 * set value to this object from PT var
	 * 
	 * @param var data to set
	 */
	public void setData(PictureType<?> var) {
		value.setValue(var);
		redefine();
	}

	/**
	 * set value to this object
	 * 
	 * @param data data to set
	 * @param obj  redefine objects
	 */
	public void setData(Object data, StructureModel obj) {
		value.setValue(data);
		redefine();
	}

	/**
	 * @return size og object
	 */
	@Override
	public int getSize() {
		return value.getSize();
	}

	/**
	 * initialize object
	 */
	@Override
	public void initialize() {
		this.value.initialize();
	}

	/**
	 * @return value in byte array to write it into file
	 */
	@Override
	public byte[] toFile() {
		return value.toFile();
	}

	/**
	 * set data from file into this object
	 * 
	 * @param bytes date in byte array to set
	 */
	@Override
	public void setDataFromFile(byte[] bytes) {
		value.setDataFromFile(bytes);
	}

	/**
	 * set defaultValue into this object
	 * 
	 * @param value to set
	 */
	@Override
	public void setDefaultValue(PictureType.DefaultValue value) {
		this.value.setDefaultValue(value);
		redefineDef(value);
	}

	public BigDecimal getValue() {
		return this.value.getValue();
	}

	public PictureType<BigDecimal> add(Object value) {
		return this.value.add(value);
	}

	public PictureType<BigDecimal> subtract(Object value) {
		return this.value.subtract(value);
	}

	public PictureType<BigDecimal> multiply(Object value) {
		return this.value.multiply(value);
	}

	public PictureType<BigDecimal> divide(Object value) {
		return this.value.divide(value);
	}

	public void setValue(Object value) {
		this.value.setValue(value);
		redefine();
	}
}
