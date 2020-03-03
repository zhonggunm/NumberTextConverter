package com.zhong.projects;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * This class provides the utility methods to convert a number to text, i.e., translate a float number or a number in string 
 * format to English text. 
 * @author 
 *
 */
public class NumberTextConverter {
	
	//Precision to round the input number, 2 is the default value
	private final static int PRECISION = 2;	
	
	//Distance between two ',' in a number, which is 3 digits away
	private final static int COMMAS_DISTANCE = 4;	
		
	//These String constants are used to generate the output text. 
	//They can be stored in external files as well to be more flexible.
	
	//Minus sign '-'
	private final static String MINUS_STRING = "minus";
	
	//Comma sign ','
	private final static String COMMA_STRING = ",";

	//Comma sign ','
	private final static String DOT_STRING = ".";

	//Currency
	private final static String CURRENCY_INTEGER_STRING = "dollar";
	
	private final static String CURRENCY_DECIMAL_STRING = "cent";

	//"and"
	private final static String AND_STRING = "and";

	//"s"
	private final static String PLURAL_STRING = "s";

	//"+"
	private final static String PLUS_SIGN = "1";
	
	//"-"
	private final static String MINUS_SIGN = "-1";

	//Zero
	private final static String ZERO_STRING = "zero";

	//Numbers with specific words
	private final static String[] NUMBER_STRINGS = {
			//single digit numbers
			"",
			"one",
			"two",
			"three",
			"four",
			"five",
			"six",
			"seven",
			"eight",
			"nine",
			
			//Double digits numbers
			"ten",
			"eleven",
			"twelve",
			"thirteen",
			"fourteen",
			"fifteen",
			"sixteen",
			"seventeen",
			"eighteen",
			"nineteen"
	}; 
	
	//Words for tens, 0 and 10 are left empty 
	private final static String[] TENS_STRINGS = {		
			"",
			"",
			"twenty",
			"thirty",
			"forty",
			"fifty",
			"sixty",
			"seventy",
			"eighty",
			"ninety"
	}; 
	
	//Hundred
	private final static String HUNDRED_STRING = "hundred";
	
	//The name for scales. This is based on short scale.
	private static final String[] SCALES_STRINGS = {
			"",            
			" thousand",    // 1,000
			" million",     // 1,000,000
			" billion",     // 1,000,000,000
			" trillion",    // 1,000,000,000,000
			" quadrillion", // 1,000,000,000,000,000
			" quintillion" // 1,000,000,000,000,000,000
	};
	
	public static final String NUMBER_OUT_OF_RANGE = "The number is out of range!";

	public static final String NUMBER_FORMAT_ERROR = "The number format is not correct!";

	// Define maximal and minimal numbers here. These should be reasonable limits for dollars for real world.
	public final static BigDecimal MAX_VALUE = new BigDecimal("1000000000000000000");         //1 Quintillion
	public final static BigDecimal MIN_VALUE = new BigDecimal("-1000000000000000000");        //-1 Quintillion
	public final static double MAX_FLOAT_VALUE = 9000000000000000d;      //The order around the largest number can be represented accurately by a 64-bit float number is 2^53
	public final static double MIN_FLOAT_VALUE = -9000000000000000d;     // the order of the smallest number can be represented accurately by a 64-bit float

	
	//Logger
	private final static Logger myLogger = LoggerFactory.getLogger(NumberTextConverter.class);
	
