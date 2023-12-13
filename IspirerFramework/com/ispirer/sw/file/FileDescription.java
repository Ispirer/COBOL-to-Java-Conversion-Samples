/*
	© 2021, Ispirer Systems OÜ. All rights reserved.
	NOTICE OF LICENSE
	This file\library is Ispirer Reusable Code (“IRC”) and you are granted a non-exclusive, worldwide, perpetual, irrevocable and fully paid up license to use, modify, adapt, sublicense and otherwise exploit this IRC as provided below and under the terms of Ispirer Systems OÜ. Reusable Code License Agreement (“License”), which can be found in supplementary LICENSE.txt file. By using this IRC, you acknowledge that you have read the License and agree with its terms as well as with the fact that IRC is the property of and belongs to Ispirer Systems OÜ only.
	IF YOU ARE NOT AGREE WITH THE TERMS OF THE LICENSE, PLEASE, STOP USING THIS IRC IMMEDIATELY! 
	PLEASE, NOTE, THAT IRC IS DISTRIBUTED “AS IS” AND WITHOUT ANY WARRANTY. IN NO EVENT WILL ISPIRER BE LIABLE FOR ANY DAMAGES, CLAIMS OR COSTS WHATSOEVER OR ANY CONSEQUENTIAL, INDIRECT, INCIDENTAL DAMAGES, OR ANY LOST PROFITS OR LOST SAVINGS. 
	Redistributions of this IRC must retain the above copyright notice and a list of significant changes made to this IRC with indication of its author(s) and date of changes.
	If you need more information, or you think that the License has been violated, please let us know by e-mail: legal.department@ispirer.com  
*/
package com.ispirer.sw.file;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ispirer.sw.file.sort.FileComparator;
import com.ispirer.sw.file.sort.SortKeys;
import com.ispirer.sw.types.PictureType;
import com.ispirer.sw.types.StructureModel;
import com.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;

/**
 * FileDescription is a class that implements work with files
 *
 * @param <T> is a type of record that file work with at the moment. This type
 *            is using for sorting files and will by specified automatically
 */
public class FileDescription<T> {

	private final Logger LOGGER = Logger.getLogger(FileDescription.class.getName());
	protected int recordSize;
	protected int block;
	protected File file;
	protected FileInputStream streamInput;
	protected FileOutputStream streamOutput;
	protected BufferedReader buffReader;
	private String name = "";
	private Boolean csv = false;
    private CSVReader csvReader;
	private T record;
	private HashMap<String, Object> keys = new HashMap<>();
	private TypeOrganization type = TypeOrganization.LINE_SEQUENTIAL;
	private Boolean advancing = false;
	private int codeError = 0;
	private int recordCounter = 0;
	private boolean isEBCDIC = false;
	public static String lineSeparator = "\r\n"; // You can specify Line separator that is fit to you
	public static List<Field> listField = new ArrayList<>();
	public boolean isInvalidKey;

	/**
	 * enum with types of Organization of files
	 *
	 * There are 4 types of Organization in Cobol
	 *
	 * LINE_SEQUENTIAL Line Sequential files are a special type of sequential file.
	 * They correspond to simple text files as produced by the standard editor
	 * provided with your operating system.
	 *
	 * RECORD_SEQUENTIAL Sequential files are the simplest form of COBOL file.
	 * Records are placed in the file in the order they are written, and can only be
	 * read back in the same order.
	 *
	 * RELATIVE_FILES Every record in a relative file can be accessed directly
	 * without having to read through any other records. Each record is identified
	 * by a unique ordinal number both when it is written and when it is read back.
	 *
	 * INDEXED Indexed files are the most complex form of COBOL file which can be
	 * handled directly by COBOL syntax. Records in an indexed file are identified
	 * by a unique user-defined key when written. Each record can contain any number
	 * of user-defined keys which can be used to read the record, either directly or
	 * in key sequence.
	 *
	 * Now is implemented work with LINE_SEQUENTIAL and INDEXED files by default
	 * organization of files is LINE_SEQUENTIAL
	 */
	public enum TypeOrganization {
		LINE_SEQUENTIAL, RECORD_SEQUENTIAL, RELATIVE_FILES, INDEXED
	}

