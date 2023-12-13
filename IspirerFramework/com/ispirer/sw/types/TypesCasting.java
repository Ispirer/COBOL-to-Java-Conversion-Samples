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

import java.math.BigDecimal;

public abstract class TypesCasting {

	public static Integer toIntegerValue(String integerFormat, Object value) {
		PictureType<Integer> integerValue = new PictureType<>(PictureType.Type.Integer,
				new AlphanumericFormat(integerFormat));
		integerValue.setValue(value);
		return integerValue.getValue();
	}

	public static Long toLongValue(String longFormat, Object value) {
		PictureType<Long> longValue = new PictureType<>(PictureType.Type.Long, new AlphanumericFormat(longFormat));
		longValue.setValue(value);
		return longValue.getValue();
	}

	public static BigDecimal toBigDecimalValue(String bigDecimalFormat, Object value) {
		PictureType<BigDecimal> bigDecimalValue = new PictureType<>(PictureType.Type.BigDecimal,
				new AlphanumericFormat(bigDecimalFormat));
		bigDecimalValue.setValue(value);
		return bigDecimalValue.getValue();
	}

	public static String toStringValue(String stringFormat, Object value) {
		PictureType<String> stringValue = new PictureType<>(PictureType.Type.String,
				new AlphanumericFormat(stringFormat));
		stringValue.setValue(value);
		return stringValue.getValue();
	}
}
