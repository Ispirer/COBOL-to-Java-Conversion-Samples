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
import com.ispirer.sw.strings.DecimalFormat;
import com.ispirer.sw.strings.Format;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.regex.Pattern;

import static com.ispirer.sw.types.PictureType.DefaultValue.HighValues;
import static com.ispirer.sw.types.PictureType.DefaultValue.Spaces;
import static java.sql.Types.*;

public class PictureType<T extends Object> implements Comparable<Object> {

	private static Logger LOGGER = LoggerFactory.getLogger(PictureType.class);
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	private T value; // stores the value
	private T type; // indicates type of variables
	private String formatedValue = ""; // stores value in the display mood
	private Format format; // the format object.
	private int indicator = 0;
	public boolean hasDefVal = false;
	private String trimmedValue; // stores trimmed value. this value is used for getting and setting value to DB
	public static boolean isEBSDIC = false;
	private DefaultValue defaultValue;

	/**
	 * Use this constructor if you need to make PT variable quickly. For example if
	 * you have numeric constant and need to make it PT variable to use in the
	 * calculations
	 *
	 * Do not use this constructor to declare PT variable that is going to be used
	 * not just in the calculation.
	 *
	 * @param value value of PT variable
	 */
	public PictureType(T value) { // can be used only for calculation and value
		this.value = value;
	}

	/**
	 * Default constructor for PT variable
	 *
	 * @param type   indicates type of variable
	 * @param format object that manage displaying of variables
	 */
	public PictureType(Type type, Format format) {
		this(type, format, Spaces); // bu default value is Spaces
		trimmedValue = null;
	}

	/**
	 * Constructor for PT variable with value for initialization
	 *
	 * @param format object that manage displaying of variables
	 * @param value  value of PT variable. also indicates type variable.
	 */
	public PictureType(Format format, T value) {
		this.type = value;
		this.format = format;
		setValue(value);
		initTrimmedValue(value.toString());
	}

	/**
	 * Constructor for PT variable with defaultValue for initialization
	 *
	 * @param type         indicates type of variable
	 * @param format       object that manage displaying of variables
	 * @param defaultValue object for initialization.
	 */
	public PictureType(Type type, Format format, DefaultValue defaultValue) {
		this.format = format;
		getTypedValue(type);
		setDefaultValue(defaultValue);
		if (format instanceof DecimalFormat && ((DecimalFormat) format).getCompType() != null) {
			setValue(0);
		}
	}

	PictureType.DefaultValue getDefValue() {
		return defaultValue;
	}

	/**
	 * This method adds addValue to current value This method is used for conversion
	 * result. It changed a lot to get the correct implementation of calculation Now
	 * it works the same way as add(Object addValue). And finalObject is not
	 * necessary.
	 *
	 * @param finalObject object where the result of calculation will be placed
	 * @param addValue    value to add
	 * @return the result of calculation in the PictureType<BigDecimal> variable.
	 *         You can use this object in next calculations
	 */
	public PictureType<BigDecimal> add(PictureType<?> finalObject, Object addValue) {
		return add(addValue);
	}

	/**
	 * This method calculates rounded result of addition addValue to current value.
	 * Scale value depends on finalObject's fractional size. Use this method to
	 * round each calculation in whole expression separately
	 *
	 * @param finalObject object where the result of calculation will be placed
	 * @param addValue    value to add
	 * @return the result of calculation in the PictureType<BigDecimal> variable.
	 *         You can use this object in next calculations
	 */
	public PictureType<BigDecimal> addTrunc(PictureType<?> finalObject, Object addValue) {
		return new PictureType<BigDecimal>(
				add(addValue).getValue().setScale(finalObject.getFractionalSize(), RoundingMode.FLOOR));
	}

	/**
	 * This method adds addValue to current value without rounding.
	 *
	 * @param addValue value to add
	 * @return the result of calculation in the PictureType<BigDecimal> variable.
	 *         You can use this object in next calculations
	 */
	public PictureType<BigDecimal> add(Object addValue) {
		if (addValue == null) {
			return new PictureType<>(toBigDecimal(this.value)); // if addValue is null, this method will return current
																// value.
		}
		if (addValue instanceof PictureType) {
			if (((PictureType<?>) addValue).getValue() == null) {
				return new PictureType<>(toBigDecimal(this.value)); // if addValue is null, this method will return
																	// current value.
			}
			return new PictureType<>(
					toBigDecimal(this.value).add(toBigDecimal(((PictureType<?>) addValue).getValue())));
		}
		return new PictureType<>(toBigDecimal(this.value).add(toBigDecimal(addValue)));
	}

	/**
	 * This method subtracts addValue from current value This method is used for
	 * conversion result. It changed a lot to get the correct implementation of
	 * calculation Now it works the same way as subtract(Object addValue). And
	 * finalObject is not necessary.
	 *
	 * @param finalObject object where the result of calculation will be placed
	 * @param addValue    value to subtract
	 * @return the result of calculation in the PictureType<BigDecimal> variable.
	 *         You can use this object in next calculations
	 */
	public PictureType<BigDecimal> subtract(PictureType<?> finalObject, Object addValue) {
		return new PictureType<BigDecimal>(subtract(addValue).getValue());
	}