	/*********************************************************************************************
	 *  TODO:
	 *  These questions should be clarified.
	 * Questions
	 * 1. Input number representation
	 * Float numbers have limited precision. Float numbers can not represent large numbers accurately. 
	 * A 64-bit floating number (double) should be OK for check writing. :) (-9,007,199,254,740,990 ~ 9,007,199,254,740,990) (53-bits)
     * String or other formats are more robust and can be extended easily.
     * 2.Valid range
		Should we set a range for the input number?
	 * 3. Scales
	 *	There are different scales schemes, i.e., long scale, short scale.
	 * 4.Round policies
     *   The input float is rounded to two decimal digits. There are multiple rounding policies.
     * 5. Negative amount
     *  Should negative numbers be supported?
	 * 6. Is it a valid input if ',' is not 3 digits apart from '.' or another ','?
	 * 7. How to handle empty string? 
	 *  - At this moment, a "zero dollar" text is generated.
	 *  
	 *  Assumptions
	 *  1. Input data format
	 *   Both float numbers and string represented numbers are supported. No scentific notation supported.
     *  2. Number range
     *   Considering the input is the amount of money, a range is used here.
     *  3. A negative number is considered as a valid input
     *  4.Rounding to the closest number is used to round the cents.
     *  5. Short scale is used for the conversion
     *  6. It's considered as an invalid number if ',' is not three digits apart from '.' or another ','.
     *  7. Empty string 
     *    A "zero dollar" text is generated.
     *  8. Do not support scientific notition
     *  9. No run-time configuration changes.
	 *  
	 *************************************************************************************************/
	
	/**
	 * Convert a float number to text. 
	 * @param number - the number which will be converted to text.
	 * @return the converted text.
	 * @throws NumberFormatException - thrown if the string does not represent a valid float number.
	 * @throws NumberOutOfRangeException - thrown if the number is out of the range.
	 */
	public static String convert(double number) throws NumberFormatException, NumberOutOfRangeException{

		if(number > MAX_FLOAT_VALUE || number < MIN_FLOAT_VALUE) {
			throw new NumberTextConverter().new NumberOutOfRangeException(Double.toString(number), 
					                                                      Double.toString(MAX_FLOAT_VALUE), 
					                                                      Double.toString(MIN_FLOAT_VALUE));
		}
		else {
			return convert(String.valueOf(number));
		}
	}
	
	/**
	 * 
	 * @param number - the number represented as a string which will be converted. 
	 * @return converted text.
	 * @throws NumberFormatException - thrown if the string does not represent a valid float number.
	 * @throws NumberOutOfRangeException - thrown if the number is out of the range.
	 */
	public static String convert(String number) throws NumberFormatException, NumberOutOfRangeException {
		// Check whether the input is null or empty.
		if (number == null) {
			return "";
		} else {
			number = number.trim();
			if (number.length() == 0) {
				return "";
			}
		}

		StringBuffer result = new StringBuffer();

		// Verify the input is a valid number
		BigInteger[] validateRes = validateNumString(number);

		// Get the integer part as a string since it might be out of the range for
		// built-in integer data types.
		String integerPart = validateRes[1].toString();

		// Fraction is 2 digits, an integer should be OK for it. It can be represented
		// by a String as well.
		int fractionPart = validateRes[2].intValue();

		// Generate the text if the amount is zero
		if ((validateRes[1].compareTo(new BigInteger("0")) == 0)
				&& (validateRes[2].compareTo(new BigInteger("0")) == 0)) {
			return ZERO_STRING + " " + CURRENCY_INTEGER_STRING;
		}

		// Convert the integer part if it's not zero
		if (validateRes[1].compareTo(new BigInteger("0")) > 0) {
			// Hold the 3-digit substring in current iteration
			String curString;
			// Index to the UNIT array for each three digits, initially for the lowest 3
			// digits
			int unitIndex = 0;
			// The number of digits in the integer part
			int numIntegerDigits = integerPart.length();

			myLogger.trace("Integer part: {} number of digits {} \n", integerPart, numIntegerDigits);

			// Loop through the integer string and convert the number 3 digits in each
			// iteration
			while (numIntegerDigits > 0) {
				int startingPos = numIntegerDigits > 3 ? numIntegerDigits - 3 : 0;
				String tmpStr;
				int tmpInt;

				// if remaining digits is more than 3, get the lowest 3 digits
				curString = integerPart.substring(startingPos, numIntegerDigits);
				tmpInt = Integer.parseInt(curString);
				myLogger.trace("Current number under processing:{}\n", curString);

				// If the converted text is not empty, insert it to the result.
				tmpStr = convertThreeDigits(tmpInt);
				if (tmpStr != null && tmpStr.length() > 0) {
					result.insert(0, tmpStr + SCALES_STRINGS[unitIndex] + " ");
				}
				unitIndex++;
				numIntegerDigits -= 3;
			}

			// Add the currency unit
			result.append(CURRENCY_INTEGER_STRING);

			// Add 's' if it's more than 1 dollar
			if (validateRes[1].compareTo(new BigInteger("1")) > 0) {
				result.append(PLURAL_STRING);
			}
		}

		// Append the fraction if it exists.
		if (fractionPart != 0) {
			myLogger.trace("Convert the fraction\n");

			// Append "and" if the number has both integer and fraction parts.
			if (result.length() != 0) {
				result.append(" " + AND_STRING + " ");
			}

			// Fraction only has 2 digits, therefore, can be handled by the other method.
			result.append(convertThreeDigits((int) fractionPart));

			result.append(" " + CURRENCY_DECIMAL_STRING);

			if (fractionPart != 1) {
				result.append(PLURAL_STRING);
			}
		}

		// Prepend "minus" if needed.
		if (validateRes[0].compareTo(new BigInteger(MINUS_SIGN)) == 0) {
			result.insert(0, MINUS_STRING + " ");
		}

		return result.toString();
	}
	