	/**
	 * FileDescription Constructor
	 *
	 * @param name        path to the file
	 * @param record      length of record
	 * @param block       count of records in a block
	 * @param isNotEBCDIC indicates file format. True value means ASCII and false
	 *                    means EBCDIC
	 */
	public FileDescription(String name, int record, int block, boolean isNotEBCDIC) {
		this.name = name;
		file = new File(this.name);
		this.recordSize = record;
		this.block = block;
		this.isEBCDIC = !isNotEBCDIC;
		this.csv = false;
	}

	/**
	 * FileDescription Constructor
	 *
	 * @param name        path to the file
	 * @param record      length of record
	 * @param block       count of records in a block
	 * @param isNotEBCDIC indicates file format. True value means ASCII and false
	 *                    means EBCDIC
	 * @param csv         indicates file type. True value means CSV file type, false
	 *                    means another type
	 */
	public FileDescription(String name, int record, int block, boolean isNotEBCDIC, boolean csv) {
		this.name = name;
		file = new File(this.name);
		this.recordSize = record;
		this.block = block;
		this.isEBCDIC = !isNotEBCDIC;
		this.csv = csv;
	}

	/**
	 * FileDescription Constructor
	 *
	 * @param name        path to the file
	 * @param record      length of record
	 * @param block       count of records in a block
	 * @param isNotEBCDIC indicates file format. True value means ASCII and false
	 *                    means EBCDIC
	 * @param keys        keys (for INDEXED files)
	 */
	public FileDescription(String name, int record, int block, boolean isNotEBCDIC, boolean csv, String... keys) {
		this.name = name;
		file = new File(this.name);
		this.recordSize = record;
		this.block = block;
		this.isEBCDIC = !isNotEBCDIC;
		this.csv = csv;
		for (String key : keys) {
			this.keys.put(key, new Object());
		}
	}

	/**
	 * This method opens file for writing.
	 *
	 * @param status status structure. use null value if you don't have this
	 *               structure
	 * @param append if <code>true</code>, then bytes will be written to the end of
	 *               the file rather than the beginning
	 * @throws IOException
	 */
	public void openOutput(StructureModel status, boolean append) throws IOException {
		try {
			recordCounter = 0;
			streamOutput = new FileOutputStream(file, append); // file is opened for writing
			if (status != null) {
				status.setData("00".toCharArray());
			}
		} catch (FileNotFoundException exc) {
			LOGGER.log(Level.WARNING, exc.getMessage());
			if (status != null) {
				status.setData("37".toCharArray());
			}
		}
	}

	/**
	 * This method opens file for reading
	 *
	 * @param status status structure. use null value if you don't have this
	 *               structure
	 * @throws IOException
	 */
	public void openInput(StructureModel status) throws IOException {
		try {
			recordCounter = 0;
			 if(csv){
                csvReader = new CSVReader(new FileReader(file.getName()));
            } else{
				streamInput = new FileInputStream(file); // file is opened for reading
				buffReader = // Here BufferedReader object creates. Need for reading file by lines
					isEBCDIC ? Files.newBufferedReader(file.toPath(), Charset.forName("IBM1047")) : // If File in
																									// EBCIDIC need to
																									// create
																									// BufferedReader
																									// object with
																									// encoding
							new BufferedReader(new InputStreamReader(streamInput));
				if (status != null) {
					status.setData("00".toCharArray());
				}
			}

		} catch (FileNotFoundException exc) {
			LOGGER.log(Level.WARNING, exc.getMessage());
			if (status != null) {
				status.setData("35".toCharArray());
			}
		}
	}