	/**
	 * This method calculates rounded result of subtraction addValue to current
	 * value. Scale value depends on finalObject's fractional size. Use this method
	 * to round each calculation in whole expression separately
	 *
	 * @param finalObject object where the result of calculation will be placed
	 * @param addValue    value to subtract
	 * @return the result of calculation in the PictureType<BigDecimal> variable.
	 *         You can use this object in next calculations
	 */
	public PictureType<BigDecimal> subtractTrunc(PictureType<?> finalObject, Object addValue) {
		return new PictureType<BigDecimal>(
				subtract(addValue).getValue().setScale(finalObject.getFractionalSize(), RoundingMode.FLOOR));
	}

	/**
	 * This method subtract addValue from variable without rounding.
	 *
	 * @param addValue value to subtract
	 * @return the result of calculation in the PictureType<BigDecimal> variable.
	 *         You can use this object in next calculations
	 */
	public PictureType<BigDecimal> subtract(Object addValue) {
		if (addValue == null) {
			return new PictureType<>(toBigDecimal(this.value));// if addValue is null, this method will return current
																// value.
		}
		if (addValue instanceof PictureType) {
			if (((PictureType<?>) addValue).getValue() == null) {
				return new PictureType<>(toBigDecimal(this.value));// if addValue is null, this method will return
																	// current value.
			}
			return new PictureType<>(
					toBigDecimal(this.value).subtract(toBigDecimal(((PictureType<?>) addValue).getValue())));
		}
		return new PictureType<>(toBigDecimal(this.value).subtract(toBigDecimal(addValue)));
	}

	/**
	 * This method divides current value by addValue object This method is used for
	 * conversion result. It changed a lot to get the correct implementation of
	 * calculation Now it works the same way as divide(Object addValue). And
	 * finalObject is not necessary.
	 *
	 * @param finalObject object where the result of calculation will be placed
	 * @param addValue    value to divide by
	 * @return the result of calculation in the PictureType<BigDecimal> variable.
	 *         You can use this object in next calculations
	 */
	public PictureType<BigDecimal> divide(PictureType<?> finalObject, Object addValue) {
		return new PictureType<BigDecimal>(divide(addValue).getValue());
	}

	/**
	 * This method calculates rounded result of division current value by addValue.
	 * Scale value depends on finalObject's fractional size. Use this method to
	 * round each calculation in whole expression separately
	 *
	 * @param finalObject object where the result of calculation will be placed
	 * @param addValue    value to divide by
	 * @return the result of calculation in the PictureType<BigDecimal> variable.
	 *         You can use this object in next calculations
	 */
	public PictureType<BigDecimal> divideTrunc(PictureType<?> finalObject, Object addValue) {
		return new PictureType<BigDecimal>(
				divide(addValue).getValue().setScale(finalObject.getFractionalSize(), RoundingMode.FLOOR));
	}

	/**
	 * This method divides current value by addValue without rounding.
	 *
	 * @param addValue value to divide by
	 * @return the result of calculation in the PictureType<BigDecimal> variable.
	 *         You can use this object in next calculations
	 */
	public PictureType<BigDecimal> divide(Object addValue) {
		try {
			if (addValue == null) {
				return new PictureType<>(toBigDecimal(this.value));// if addValue is null, this method will return
																	// current value.
			}
			if (addValue instanceof PictureType) {
				if (((PictureType<?>) addValue).getValue() == null) {
					return new PictureType<>(toBigDecimal(this.value));// if addValue is null, this method will return
																		// current value.
				}
				return new PictureType<>(toBigDecimal(this.value)
						.divide(toBigDecimal(((PictureType<?>) addValue).getValue()), 18, RoundingMode.HALF_UP)); // here
																													// is
																													// rounding
																													// because
																													// in
																													// COBOL
																													// the
																													// biggest
																													// length
																													// of
																													// numeric
																													// variable
																													// is
																													// 18.
			}
			return new PictureType<>(toBigDecimal(this.value).divide(toBigDecimal(addValue), 18, RoundingMode.HALF_UP));// here
																														// is
																														// rounding
																														// because
																														// in
																														// COBOL
																														// the
																														// biggest
																														// length
																														// of
																														// numeric
																														// variable
																														// is
																														// 18.
																														// And
																														// other
		} catch (Exception e) {
			// CHANGE 09/25/2018 added try/catch block to handle arithmetic errors. Now it
			// returns 0 if any error has been caught
			return new PictureType<>(new BigDecimal(0));
		}
	}

