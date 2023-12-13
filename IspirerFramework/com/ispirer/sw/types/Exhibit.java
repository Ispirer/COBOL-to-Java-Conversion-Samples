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

public class Exhibit {

	private String[] params;
	private String[] names;

	public Exhibit() {

	}

	public Exhibit(String[] params, String[] names) {
		this.params = params;
		this.names = names;
	}

	public String getValueChanged(Object[] params) {
		String result = "";
		int i = 0;
		for (Object param : params) {
			if (param.toString().equals(this.params[i])) {
				if (param instanceof StructureModel) {
					((StructureModel) param).setDefaultValue(PictureType.DefaultValue.Spaces);
					result = result + param.toString() + " ";
				} else if (param instanceof PictureType) {
					((PictureType<?>) param).setDefaultValue(PictureType.DefaultValue.Spaces);
					result = result + param.toString() + " ";
				}
			} else {
				result = result + param.toString() + " ";
			}
			i++;
		}
		return result;
	}

	public String getValueChangedNamed(Object[] params) {
		String result = "";
		int i = 0;
		for (Object param : params) {
			if (param.toString().equals(this.params[i])) {
				if (param instanceof StructureModel) {
					((StructureModel) param).setDefaultValue(PictureType.DefaultValue.Spaces);
					result = result + this.names[i] + " = " + param.toString() + " ";
				} else if (param instanceof PictureType) {
					((PictureType<?>) param).setDefaultValue(PictureType.DefaultValue.Spaces);
					result = result + this.names[i] + " = " + param.toString() + " ";
				}
			} else {
				result = result + this.names[i] + " = " + param.toString() + " ";
			}
			i++;
		}
		return result;
	}

	public String getValue() {
		String result = "";
		for (Object param : this.params) {
			result = result + param + " ";
		}
		return result;
	}

	public String getValueNamed() {
		String result = "";
		for (int i = 0; i < this.params.length; i++) {
			result = result + this.names[i] + " = " + this.params[i] + " ";
		}
		return result;
	}
}