	/**
	 * reads one record from file
	 *
	 * @param strRec record object
	 * @return record that was read
	 * @throws IOException when file is finished or some error occurs during reading
	 *                     when file is finished codeError = 0 if codeError != 0
	 *                     there is some error in reading this file
	 */
	public String read(StructureModel strRec) throws IOException {
		recordCounter++;
		if (!csv && streamInput == null) { // check if file is opened
			this.codeError = 1;
			throw new IOException();
		}
		String line;
		int lineNum = 0;
		
		 if(csv){
            String[] values = null;
            values = csvReader.readNext();
            strRec.setData(values); // set data to record object
            return strRec.toString();
        } else{

			if (isEBCDIC) {// need to read line for EBCIDIC file other way
				PictureType.isEBSDIC = true;
				char[] buff = new char[recordSize];
				lineNum = buffReader.read(buff); // reading line
				line = new String(buff);
			} else {
				PictureType.isEBSDIC = false;
				line = buffReader.readLine(); // reading line
			}
		}

		if (lineNum == -1 || line == null) { // check if EOF
			this.codeError = 0;
			throw new IOException();
		}

		strRec.setData(line.toCharArray()); // set data to record object
		return strRec.toString();
	}

	public String readIndexed(StructureModel strRec, String key) throws IOException {
		byte[] data = new byte[strRec.getSize()];
		if (streamInput == null) {
			throw new IOException();
		}
		int sizeRead = streamInput.read(data);
		int sizeReadFinal = sizeRead;
		List<String> listFile = new ArrayList<>();
		byte[] allData;
		allData = Files.readAllBytes(Paths.get(name));

		if (Files.readAllLines(Paths.get(name), StandardCharsets.ISO_8859_1).get(0).length() > 130 && Files
				.readAllLines(Paths.get(name), StandardCharsets.ISO_8859_1).get(0).substring(0, 130).contains("@")) {
			String newLine = Files.readAllLines(Paths.get(name), StandardCharsets.ISO_8859_1).get(0).substring(131);
			allData = newLine.getBytes();
			int i = 0;
			while (i < allData.length) {
				listFile.add(new String(allData).substring(i, sizeRead - 1));
				i = sizeRead + 2;
				sizeRead = sizeRead + sizeReadFinal + 3;
			}
		} else {
			int i = 0;
			while (i < allData.length) {
				listFile.add(new String(allData).substring(i, sizeRead));
				i = sizeRead;
				sizeRead += sizeReadFinal;
			}
		}

		if (key == null) {
			return listFile.get(listFile.size() - 1);
		} else {
			for (String record : listFile) {
				strRec.setData(record.toCharArray());
				try {
					if (strRec.getClass().getField(key).get(strRec).toString().equals(keys.get(key))) {
						strRec.setDataFromFile(record.getBytes());
						return strRec.toString();
					}
				} catch (IllegalAccessException | NoSuchFieldException e) {
					LOGGER.info(String.valueOf(e));
				}
			}
		}
		return null;
	}

	/**
	 * writing data to file
	 *
	 * @param data to write
	 * @throws IOException
	 */
	public void write(String data) throws IOException {
		if (this.type.compareTo(TypeOrganization.LINE_SEQUENTIAL) == 0) {
			if (this.advancing) { // if advancing need to add space in the begining of the record
				streamOutput.write((" " + data + lineSeparator).getBytes()); // in the end of record adding
																				// lineSeparator
			} else {
				streamOutput.write((data + lineSeparator).getBytes());
			}
		} else if (this.advancing) {
			streamOutput.write((" " + data).getBytes());
		} else {
			streamOutput.write(data.getBytes());
		}
		recordCounter++;
		streamOutput.flush();
	}

	/**
	 * immitate Cobol function write After Line
	 *
	 * @param data to write
	 * @param line after that need to write.
	 * @throws IOException
	 */
	public void writeAfterLine(Object data, int line) throws IOException {
		if (this.advancing) {
			//
			// left this code commented because we don't know why WRITE AFTER ADVANCING n
			// LINES doesn't add blank lines in COBOL.
			// The situation when it will add lines can be.
			//
//         if(recordCounter == 0){
//            streamOutput.write(StringUtils.repeat(lineSeparator, line).getBytes());
//         } else{
//            streamOutput.write(StringUtils.repeat(lineSeparator, line-1).getBytes());
//         }
			streamOutput.write(("0" + data.toString() + lineSeparator).getBytes()); // need to add zero on the begining
																					// of the record if advancing
		} else {
			streamOutput.write((data.toString() + lineSeparator).getBytes());
			streamOutput.write(StringUtils.repeat(lineSeparator, line).getBytes()); // writes line count of lines before
																					// record
		}
		recordCounter++;
		streamOutput.flush();
	}