	/**
	 * calculates division of current value by addValue
	 *
	 * @param addValue value to divide by
	 * @return integer part of division and remainder in the BigDecimal array.
	 */
	public BigDecimal[] divideAndRemainder(Object addValue) {
		try {
			if (addValue == null) {
				return new BigDecimal[] { new BigDecimal(0), new BigDecimal(0) };// if addValue is null, this method
																					// will return current value.
			}
			if (addValue instanceof PictureType) {
				if (((PictureType<?>) addValue).getValue() == null) {
					return new BigDecimal[] { new BigDecimal(0), new BigDecimal(0) };// if addValue is null, this method
																						// will return current value.
				}
				return toBigDecimal(this.value)
						.divideAndRemainder(toBigDecimal(((PictureType<?>) addValue).getValue()));
			}
			return toBigDecimal(this.value).divideAndRemainder(toBigDecimal(addValue));
		} catch (Exception e) {
			// CHANGE 09/25/2018 added try/catch block to handle arithmetic errors. Now it
			// returns 0 if any error has been caught
			return new BigDecimal[] { new BigDecimal(0), new BigDecimal(0) };
		}
	}

	/**
	 * This method multiplies current value by addValue object This method is used
	 * for conversion result. It changed a lot to get the correct implementation of
	 * calculation Now it works the same way as multiply(Object addValue). And
	 * finalObject is not necessary.
	 *
	 * @param finalObject object where the result of calculation will be placed
	 * @param addValue    value to multiply by
	 * @return the result of calculation in the PictureType<BigDecimal> variable.
	 *         You can use this object in next calculations
	 */
	public PictureType<BigDecimal> multiply(PictureType<?> finalObject, Object addValue) {
		return new PictureType<BigDecimal>(multiply(addValue).getValue());
	}

	/**
	 * This method calculates rounded result of multiplying current value by
	 * addValue. Scale value depends on finalObject's fractional size. Use this
	 * method to round each calculation in whole expression separately
	 *
	 * @param finalObject object where the result of calculation will be placed
	 * @param addValue    value to multiply by
	 * @return the result of calculation in the PictureType<BigDecimal> variable.
	 *         You can use this object in next calculations
	 */
	public PictureType<BigDecimal> multiplyTrunc(PictureType<?> finalObject, Object addValue) {
		return new PictureType<BigDecimal>(
				multiply(addValue).getValue().setScale(finalObject.getFractionalSize(), RoundingMode.FLOOR));
	}

	/**
	 * This method multiplies current value by addValue without rounding.
	 *
	 * @param addValue value to multiply by
	 * @return the result of calculation in the PictureType<BigDecimal> variable.
	 *         You can use this object in next calculations
	 */
	public PictureType<BigDecimal> multiply(Object addValue) {
		if (addValue == null) {
			return new PictureType<>(toBigDecimal(this.value));// if addValue is null, this method will return current
																// value.
		}
		if (addValue instanceof PictureType) {
			if (((PictureType<?>) addValue).getValue() == null) {
				return new PictureType<>(toBigDecimal(this.value));// if addValue is null, this method will return
																	// current value.
			}
			return new PictureType<>(
					toBigDecimal(this.value).multiply(toBigDecimal(((PictureType<?>) addValue).getValue())));
		}
		return new PictureType<>(toBigDecimal(this.value).multiply(toBigDecimal(addValue)));
	}

