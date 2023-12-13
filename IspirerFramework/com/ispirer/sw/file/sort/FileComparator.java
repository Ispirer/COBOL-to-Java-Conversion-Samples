/*
	© 2021, Ispirer Systems OÜ. All rights reserved.
	NOTICE OF LICENSE
	This file\library is Ispirer Reusable Code (“IRC”) and you are granted a non-exclusive, worldwide, perpetual, irrevocable and fully paid up license to use, modify, adapt, sublicense and otherwise exploit this IRC as provided below and under the terms of Ispirer Systems OÜ. Reusable Code License Agreement (“License”), which can be found in supplementary LICENSE.txt file. By using this IRC, you acknowledge that you have read the License and agree with its terms as well as with the fact that IRC is the property of and belongs to Ispirer Systems OÜ only.
	IF YOU ARE NOT AGREE WITH THE TERMS OF THE LICENSE, PLEASE, STOP USING THIS IRC IMMEDIATELY! 
	PLEASE, NOTE, THAT IRC IS DISTRIBUTED “AS IS” AND WITHOUT ANY WARRANTY. IN NO EVENT WILL ISPIRER BE LIABLE FOR ANY DAMAGES, CLAIMS OR COSTS WHATSOEVER OR ANY CONSEQUENTIAL, INDIRECT, INCIDENTAL DAMAGES, OR ANY LOST PROFITS OR LOST SAVINGS. 
	Redistributions of this IRC must retain the above copyright notice and a list of significant changes made to this IRC with indication of its author(s) and date of changes.
	If you need more information, or you think that the License has been violated, please let us know by e-mail: legal.department@ispirer.com  
*/
package com.ispirer.sw.file.sort;

import com.ispirer.sw.file.FileDescription;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Comparator;

public class FileComparator implements Comparator<Object> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileComparator.class);

	@Override
	public int compare(Object o1, Object o2) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		for (int i = 0; i < FileDescription.listField.size(); i++) {
			try {
				compareToBuilder.append(FileDescription.listField.get(i).get(o1),
						FileDescription.listField.get(i).get(o2));
			} catch (IllegalAccessException e) {
				LOGGER.info(String.valueOf(e));
			}
		}
		return compareToBuilder.toComparison();
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}
}