	/**
	 * immitate Cobol function write After Page
	 *
	 * @param data to write
	 * @throws IOException
	 */
	public void writeAfterPage(Object data) throws IOException {
		if (data instanceof String) {
			if (this.advancing) {
				streamOutput.write(("1" + data.toString() + lineSeparator).getBytes());// need to add 1 on the begining
																						// of the record if advancing
			} else {
				streamOutput.write((data.toString() + lineSeparator).getBytes());
			}
		} else if (data instanceof StructureModel) {
			byte[] structure = ((StructureModel) data).toFile();
			byte[] newStructure = new byte[structure.length + 2];
			if (this.advancing) {
				newStructure[0] = "1".getBytes()[0];
			}
			for (int i = 1; i <= structure.length; i++) {
				newStructure[i] = structure[i - 1];
			}
			newStructure[newStructure.length - 1] = String.valueOf(lineSeparator).getBytes()[0];
			streamOutput.write(newStructure);
		}
		streamOutput.flush();
	}

	/**
	 * writes StructureModel objects to file
	 *
	 * @param model object to write
	 * @throws IOException
	 */
	public void write(StructureModel model) throws IOException {
		if (this.type.compareTo(TypeOrganization.LINE_SEQUENTIAL) == 0) {
			if (this.advancing) {
				streamOutput.write(" ".getBytes());
				streamOutput.write(model.toFile());
				streamOutput.write(lineSeparator.getBytes());
			} else {
				streamOutput.write(model.toFile());
				streamOutput.write(lineSeparator.getBytes());
			}
		} else if (this.advancing) {
			streamOutput.write(" ".getBytes());
			streamOutput.write(model.toFile());
		} else {
			streamOutput.write(model.toFile());
		}
		streamOutput.flush();
	}

	/**
	 * closes file
	 *
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (streamInput != null) {
			streamInput.close();
			buffReader.close();
		}
		if (streamOutput != null) {
			streamOutput.close();
		}
	}

	public static byte[] transcodeField(byte[] source, Charset from, Charset to) {
		byte[] result = new String(source, from).getBytes(to);
		if (result.length != source.length) {
			throw new AssertionError(result.length + "!=" + source.length);
		}
		return result;
	}

	public boolean hasNext() throws IOException {
		return streamInput.available() > 0;
	}

	public String readIO(StructureModel strRec) throws IOException {
		strRec.setData(new String(Files.readAllBytes(Paths.get(this.name))).toCharArray());
		return strRec.toString();
	}

	public List<String> readTextFileByLines(String fileName) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(fileName));
		return lines;
	}

	public void rewrite(String fileName, String content) throws IOException {
		List<String> lines = Files.readAllLines(file.toPath());
		lines.set(recordCounter - 1, content);
		Files.write(file.toPath(), lines, StandardOpenOption.WRITE);
	}

	public void rewrite(String fileName, StructureModel model) throws IOException {
		List<String> lines = Files.readAllLines(file.toPath());
		lines.set(recordCounter - 1, model.toString());
		Files.write(file.toPath(), lines, StandardOpenOption.WRITE);
	}

	public String getName() {
		return this.name;
	}

	/**
	 * Sort file
	 *
	 * @param sortKeys collection of keys to sort
	 * @param record   specify Type of records in file
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> sortFile(List<SortKeys> sortKeys, T record) {// }, int[] sizes){
		this.record = record;
		List<String> listTempFile = new ArrayList<>();

		try {
			listTempFile = Files.readAllLines(Paths.get(name), StandardCharsets.UTF_8); // reads all lines into List
		} catch (IOException e) {
			LOGGER.info(String.valueOf(e));
		}

		// This loop fills list of records by strings that was read
		List<T> listSFile = new ArrayList<>();
		for (String line : listTempFile) {
			try {
				this.record = (T) this.record.getClass().newInstance();
				this.record.getClass().getMethod("setData", line.toCharArray().getClass()).invoke(this.record,
						line.toCharArray());
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException
					| InstantiationException e) {
				LOGGER.info(String.valueOf(e));
			}
			listSFile.add(this.record);
		}

		listField = new ArrayList<>();
		for (int i = 0; i < sortKeys.size(); i++) {
			listField.add(getField(this.record, sortKeys.get(i).getField()));
		} // collect fields for sorting

		Collections.sort(listSFile, new FileComparator()); // sorting

		return listSFile;
	}

	/**
	 * write all records into file
	 */
	public void writeAll(List<?> list) throws IOException {
		for (Object record : list) {
			write(record.toString());
		}
	}

