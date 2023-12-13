/*
	© 2021, Ispirer Systems OÜ. All rights reserved.
	NOTICE OF LICENSE
	This file\library is Ispirer Reusable Code (“IRC”) and you are granted a non-exclusive, worldwide, perpetual, irrevocable and fully paid up license to use, modify, adapt, sublicense and otherwise exploit this IRC as provided below and under the terms of Ispirer Systems OÜ. Reusable Code License Agreement (“License”), which can be found in supplementary LICENSE.txt file. By using this IRC, you acknowledge that you have read the License and agree with its terms as well as with the fact that IRC is the property of and belongs to Ispirer Systems OÜ only.
	IF YOU ARE NOT AGREE WITH THE TERMS OF THE LICENSE, PLEASE, STOP USING THIS IRC IMMEDIATELY! 
	PLEASE, NOTE, THAT IRC IS DISTRIBUTED “AS IS” AND WITHOUT ANY WARRANTY. IN NO EVENT WILL ISPIRER BE LIABLE FOR ANY DAMAGES, CLAIMS OR COSTS WHATSOEVER OR ANY CONSEQUENTIAL, INDIRECT, INCIDENTAL DAMAGES, OR ANY LOST PROFITS OR LOST SAVINGS. 
	Redistributions of this IRC must retain the above copyright notice and a list of significant changes made to this IRC with indication of its author(s) and date of changes.
	If you need more information, or you think that the License has been violated, please let us know by e-mail: legal.department@ispirer.com  
*/
package com.ispirer.sw.util;

import com.ispirer.sw.types.PictureType;
import java.lang.reflect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgramUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProgramUtils.class);

	public static Class<?> getClass(String name, Package pkg) {
		String cls_name = pkg.getName() + "." + name.substring(0, 1).toUpperCase()
				+ name.toLowerCase().replace("-", "_").substring(1);
		try {
			return Class.forName(cls_name);
		} catch (Exception e) {
			LOGGER.info(String.valueOf(e));
		}
		return null;
	}

	public static Object getObject(PictureType<String> name, Package pkg) {
		try {
			Class<?> cls = getClass(name.getValue(), pkg);
			return cls.getMethod("getInstance").invoke(cls.newInstance());
		} catch (Exception e) {
			LOGGER.info(String.valueOf(e));
		}
		return null;
	}

	public static Method getMethod(PictureType<String> name, Package pkg, Class<?>[] classParams) {
		if (classParams == null) {
			String meth_name = name.getValue().substring(0, 1).toUpperCase()
					+ name.getValue().toLowerCase().replace("-", "").substring(1) + "ProcedureDivision";
			try {
				Class<?> cls = getClass(name.getValue(), pkg);
				return cls.getMethod(meth_name);
			} catch (Exception e) {
				LOGGER.info(String.valueOf(e));
			}
		} else {
			String meth_name = name.getValue().substring(0, 1).toUpperCase()
					+ name.getValue().toLowerCase().replace("-", "").substring(1) + "ProcedureDivision";
			try {
				Class<?> cls = getClass(name.getValue(), pkg);
				return cls.getMethod(meth_name, classParams);
			} catch (Exception e) {
				LOGGER.info(String.valueOf(e));
			}
		}
		return null;
	}
}