	/**
	 * This method validate whether the input string represents a valid float number. 
	 * If it's not valid, exception would be thrown. Otherwise, it rounds the number, and return the integer part and decimal part of the number.
	 * @param input - a number in string representation 
	 * @return it returns an array with three elements
	 * 			- Element 0 is the sign of the number, 1 is positive and -1 is negative
	 *          - Element 1 is the integer part of the number
	 *          - Element 2 is the decimal part of the number 
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	private static BigInteger[] validateNumString(String inStr) throws NumberFormatException, NumberOutOfRangeException {
		BigInteger[] res = new BigInteger[3];
		BigDecimal inNumber;
		
		//A valid number should not start with '.'
		if( inStr.charAt(0) == '.')
		{
			throw new NumberFormatException("A valid number should not start with '.'!");
		}
		
		//Check whether there are 3 digits between ',' and '. or two ','. 
        //Remove ',' if they are in the right positions.
		StringBuffer tmpStr = new StringBuffer(inStr);
		
		//Get the position of '.'. Set it to the string end if this number does not have a dot.
		int dot_position = tmpStr.indexOf(DOT_STRING);
		
		//If there is no '.', set the position to string length.
		if(dot_position == -1){
			dot_position = tmpStr.length();
		}
		
		int comma_position = tmpStr.indexOf(COMMA_STRING);
		
		//If it contains ',', check the position.
		while(comma_position != -1) {
			//If it starts with ',', 
			//or a ',' appears after the '.'
			//or it's not at the right position, throw an exception.
			if( (comma_position == 0) ||
				(comma_position > dot_position) ||
				(( dot_position - comma_position) % COMMAS_DISTANCE != 0)) {
				throw new NumberFormatException(inStr + " has \',\' at wrong postions.");
			}
			
			//Remove current ',' and get the position of next ','. 
			tmpStr.deleteCharAt(comma_position);
			dot_position--;
			comma_position = tmpStr.indexOf(COMMA_STRING);
		}
		
		inStr = tmpStr.toString();
		
		// Is the input string a valid number? A NumberFormatException is thrown if it's
		// not valid.
		inNumber = new BigDecimal(inStr);

		// Round the number to 2 decimal digit
		inNumber = inNumber.setScale(PRECISION, RoundingMode.HALF_UP);

		// Is the number in the range?
		if (inNumber.compareTo(MAX_VALUE) > 0 || inNumber.compareTo(MIN_VALUE) < 0) {
			myLogger.info("Input {} is out of range " + inStr.toString());
			NumberOutOfRangeException noore = 
					new NumberTextConverter().new NumberOutOfRangeException("Number out of range: " + inStr.toString(), 
							MAX_VALUE.toString(), MIN_VALUE.toString());
			throw noore;
		}

		// Check its sign, res[0] indicates the sign
		res[0] = new BigInteger(PLUS_SIGN);

		if (inNumber.compareTo(new BigDecimal("0")) < 0) {
			res[0] = new BigInteger(MINUS_SIGN);

			// Get the abs value if it's negative
			inNumber = inNumber.abs();
		}

		// Divide it to the integer section and decimal section
		res[1] = inNumber.toBigInteger();
		BigDecimal tmpBigDecimal = inNumber.subtract(new BigDecimal(res[1]));
		res[2] = tmpBigDecimal.scaleByPowerOfTen(PRECISION).toBigInteger();

		myLogger.trace("The number is valid: sign is {}, integer part is {}, decimal part is {}", res[0].toString(),
				res[1].toString(), res[2].toString());

		return res;
	}

	/**
	 * Convert a number with no more than three digits to text.
	 * @param number - the number which will be converted
	 * @return the converted text representation of the number
	 */
	private static String convertThreeDigits(int number) throws NumberFormatException{

		myLogger.trace("Converting number {}\n", number);

		if (number == 0) {
			return "";
		}

		StringBuilder stringB = new StringBuilder();

		//It could be handled in a different way because this is an internal error by invoking a private method.
		if (number > 999) {
			myLogger.error("Expecting a 3-digit number, but get a larger one: " + number +"!\n");
			throw new NumberFormatException("Expecting a 3-digit number, but get a larger one: " + number +"!\n");
		}
		
		if (number >= 100) {
			int firstDigit = number / 100;
			number = number % 100;
			stringB.append(NUMBER_STRINGS[firstDigit] + " " + HUNDRED_STRING + " ");
		}

		if (number < 20) {
			stringB.append(NUMBER_STRINGS[number]);
		} else {
			stringB.append(TENS_STRINGS[number / 10]);
			if (number % 10 != 0) {
				stringB.append(" " + NUMBER_STRINGS[number % 10]);
			}
		}

		return stringB.toString();
	}