	/**
	 * remove file is using to remove temp file for sorting
	 *
	 * @throws IOException
	 */
	public void remove() throws IOException {
		close();
		file = null;
		Files.deleteIfExists(Paths.get(name));
	}

	private <T> Field getField(T record, String fieldName) {
		try {
			return record.getClass().getField(fieldName);
		} catch (NoSuchFieldException e) {
			LOGGER.info(String.valueOf(e));
		}
		return null;
	}

	public HashMap<String, Object> getKeys() {
		return keys;
	}

	public void setKeys(HashMap<String, Object> keys) {
		this.keys = keys;
	}

	public TypeOrganization getType() {
		return type;
	}

	public void setType(TypeOrganization type) {
		this.type = type;
	}

	public Boolean getAdvancing() {
		return advancing;
	}

	public void setAdvancing(Boolean advancing) {
		this.advancing = advancing;
	}

	public int getCodeError() {
		return codeError;
	}

	public void setCodeError(int codeError) {
		this.codeError = codeError;
	}

	public void setRecord(T record) {
		// this.record = record;
	}

	public void writeInvalidKey(StructureModel modelRec, String key) throws IOException {
		Class<? extends StructureModel> classDefinition = modelRec.getClass();
		Object model = null;
		Object strRec = null;
		try {
			model = classDefinition.newInstance();
			strRec = classDefinition.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		this.openInput(null);
		((StructureModel) model).setData(readIndexed(((StructureModel) model), null).toCharArray());
		try {
			if (model.getClass().getField(key).get(model).toString().compareTo(keys.get(key).toString()) > 0) {
				throw new IOException();
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		// StructureModel strRec = modelRec;
		byte[] data = new byte[((StructureModel) model).getSize()];
		if (streamInput == null) {
			throw new IOException();
		}
		int sizeRead = streamInput.read(data);
		int sizeReadFinal = sizeRead;
		List<String> listFile = new ArrayList<>();
		byte[] allData;
		allData = Files.readAllBytes(Paths.get(name));

		if (Files.readAllLines(Paths.get(name), StandardCharsets.ISO_8859_1).get(0).length() > 130 && Files
				.readAllLines(Paths.get(name), StandardCharsets.ISO_8859_1).get(0).substring(0, 130).contains("@")) {
			String newLine = Files.readAllLines(Paths.get(name), StandardCharsets.ISO_8859_1).get(0).substring(131);
			allData = newLine.getBytes();
			int i = 0;
			while (i < allData.length) {
				listFile.add(new String(allData).substring(i, sizeRead - 1));
				i = sizeRead + 2;
				sizeRead = sizeRead + sizeReadFinal + 3;
			}
		} else {
			int i = 0;
			while (i < allData.length) {
				listFile.add(new String(allData).substring(i, sizeRead));
				i = sizeRead;
				sizeRead += sizeReadFinal;
			}
		}
		for (String record : listFile) {
			((StructureModel) strRec).setData(record.toCharArray());
			try {
				if (strRec.getClass().getField(key).get(strRec).toString().equals(keys.get(key))) {
					throw new IOException();
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}

		streamOutput.write(modelRec.toFile());
		streamOutput.flush();
	}
}