	/**
	 * rounds value scale size depends on the decimal size of this variable
	 *
	 * @param value value to round
	 * @return rounded value
	 */
	private BigDecimal round(BigDecimal value) {
		if (value == null) {
			return value;
		}
		return value.setScale(((DecimalFormat) getFormat()).getDecimalSize(), BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * rounds value and set the result into this object
	 *
	 * @param value value to round and set
	 */
	public void setRoundedValue(PictureType<BigDecimal> value) {
		setValue(round(value.getValue()));
	}

	/**
	 * rounds value and set the result into this object
	 *
	 * @param value value to round and set
	 */
	public void setRoundedValue(BigDecimal value) {
		setValue(round(value));
	}

	private int getFractionalSize() {
		return ((DecimalFormat) format).getDecimalSize();
	}

	private int comparePicTo(PictureType<?> comparisonvar) {
		return compareTo(comparisonvar.getValue());
	}

	private int compareToDef(DefaultValue defaultValue) {
		switch (defaultValue) {
		case Spaces:
			return (format instanceof DecimalFormat ? formatedValue.replaceAll("[+-.,/ ]", "") : formatedValue.trim())
					.isEmpty() ? 0 : 1;
		case Zeroes:
			try {
				if (value == null) {
					return 0;
				}
				return getCompareValue(Double.compare(Double.parseDouble(String.valueOf(value).trim()), 0.0));
			} catch (NumberFormatException e) {
				return compareTo("0");
			}
		case LowValues:
			return getCompareValue(formatedValue.compareTo(StringUtils.repeat((char) 0, getSize())));
		case Quotes:
			return formatedValue.compareTo(StringUtils.repeat("\"", format.getLength()));
		case HighValues:
			if (format instanceof DecimalFormat) {
				return compareTo(StringUtils.repeat("9", getSize()));
			} else {
				return compareTo(StringUtils.repeat("\u00FF", getSize()));
			}
		default:
			return 0;
		}
	}

	/**
	 * Compares two objects
	 *
	 * @param comparisonvar
	 * @return 0, 1 or -1 if 0 this object is equal to comparisonvar if 1 this
	 *         object is biegger than comparisonvar if -1 this object is less than
	 *         comparisonvar
	 */
	public int compareTo(Object comparisonvar) {
		if (value == null && !(comparisonvar instanceof DefaultValue)) {
			return -1;// returns -1 if current value is null. It means that comparisonvar is bigger
						// than null. Except cases when comparison var is DefaultValue
		}
		if (comparisonvar instanceof PictureType) {
			if (type == null) {
				return -1 * ((PictureType<?>) comparisonvar).compareTo(value); // revert expression if this object's
																				// type isn't specified to avoid
																				// throwing NPE
			}
			return comparePicTo((PictureType<?>) comparisonvar); // call specific method for comparison with PT variable
		}
		if (comparisonvar instanceof DefaultValue) {
			return compareToDef((DefaultValue) comparisonvar);// call specific method for comparison with DefaultValue
																// variable
		}
		if (type instanceof Integer || type instanceof Long || type instanceof BigDecimal) { // comparison for numeric
																								// values
			if (comparisonvar instanceof String) {
				int res = toString().trim().compareTo(comparisonvar.toString().trim()); // if comparisonvar is string
																						// need to compare in the String
																						// mood
				return getCompareValue(res);
			} else {
				return new BigDecimal(value.toString()).compareTo(new BigDecimal(comparisonvar.toString())); // if
																												// comparison
																												// value
																												// is
																												// numeric
																												// need
																												// to
																												// compare
																												// objects
																												// as
																												// BigDecimal
																												// objects.
				// Because BigDecimal is the biggest numeric type that is used
			}
		} else if (type instanceof String) { // comparison for string values. always in String mood
			int res = toString().trim().compareTo(comparisonvar.toString().trim());
			return getCompareValue(res);
		} else if (type == null && comparisonvar != null) { // if type isn't specified it will compare two objects as
															// BigDecimal.
			return ((BigDecimal) value).compareTo(new BigDecimal(comparisonvar.toString()));
		}
		return 0;
	}

	private int getCompareValue(int value) {
		int res = value;
		if (res > 0) {
			return 1;
		} else if (res < 0) {
			return -1;
		} else {
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	private void executeFormat() {
		if (value == null) {
			return;
		}
		if (value instanceof Integer) {
			if (!((DecimalFormat) format).isSigned()) {
				value = (T) (Integer) Math.abs((Integer) value);
			}
			value = (T) (Integer) new BigInteger(value.toString())
					.divideAndRemainder(new BigInteger("1" + StringUtils.repeat("0", format.getSize())))[1].intValue();
		} else if (value instanceof Long) {
			if (!((DecimalFormat) format).isSigned()) {
				value = (T) (Long) (Math.abs((Long) value));
			}
		} else if (value instanceof BigDecimal) {
			if (!((DecimalFormat) format).isSigned()) {
				value = (T) ((BigDecimal) value).abs();
			}
			if (((BigDecimal) value).compareTo(BigDecimal.ZERO) != 0) {
				if (String.valueOf(((BigDecimal) value).remainder(BigDecimal.ONE).doubleValue()).split("\\.")[1]
						.length() > ((DecimalFormat) format).getDecimalSize()) {
					value = (T) ((BigDecimal) value).setScale(((DecimalFormat) format).getDecimalSize(),
							RoundingMode.DOWN);
				} else {
					value = (T) ((BigDecimal) value).setScale(((DecimalFormat) format).getDecimalSize(),
							RoundingMode.UP);
				}
			}

			value = (T) ((BigDecimal) value).remainder(new BigDecimal("1" + StringUtils.repeat("0", format.getSize())));
		}
		formatedValue = format != null ? format.toStringWithFormat(this.value) : this.value.toString();
		if (this.type instanceof String) {
			value = (T) formatedValue;
		}
	}

	@SuppressWarnings("unchecked")
	private void getTypedValue(Type t) {
		switch (t) {
		case Integer:
			this.type = (T) (Integer) 0;
			break;
		case String:
			this.type = (T) "";
			break;
		case BigDecimal:
			this.type = (T) new BigDecimal(0);
			break;
		case Long:
			this.type = (T) (Long) 0L;
			break;
		default:
		}
	}

	private Integer toInteger(Object value) {
		if (value == null) {
			return 0;
		} else if (value instanceof Integer) {
			return (Integer) value;
		} else if (value instanceof Long) {
			// return ((Long) value).intValue();
			try {
				return java.lang.Math.toIntExact((Long) value);
			} catch (ArithmeticException e) {
				// if long value more them max integer
				return new Integer(value.toString().substring(value.toString().length() - this.getSize() - 1));
			}
		} else if (value instanceof BigDecimal) {
			return ((BigDecimal) value).intValue();
		} else if (value instanceof Double) {
			return ((Double) value).intValue();
		} else if (value instanceof String || value instanceof StructureModel) {
			try {
				String str = value.toString();
				if (str.trim().length() < getSize() && str.trim().length() > 0) {
					str = str.replace(" ", "0");
				}
				return new BigInteger(str.trim())
						.divideAndRemainder(new BigInteger("1" + StringUtils.repeat("0", format.getSize())))[1]
						.intValue();
			} catch (NumberFormatException ex) {
				formatedValue = value.toString();
				return null;
			}
		}
		return 0;
	}

	private Long toLong(Object value) {
		if (value == null) {
			return 0L;
		} else if (value instanceof Integer) {
			return Long.valueOf((Integer) value);
		} else if (value instanceof Long) {
			return (Long) value;
		} else if (value instanceof BigDecimal) {
			return ((BigDecimal) value).longValue();
		} else if (value instanceof Double) {
			return ((Double) value).longValue();
		} else if (value instanceof String || value instanceof StructureModel) {
			try {
				if (StringUtils.containsAny(value.toString(), '+', '-')) {
					throw new NumberFormatException();
				}
				return new BigInteger(value.toString().trim())
						.divideAndRemainder(new BigInteger("1" + StringUtils.repeat("0", format.getSize())))[1]
						.longValue();
			} catch (NumberFormatException ex) {
				formatedValue = value.toString();
				return null;
			}
		}
		return 0L;
	}

	private BigDecimal toBigDecimal(Object value) {
		if (value == null) {
			return new BigDecimal(0);
		} else if (value instanceof Integer || value instanceof Long || value instanceof Double) {
			return new BigDecimal(String.valueOf(value));
		} else if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		} else if (value instanceof String || value instanceof StructureModel) {
			try {
				return new BigDecimal(value.toString().trim());
			} catch (NumberFormatException ex) {
				formatedValue = value.toString();
				return null;
			}
		}
		return new BigDecimal(0);
	}

	private String toStringValue(Object value) {
		if (value == null) {
			return "";
		} else if (value instanceof Integer) {
			return Integer.toString(Math.abs((Integer) value));
		} else if (value instanceof Long) {
			return Long.toString(Math.abs((Long) value));
		} else if (value instanceof BigDecimal) {
			return "" + ((BigDecimal) value).abs().toString().replaceAll("\\.", "");
		} else if (value instanceof Double) {
			return Double.toString(Math.abs((Double) value)).replaceAll("\\.", "");
		} else if (value instanceof String || value instanceof StructureModel) {
			return value.toString();
		}
		return "";
	}

	/**
	 * get String view of object
	 *
	 * @return String view of object
	 */
	@Override
	public String toString() {
		if (this.type instanceof Integer || this.type instanceof Long || this.type instanceof BigDecimal
				|| this.type instanceof String) {
			return formatedValue; // by default it should return formated value because it should store the
									// correct String view of PT object
		} else {
			return String.valueOf(value);
		}
	}

	/**
	 *
	 * @return value of PT variable
	 */
	public T getValue() {
		return value;
	}

	/**
	 * sets value into this object
	 *
	 * @param value to set
	 */
	@SuppressWarnings("unchecked")
	public void setValue(Object value) {
		// initialize trimmed value that is used to set it into DB.
		// this logic works correct only for Stricng PT variables.
		if (value instanceof String) {
			initTrimmedValuebyString(value == null ? "" : (String) value);
		} else {
			initTrimmedValue(value == null ? "" : value.toString());

		}
		if (value instanceof PictureType && ((PictureType<?>) value).getValue() instanceof String) {
			initTrimmedValuebyString(((PictureType<?>) value).getTrimmedValue() == null ? ""
					: ((PictureType<?>) value).getTrimmedValue());
		}

		hasDefVal = false;
		// if new value is PT object need to call specific method for this case
		if (value instanceof PictureType) {
			setValueFromPT((PictureType<?>) value);
			executeFormat();
			return;
		}
		// if new value is DevaultValue object need to call specific method for this
		// case
		if (value instanceof DefaultValue) {
			setDefaultValue((DefaultValue) value);
			return;
		}

		// if new value is other type need to cast it to the correct type
		// the correct type is specified in tyoe variable
		if (this.type instanceof Integer) {
			this.value = (T) toInteger(value);
		} else if (this.type instanceof Long) {
			this.value = (T) toLong(value);
		} else if (this.type instanceof BigDecimal) {
			this.value = (T) toBigDecimal(value);
		} else if (this.type instanceof String) {
			this.value = (T) toStringValue(value);
		}
		// The new value is successfully set
		// need to executes format to get correct formated value for this value
		executeFormat();
	}

	private void setValueFromPT(PictureType<?> value) {
		if (value.format != null && value.format instanceof DecimalFormat && type instanceof String) {
			if (((DecimalFormat) value.format).getSign() == 's') {
				setValue(value.formatedValue.replaceAll("[+-]", ""));
			} else {
				setValue(value.formatedValue);
			}
		} else if (/* type instanceof String && */ value.type instanceof String) { // commented due to case
			/*
			 * PictureType<Integer> LabLine2CtnNo = new
			 * PictureType<>(PictureType.Type.Integer, new DecimalFormat("ZZ9"));
			 * PictureType<String> TShipCtnNo = new PictureType<>(PictureType.Type.String,
			 * new AlphanumericFormat("X(3)")); TShipCtnNo.setValue("7");
			 * System.out.println("|"+TShipCtnNo.toString()+"|");
			 * LabLine2CtnNo.setValue(TShipCtnNo); System.out.println(LabLine2CtnNo);
			 */
			setValue(value.getTrimmedValue());
		} else {
			setValue(value.getValue());
		}
	}

	/**
	 * sets data from file. uses in the DataStructures.
	 *
	 * @param bytes value to set in bytes
	 */
	public void setDataFromFile(byte[] bytes) {
		hasDefVal = false;
		String str = new String(bytes);
		if (str.equals(StringUtils.repeat("\u00ff", str.length()))) { // if HighValues need to set DefaultValue
																		// HighValues
			setDefaultValue(HighValues);
			return;
		}
		if (format instanceof DecimalFormat && ((DecimalFormat) format).getCompType() == DecimalFormat.CompType.Comp) { // proceed
																														// string
																														// for
																														// COMP
																														// variables
			str = setCompFromFile(bytes);
		}
		if (format instanceof DecimalFormat && ((DecimalFormat) format).getCompType() == DecimalFormat.CompType.Comp3) { // proceed
																															// string
																															// for
																															// COMP3
																															// variables
			str = setComp3FromFile(bytes);

		}
		if (format instanceof DecimalFormat
				&& /* ((DecimalFormat) format).isSigned() && */ ((DecimalFormat) format).getCompType() == null) { // adds
																													// sign
																													// for
																													// non
																													// comp
																													// variables
			str = setSignedFromFile(str);
		}
		if (format instanceof DecimalFormat && str.isEmpty()) { // handles situation when str is empty.
			str = "0";
		}
		if (format instanceof DecimalFormat && ((DecimalFormat) format).getDecimalSize() > 0) { // put comma on the
																								// correct place
			try {
				BigDecimal.valueOf(Double.parseDouble(str)); // test if str is numeric
				if (((DecimalFormat) format).getCompType() == DecimalFormat.CompType.Comp3
						&& str.length() < ((DecimalFormat) format).getDecimalSize()) { // string is empty or only have
																						// decimals
					String decNumber = "";
					for (int j = str.length(); j < ((DecimalFormat) format).getDecimalSize(); j++) {
						decNumber = "0" + decNumber;
					}
					str = "." + decNumber + str;
				} else if (!str.contains(".")) {
					str = str.substring(0, str.length() - ((DecimalFormat) format).getDecimalSize()) + "."
							+ str.substring(str.length() - ((DecimalFormat) format).getDecimalSize());
				}
			} catch (NumberFormatException ex) {
				// do nothing in this case
				// exception throws when str is not numeric
				// but in COBOL data will be stored into variable in this case even if it isn't
				// numeric.
			}

		}
		if (format.getPattern().contains(",") && !str.contains(",")) {
			formatedValue = str;
			return;
		}
		// all previous steps was preparation for data from file to be set into this
		// object
		setValue(str);
	}

	private String setCompFromFile(byte[] bytes) {
		String str = bytesToHex(bytes);
		str = StringUtils.repeat(str.charAt(0) == 'F' ? 'F' : '0', 16 - str.length()) + str;
		return String.valueOf(new BigInteger(str, 16).longValue());
	}

	private String setComp3FromFile(byte[] bytes) {
		String str;
		if (isEBSDIC) {
			str = bytesToHex(new String(bytes).getBytes(Charset.forName("IBM1047")));
		} else {
			str = bytesToHex(bytes);
		}
		str = str.replaceAll("EFBE", "");
		boolean isnegative = str.charAt(str.length() - 1) == 'D';
		if (((str.charAt(str.length() - 1) == 'D' || str.charAt(str.length() - 1) == 'C')
				&& ((DecimalFormat) format).isSigned()) || str.charAt(str.length() - 1) == 'F') {
			str = str.substring(0, str.length() - 1);
			try {
				str = String.valueOf(type instanceof BigDecimal && new Long(str).compareTo(
						Long.parseLong(StringUtils.repeat('9', ((DecimalFormat) format).getDecimalSize()))) == -1
								? new BigDecimal(str)
										.divide(new BigDecimal(Math.pow(10, ((DecimalFormat) format).getDecimalSize())
												* (isnegative ? -1 : 1)))
								: new Long(str) * (isnegative ? -1 : 1));
				// str = String.valueOf(new Long(str) * (isnegative ? -1 : 1));
			} catch (Exception e) {
				str = new String(bytes);
			}
		} else {
			str = new String(bytes);
		}
		return str;
	}

	private String setSignedFromFile(String str) {
		if (!str.isEmpty()
				&& Pattern.compile("[qrstuvwxyp}JKLMNOPQR]").matcher(str.substring(str.length() - 1)).matches()) {
			return "-" + trimZeroes(str.substring(0, str.length() - 1)
					+ ((DecimalFormat) format).getNegativeInt(str.charAt(str.length() - 1)));
		}
		if (!str.isEmpty() && Pattern.compile("[\\{ABCDEFGHI]").matcher(str.substring(str.length() - 1)).matches()) {
			return str.substring(0, str.length() - 1)
					+ ((DecimalFormat) format).getNegativeInt(str.charAt(str.length() - 1));
		}
		return str;
	}

	private String trimZeroes(String str) {
		String res = str;
		while (!res.isEmpty() && res.charAt(0) == '0') {
			res = res.substring(1);
		}
		return res;
	}

	private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

	/**
	 * set Defaultvalue variable into this PT object
	 *
	 * @param defaultValue to set
	 */
	@SuppressWarnings("unchecked")
	public void setDefaultValue(DefaultValue defaultValue) {
		switch (defaultValue) {
		case Spaces:
			formatedValue = StringUtils.repeat(" ", format.getLength()); // sets chain of spaces if defaultValue is
																			// SPACES
			setValue(formatedValue);
			break;
		case Zeroes:
			formatedValue = StringUtils.repeat("0", format.getLength());// sets chain of zeroes if defaultValue is
																		// ZEROES
			setValue(formatedValue);
			break;
		case LowValues:
			formatedValue = "";
			value = format instanceof DecimalFormat ? null : (T) formatedValue; // sets empty String if defaultValue is
																				// LowValues
			break;
		case Quotes:
			formatedValue = StringUtils.repeat("\"", format.getLength());
			value = format instanceof DecimalFormat ? null : (T) formatedValue;// sets chain of quotes if defaultValue
																				// is QUOTES
			break;
		case HighValues:
			if (format instanceof DecimalFormat) {
				setValue(StringUtils.repeat("9", getSize()));// sets chain of 9 if defaultValue is HighValues and format
																// instanceof DecimalFormat
				break;
			} else {
				setValue(StringUtils.repeat("\u00FF", getSize()));// sets chain of "\u00FF" if defaultValue is
																	// HighValues and format instanceof
																	// AlphanumericFormat
			}

		default:
		}
		this.defaultValue = defaultValue;
		hasDefVal = false; // set False to hasDefVal flag
	}

	/**
	 * set Defaultvalue variable into this PT object from DataStructure. Uses only
	 * for PT objects that are in structure
	 *
	 * @param defaultValue
	 */
	@SuppressWarnings("unchecked")
	public void setDefaultValueFromStructure(DefaultValue defaultValue) {
		switch (defaultValue) {
		case Spaces:
			formatedValue = StringUtils.repeat(" ", format.getLength());
			value = format instanceof DecimalFormat ? null : (T) formatedValue;// sets chain of spaces if defaultValue
																				// is SPACES
			break;
		case Zeroes:
			formatedValue = StringUtils.repeat("0", format.getLength());
			value = format instanceof DecimalFormat ? (T) (Integer) 0 : (T) formatedValue;// sets chain of zeroes if
																							// defaultValue is ZEROES
			break;
		case LowValues:
			formatedValue = "";
			value = format instanceof DecimalFormat ? null : (T) formatedValue;// sets empty String if defaultValue is
																				// LowValues
			break;
		case Quotes:
			formatedValue = StringUtils.repeat("\"", format.getLength());
			value = format instanceof DecimalFormat ? null : (T) formatedValue;// sets chain of quotes if defaultValue
																				// is QUOTES
			break;
		case HighValues:
			if (format instanceof DecimalFormat) {
				setValue(StringUtils.repeat("9", // sets chain of 9 if defaultValue is HighValues and format instanceof
													// DecimalFormat. put comma if needed
						((DecimalFormat) format).getSize())
						+ ((((DecimalFormat) format).getDecimalSize()) != 0
								? "." + StringUtils.repeat("9", ((DecimalFormat) format).getDecimalSize())
								: ""));
			} else {
				setValue(StringUtils.repeat("\u00FF", getSize()));// sets chain of "\u00FF" if defaultValue is
																	// HighValues and format instanceof
																	// AlphanumericFormat
			}
		default:
		}
		this.defaultValue = defaultValue;
		hasDefVal = true;// set True to hasDefVal flag
		initTrimmedValue(null);
	}

	/**
	 * sets obejct to the initial state
	 */
	public void initialize() {
		initTrimmedValue(null);
		hasDefVal = false;
		if (this.type instanceof Integer) {
			setValue(0);
		} else if (this.type instanceof Long) {
			setValue(0L);
		} else if (this.type instanceof BigDecimal) {
			setValue(new BigDecimal(0));
		} else if (this.type instanceof String) {
			setValue("");
		}
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	/**
	 * converts object to byte array to write it into file
	 *
	 * @return byte array to write into file
	 */
	public byte[] toFile() {
		return value == null || hasDefVal && (format instanceof DecimalFormat && value.equals(0))
				|| (hasDefVal && (defaultValue != HighValues && !(format instanceof DecimalFormat)))
						? formatedValue.getBytes()
						: new String(byteArrToChar(format.toFileString(value))).getBytes();
	}

	private char[] byteArrToChar(byte[] ba) {
		char[] chars = new char[ba.length];

		for (int i = 0; i < ba.length; i++) {
			chars[i] = (char) ba[i];
		}
		return chars;
	}

	/**
	 * returns size of object. size depends on format.
	 *
	 * @return
	 */
	public int getSize() {
		if (format instanceof DecimalFormat) {
			if (((DecimalFormat) format).getCompType() == null) {
				return format.getLength();
			} else if (((DecimalFormat) format).getCompType() == DecimalFormat.CompType.Comp) {
				return ((DecimalFormat) format).getCompSize();
			} else {
				return (int) Math
						.round(((double) format.getSize() + ((DecimalFormat) format).getDecimalSize()) / 2 + 0.5);
			}
		} else {
			return format.getSize();
		}
	}

	/**
	 * Indicators are not supported in java. This method is used to implement Cobol
	 * logic in the converted code. You don't have to use this logic. get method for
	 * indicator
	 *
	 * @return indicator value
	 */
	public int getIndicator() {
		return indicator;
	}

	/**
	 * Indicators are not supported in java. This method is used to implement Cobol
	 * logic in the converted code. You don't have to use this logic. set method for
	 * indicator
	 *
	 * @param indicator new value of indicator
	 */
	public void setIndicator(int indicator) {
		this.indicator = indicator;
	}

	/**
	 * get value with indicator if indicator is -1 it returns null in other case it
	 * returns current value
	 *
	 * @return value to set as a parameter for query
	 */
	@SuppressWarnings("unchecked")
	public T getIndValue() {
		if (indicator == -1) {
			return null;
		}
		if (type instanceof String) {
			return (T) getTrimmedValue();
		}
		return value;
	}

	/**
	 * set value with indicator if value is null indicator is -1 in other case
	 * indicator is 0
	 *
	 * @param value to set into variable
	 */
	public void setIndValue(Object value) {
		if (value == null) {
			indicator = -1;
		} else {
			indicator = 0;
			setValue(value);
		}

	}

	/**
	 * this method for Pro*Cobol with ORACLE
	 */
	public void setIndValue(Object value, int columnType) {
		if (value == null) {
			indicator = -1;
			if (columnType == VARCHAR || columnType == CHAR || columnType == LONGNVARCHAR) {
				setValue("", columnType);
			}
		} else {
			indicator = 0;
			if (columnType == CLOB) {
				try {
					setValue(((Clob) value).getSubString(1, (int) ((Clob) value).length()));
					return;
				} catch (SQLException e) {
					LOGGER.info(String.valueOf(e));
				}
			}
			setValue(value.toString(), columnType);
		}

	}

	private void initTrimmedValue(String value) {
		trimmedValue = value;
		initTrimmedValuebyString(value != null ? value : "");

	}

	private void initTrimmedValuebyString(String value) {
		trimmedValue = value;
		if (trimmedValue.length() > getSize()) {
			trimmedValue = trimmedValue.substring(0, getSize());
		}
		while (trimmedValue.length() > 0 && trimmedValue.substring(trimmedValue.length() - 1).equals(" ")) {
			trimmedValue = trimmedValue.substring(0, trimmedValue.length() - 1);
		}
	}

	/**
	 * Use method tp get trimmedValue use Trimmed value only for String variables
	 *
	 * @return trimmed value
	 */
	public String getTrimmedValue() {
		return trimmedValue == null || trimmedValue.trim().isEmpty() ? "" : trimmedValue;
	}

	/**
	 * this method for Pro*Cobol with ORACLE
	 */
	public void setValue(String value, int columnType) {
		if (value == null) {
			setValue(StringUtils.repeat(" ", getSize()));
			return;
		}
		if (columnType == VARCHAR || columnType == CHAR || columnType == LONGNVARCHAR) {
			setValue(value);
		} else {
			setValue(StringUtils.repeat(" ", getSize() - value.replaceAll("[-.]", "").length())
					+ value.replaceAll("[-.]", ""));
		}
	}

	/**
	 * This enum declares all default values that can be used in cobol
	 */
	public enum DefaultValue {
		Spaces(" "), Zeroes("0"), HighValues(" "), LowValues(" "), Quotes(" "), Nulls(" ");

		private final String value;

		private DefaultValue(String s) {
			value = s;
		}

		public boolean equalsName(String otherName) {
			return value.equals(otherName);
		}

		@Override
		public String toString() {
			return this.value;
		}
	}

	/**
	 * replace string in the current value to new string use for String PT variables
	 *
	 * @param from symbols to replace
	 * @param to   symbols to replace by
	 */
	@SuppressWarnings("unchecked")
	public void replaceAll(String from, String to) {
		formatedValue = formatedValue.replaceAll(from, to);
		if (format instanceof AlphanumericFormat) {
			value = (T) formatedValue;
		}
		if (value instanceof String) {
			initTrimmedValuebyString(formatedValue);
		}
	}

	/**
	 * replace string in the current value to new string only once use for String PT
	 * variables
	 *
	 * @param from symbols to replace
	 * @param to   symbols to replace by
	 */
	@SuppressWarnings("unchecked")
	public void replaceFirst(String from, String to) {
		formatedValue = formatedValue.replaceFirst(from, to);
		if (format instanceof AlphanumericFormat) {
			value = (T) formatedValue;
		}
	}

	/**
	 * this enum specify all type that are supported in the PT
	 */
	public enum Type {
		Integer, String, BigDecimal, Long
	}

}