	/**
	 * This exception indicates that the input number is out of the range.
	 * @author 
	 *
	 */
	public class NumberOutOfRangeException extends Exception{
		private static final long serialVersionUID = 1L;
		String errInfo;
		
		/**
		 * Constructor
		 * @param - error message.
		 * @param - minimal valid value.
		 * @param - maximal valid value.
		 */
		public NumberOutOfRangeException(String msg, String min, String max) {
			super();
			errInfo = "\n" + msg + "\n" +
			          "The maximal supported number is:" + max.toString() + "\n" +
			          "The minimal supported number is:" + min.toString() + "\n";
		}
		
		/**
		 * Return the string representation of this class.
		 */
		public String toString() {
			return super.toString() + errInfo;
		}
	}
	
	/*
	static String convert(long[] number, boolean positive)
	{
		StringBuffer result = new StringBuffer();
		long integerPart;
		long fractionalPart;
		
		if(number == null || number.length != 2)
		{
			return null;
		}
		integerPart = number[0];
		fractionalPart = number[1];
		
		//Validate input
		
		//Check whether it's 0
		if( (integerPart == 0) && (fractionalPart == 0))
		{
			return DIGITS_TO_TEXT[0];
		}
		
		//Negative number
		if( !positive)
		{
			result.append(MINU_STRING + " ");
		}

		//Convert the integer part
		if(integerPart != 0)
		{
			result.append(convertInteger(integerPart, 0));
		
			result.append(" " + CURRENCY_STRING);
		
			if(integerPart != 1)
			{
				result.append("s");
			}
		}

		
		//Append the fraction if it exists.
		if(fractionalPart != 0)
		{

			//Append "and" if the number has both integer and fraction parts.
			if(integerPart != 0)
			{
				result.append("and ");
			}

			//Convert the fraction
			result.append(convertThreeDigits((int)fractionalPart));

			result.append(" cent");

			if(fractionalPart != 1)
			{
				result.append("s");
			}
		}
		
		
		return result.toString();
	}

	private static String convertInteger(long number, int unitIndex)
	{
		StringBuilder stringB = new StringBuilder();
		
		if(number >= 1000)
		{		
			stringB.append(convertInteger(number/1000, unitIndex+1));
			stringB.append(SCALES[unitIndex + 1]);
			number %= 1000;
		}
		stringB.append(" " + convertThreeDigits((int)number));
		
		return stringB.toString();
	}
*/	
}
