/*
	© 2021, Ispirer Systems OÜ. All rights reserved.
	NOTICE OF LICENSE
	This file\library is Ispirer Reusable Code (“IRC”) and you are granted a non-exclusive, worldwide, perpetual, irrevocable and fully paid up license to use, modify, adapt, sublicense and otherwise exploit this IRC as provided below and under the terms of Ispirer Systems OÜ. Reusable Code License Agreement (“License”), which can be found in supplementary LICENSE.txt file. By using this IRC, you acknowledge that you have read the License and agree with its terms as well as with the fact that IRC is the property of and belongs to Ispirer Systems OÜ only.
	IF YOU ARE NOT AGREE WITH THE TERMS OF THE LICENSE, PLEASE, STOP USING THIS IRC IMMEDIATELY! 
	PLEASE, NOTE, THAT IRC IS DISTRIBUTED “AS IS” AND WITHOUT ANY WARRANTY. IN NO EVENT WILL ISPIRER BE LIABLE FOR ANY DAMAGES, CLAIMS OR COSTS WHATSOEVER OR ANY CONSEQUENTIAL, INDIRECT, INCIDENTAL DAMAGES, OR ANY LOST PROFITS OR LOST SAVINGS. 
	Redistributions of this IRC must retain the above copyright notice and a list of significant changes made to this IRC with indication of its author(s) and date of changes.
	If you need more information, or you think that the License has been violated, please let us know by e-mail: legal.department@ispirer.com  
*/
package com.ispirer.sw.jd;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ispirer.sw.file.FileDescription;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Gcos {

	private static final Logger LOGGER = LoggerFactory.getLogger(Gcos.class);

	public static void sort(String inFile, String typeInFile, String outFile, String typeOutFile, String keyString,
			int recSize) {
		List<String> allInRecords = new ArrayList<>();
		try {
			allInRecords = Arrays.asList(
					new String(Files.readAllBytes(Paths.get(inFile))).split(String.format("(?<=\\G.{%s})", recSize)));
		} catch (IOException e) {
			LOGGER.info(String.valueOf(e));
		}

		String[] keys = keyString.split(" ");
		List<String[]> keyPairs = new ArrayList<>();
		for (int i = 0; i < keys.length; i++) {
			keyPairs.add(new String[] { keys[i], keys[++i] });
		}

		MultiMap multiMap = new MultiValueMap();
		String bufLine = "";
		int cnt = 0;
		for (String line : allInRecords) {
			if (line.equals(bufLine)) {
				cnt++;
				line = line + cnt++;
			} else {
				bufLine = line;
			}
			for (int j = 0; j < keyPairs.size(); j++) {
				String key = line.substring(Integer.valueOf(keyPairs.get(j)[0]) - 1,
						Integer.valueOf(keyPairs.get(j)[0]) - 1 + Integer.valueOf(keyPairs.get(j)[1]));
				multiMap.put(line, key);
			}
		}

		@SuppressWarnings("unchecked")
		List<Map.Entry<String, List<String>>> listOfEntries = new ArrayList<>(multiMap.entrySet());

		List<Map.Entry<String, List<String>>> listOfEntriesGroup = new ArrayList<>();
		List<Map.Entry<String, List<String>>> listOfEntriesFinal = new ArrayList<>();

		int x = 0;
		Collections.sort(listOfEntries, comparator(x));
		while (x < (keyPairs.size() - 1)) {
			x++;
			for (int ind = 0; ind < listOfEntries.size(); ind++) {
				int indGroup = 0;
				int defInd = ind;
				boolean flag = true;
				if (ind < (listOfEntries.size() - 1) && listOfEntries.get(ind).getValue().get(x - 1)
						.compareTo(listOfEntries.get(++ind).getValue().get(x - 1)) == 0) {
					listOfEntriesGroup.add(indGroup, listOfEntries.get(ind - 1));
					indGroup++;
					ind--;
				} else {
					if (ind != listOfEntries.size() - 1) {
						ind--;
						flag = false;
					}
					if (listOfEntriesGroup.isEmpty()) {
						if (defInd != ind && flag) {
							ind--;
						}
						listOfEntriesGroup.add(indGroup, listOfEntries.get(ind));
					} else {
						listOfEntriesGroup.add(++indGroup, listOfEntries.get(ind));
					}

					// }
					Collections.sort(listOfEntriesGroup, comparator(x));
					for (Map.Entry<String, List<String>> line : listOfEntriesGroup) {
						listOfEntriesFinal.add(line);
					}
					listOfEntriesGroup = new ArrayList<>();
				}
			}
			listOfEntries = listOfEntriesFinal;
			listOfEntriesFinal = new ArrayList<>();
		}

		try {
			FileWriter fw = new FileWriter(outFile);
			for (int i = 0; i < listOfEntries.size(); i++) {
				fw.write(listOfEntries.get(i).getKey().substring(0, recSize));
				fw.write(FileDescription.lineSeparator);
			}
			fw.close();
		} catch (IOException e) {
			LOGGER.info(String.valueOf(e));
		}
	}

	private static Comparator<Map.Entry<String, List<String>>> comparator(int x) {
		return new Comparator<Map.Entry<String, List<String>>>() {
			@Override
			public int compare(Map.Entry<String, List<String>> o1, Map.Entry<String, List<String>> o2) {
				CompareToBuilder compareToBuilder = new CompareToBuilder();
				compareToBuilder.append(o1.getValue().get(x), o2.getValue().get(x));
				int comp = compareToBuilder.toComparison();
				if (comp == 0) {
					compareToBuilder.append(o1.getKey(), o2.getKey());
					return compareToBuilder.toComparison();
				}
				return comp;
			}
		};
	}
}
