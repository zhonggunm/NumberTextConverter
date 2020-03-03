package utils;

import com.zhong.projects.NumberTextConverter;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisplayName("Test the number to text converter")
class TestNumberTextConerter {
	private final static Logger logger = LoggerFactory.getLogger(TestNumberTextConerter.class);
	
	private final static String[][] CONVERT_STRING_NORMAL_SCENARIOS = {
			/*
			 * 1. Verify the basic function works:
			 *  - Numbers 0~19
			 *  - Tens
			 *  - Hundred
			 *  - Thousand, million, etc. defined in scale array
			 *  - Positive numbers
			 *  - Negative numbers
			 *  - Zero
			 *  - Integer only
			 *  - Fraction only
			 *  - Numbers with both parts
			 *  - Rounding
			 *  - Number with ","
			 *  - Max
			 *  - Min
			 *  - Number with leading '0's
			 */
			//Test single digits
			{ "1", "one dollar" },
			{ "2", "two dollars" },
			{ "3", "three dollars" },
			{ "4", "four dollars" },
			{ "5", "five dollars" },
			{ "6", "six dollars" },
			{ "7", "seven dollars" },
			{ "8", "eight dollars" },
			{ "9", "nine dollars" },
			
			//10 ~ 19
			{ "10", "ten dollars" },
			{ "11", "eleven dollars" },
			{ "12", "twelve dollars" },
			{ "13", "thirteen dollars" },
			{ "14", "fourteen dollars" },
			{ "15", "fifteen dollars" },
			{ "16", "sixteen dollars" },
			{ "17", "seventeen dollars" },
			{ "18", "eighteen dollars" },
			{ "19", "nineteen dollars" },

			//20 ~ 90
			{ "20", "twenty dollars" },
			{ "30", "thirty dollars" },
			{ "40", "forty dollars" },
			{ "50", "fifty dollars" },
			{ "60", "sixty dollars" },
			{ "70", "seventy dollars" },
			{ "80", "eighty dollars" },
			{ "90", "ninety dollars" },
            
			//Cover all elements in scale array
			{ "001001001001001001.01","one quadrillion one trillion one billion one million one thousand one dollars and one cent"},   
			{ " 1000000000000000000 ", "one quintillion dollars"},          //Max      
			{ "-1000000000000000000", "minus one quintillion dollars"},   //Min

			
			{ "0","zero dollar"},
			{ "0.0","zero dollar"},
			{ "23", "twenty three dollars" }, 
			{ "-45", "minus forty five dollars" },
			{ "0.345", "thirty five cents" },
			{ "0.244", "twenty four cents" },
			{ "123.46", "one hundred twenty three dollars and forty six cents" },
			{ "7,456,123.46", "seven million four hundred fifty six thousand one hundred twenty three dollars and forty six cents" },
			{ "7,456,123", "seven million four hundred fifty six thousand one hundred twenty three dollars" }, 

			/*
			 * 3. Negative conditions - Spaces in the input - non numerical characters in
			 * the input - multiple '.' in input - empty string - null
			 */

	};
	
	private final static String[][] CONVERT_STRING_BOUNDARY_SCENARIOS = {
			/*
			 * 2. Boundary and special scenarios
			 *  - null
			 *  - empty
			 *  - zero
			 *  - (-1)
			 *  - amount less than one cent
			 *  - amount rounded to one cent
			 *  - leading 0 
			 *  - 0s in middle 
			 *  spaces on either end
			 * 0.99 
			 * 0.995 
			 * 0.994 
			 * MAX - 1 
			 * MIN + 1
			 */
			{ null, "" },
			{ "", "" },
			{"0","zero dollar"},	
			{ "-1", "minus one dollar" },
			{ "0.004", "zero dollar"},
			{ "0.005", "one cent"},
			{ "0.995", "one dollar"},
			{ "0.994", "ninety nine cents"},			
			{ "0001234", "one thousand two hundred thirty four dollars" },
			{ "  1234  ", "one thousand two hundred thirty four dollars" },
			{ "-00102234", "minus one hundred two thousand two hundred thirty four dollars" },
			{ "999.99", "nine hundred ninety nine dollars and ninety nine cents" },
			{ "999999", "nine hundred ninety nine thousand nine hundred ninety nine dollars" },
			{ "1001", "one thousand one dollars" },
			{ "999999999999999999", "nine hundred ninety nine quadrillion nine hundred ninety nine trillion nine hundred ninety nine billion nine hundred ninety nine million nine hundred ninety nine thousand nine hundred ninety nine dollars"},          //Max - 1      
			{ "-999999999999999999", "minus nine hundred ninety nine quadrillion nine hundred ninety nine trillion nine hundred ninety nine billion nine hundred ninety nine million nine hundred ninety nine thousand nine hundred ninety nine dollars"},          //Min + 1      
	};

	// Random number

	/*
	 * 3. Negative conditions 
	 * - Spaces in the input 
	 * - non numerical characters in the input 
	 * - multiple '.' in input - empty string
	 * - start with '.'
	 * - ',' at wrong position
	 * - Max + 1
	 * - Min - 1
	 */
	private final static String[] CONVERT_STRING_EXCEPTION_SCENARIOS_INPUT = { 
			"34567 55", 
			"52345x35.35", 
			"535.353.5",
			".2345353535",
			"74,56,123", 
			"7,456,123.2,",			 
			",456,123", 
			"1000000000000000001",
			"-1000000000000000001",
			};
	private final static Class[] CONVERT_STRING_EXCEPTION_SCENARIOS_EXCEPTIONS = { 
			NumberFormatException.class,
			NumberFormatException.class,
			NumberFormatException.class,	
			NumberFormatException.class,
			NumberFormatException.class,
			NumberFormatException.class, 
			NumberFormatException.class, 
			NumberTextConverter.NumberOutOfRangeException.class,
			NumberTextConverter.NumberOutOfRangeException.class,
	};
	/*
	private final static String[][] CONVERT_STRING_EXCEPTION_SCENARIOS = { 
			{"34567 55", NumberTextConverter.NUMBER_FORMAT_ERROR}, 
			{"52345x35.35", NumberTextConverter.NUMBER_FORMAT_ERROR}, 
			{"535.353.5", NumberTextConverter.NUMBER_FORMAT_ERROR},
			{".2345353535", NumberTextConverter.NUMBER_FORMAT_ERROR},
			{"74,56,123", NumberTextConverter.NUMBER_FORMAT_ERROR},
			{"7,456,123.2,", NumberTextConverter.NUMBER_FORMAT_ERROR},			 
			{",456,123", NumberTextConverter.NUMBER_FORMAT_ERROR},
			{"1000000000000000001", NumberTextConverter.NUMBER_OUT_OF_RANGE},
			{"-1000000000000000001",NumberTextConverter.NUMBER_OUT_OF_RANGE},
			};
			*/
	
	/* 
	 * 4.Testing the method taking a float as input.
	 */
	private final static double[] CONVERT_FLOAT_NORMAL_SCENARIOS = { 
			-102234f,
			999.99f,
			0.0,
			9000000000000000d,
			-9000000000000000d,
	};

	private final static String[] CONVERT_FLOAT_NORMAL_SCENARIO_OUTPUT = { 
			"minus one hundred two thousand two hundred thirty four dollars",
			 "nine hundred ninety nine dollars and ninety nine cents",
			 "zero dollar",
			 "nine quadrillion dollars",
			 "minus nine quadrillion dollars"
	};

	
	/*
	 * 5. Negative scenarios for float input 
	 * - Max + 1
	 * - Min - 1
	 */
	
	private final static double[] CONVERT_FLOAT_EXCEPTION_SCENARIOS_INPUT = { 
			9000000000000001d,
			-9000000000000001d,
	};
	
	private final static Class[] CONVERT_FLOAT_EXCEPTION_SCENARIOS_EXCEPTIONS = { 
			NumberTextConverter.NumberOutOfRangeException.class,
			NumberTextConverter.NumberOutOfRangeException.class,
	};
	
	/*
	private final static String[] CONVERT_FLOAT_EXPECTED_EXCEPTIONS = { 
			NumberTextConverter.NUMBER_OUT_OF_RANGE,
			NumberTextConverter.NUMBER_OUT_OF_RANGE,
	};
	*/
	public static void main(String[] args) {
		logger.info("Test the converter");

		int failed = 0;
		failed += testConvertStringBasicScenarios();
		failed += testConvertStringBoundaryScenarios();
		failed += testConvertStringExceptionScenarios();
		failed += testConvertFloatNormalScenarios();
		failed += testConvertFloatExceptionScenarios();
		
		logger.info("Test completed. \n" +
		        "{} tested, {} failed.\n", 
				CONVERT_STRING_NORMAL_SCENARIOS.length + CONVERT_STRING_BOUNDARY_SCENARIOS.length + CONVERT_STRING_EXCEPTION_SCENARIOS_INPUT.length + CONVERT_FLOAT_NORMAL_SCENARIOS.length,
				failed);
	}

	/**
	 * Test the basic scenarios of the converting function.
	 * @return
	 */
	static int testConvertStringBasicScenarios() {
		int passed = 0;
		int failed = 0;


		logger.info("Start testing basic function handling.\n");

		for (String[] tc : CONVERT_STRING_NORMAL_SCENARIOS) {

			try {
				String textString = NumberTextConverter.convert(tc[0]);
				if (!tc[1].equals(textString)) {
					logger.error("Case failed, number is {}, \n" + "converted text is {}\n" + "expected  text is {}\n",
							tc[0], textString, tc[1]);

					failed++;
				}
				passed++;
			} catch (Exception e) {
				failed++;
				logger.info("Caught an exception: {}", e.toString());
				continue;
			}
		}

		logger.info("{} cases tested, {} cases passed, {} cases failed.\n", CONVERT_STRING_NORMAL_SCENARIOS.length, passed, failed);

		return failed;
	}

	/**
	 * Test the boundary scenarios of the converting function.
	 * @return - the number of failed cases.
	 */
	static int testConvertStringBoundaryScenarios() {
		int passed = 0;
		int failed = 0;


		logger.info("Start testing boundary handling.\n");

		for (String[] tc : CONVERT_STRING_BOUNDARY_SCENARIOS) {
			try {

				String textString = NumberTextConverter.convert(tc[0]);

				if (!tc[1].equals(textString)) {
					logger.error("!!!Case failed: input number is {}\nexpected text: {},\n" + "returned text: {} \n",
							tc[0], tc[1], textString);

					failed++;
				}

				passed++;
			} catch (Exception e) {
				failed++;
				logger.info("Caught an exception: {}", e.toString());
				continue;
			}
		}

		logger.info("{} cases tested, {} cases passed, {} cases failed.\n", CONVERT_STRING_BOUNDARY_SCENARIOS.length, passed, failed);

		return failed;

	}

	/**
	 * Test the exception scenarios of the converting function.
	 * @return - the number of failed cases.
	 */
	static int testConvertStringExceptionScenarios() {

		int passed = 0;
		int failed = 0;

		logger.info("Start testing number format error handling.\n");
		
		for (int i = 0; i < CONVERT_STRING_EXCEPTION_SCENARIOS_INPUT.length; i++) {
			String tc = CONVERT_STRING_EXCEPTION_SCENARIOS_INPUT[i];
			String convertedString;
			logger.info("Input: {}\n", tc);

			try {
				convertedString = NumberTextConverter.convert(tc);
			} catch (Exception e) {
				logger.info("Caught an exception as expected: {}.\n", e.toString());
				if(e.getClass() == CONVERT_STRING_EXCEPTION_SCENARIOS_EXCEPTIONS[i]) {
					passed++;
				} else {
					failed++;
				}
				continue;

			}
			logger.info("Expected to catch exception, but did not. The converted text is: \n{}\n", convertedString);
			failed++;
		}
		
		logger.info("{} cases tested, {} cases passed, {} cases failed", CONVERT_STRING_EXCEPTION_SCENARIOS_INPUT.length,
				passed, failed);

		return failed;
	}

	/**
	 * Test the converting method which takes a float as input.
	 * @return - the number of failed cases.
	 */
	static int testConvertFloatNormalScenarios() {
		int passed = 0;
		int failed = 0;
		logger.info("Start testing input is a float number.\n");

		for(int i = 0; i < CONVERT_FLOAT_NORMAL_SCENARIOS.length; i++) {
			
			try {
				String convertedString = NumberTextConverter.convert(CONVERT_FLOAT_NORMAL_SCENARIOS[i]);
				if (convertedString.equals(CONVERT_FLOAT_NORMAL_SCENARIO_OUTPUT[i])) {
					passed++;
				} else {
					failed++;
					logger.error("!!!Case failed: input number is {}\nexpected text: {},\n" + "returned text: {} \n",
							CONVERT_FLOAT_NORMAL_SCENARIOS[i], CONVERT_FLOAT_NORMAL_SCENARIO_OUTPUT[i],
							convertedString);
				}

			} catch (Exception e) {
				failed++;
				logger.info("Caught an exception: {}", e.toString());
				continue;
			}
		}

		logger.info("{} cases passed, {} cases failed.\n", passed, failed);
		return failed;
	}

	/**
	 * Test the exception scenarios of the converting function.
	 * @return - the number of failed cases.
	 */
	static int testConvertFloatExceptionScenarios() {
	
		int passed = 0;
		int failed = 0;
	
		logger.info("Start testing conver float number format error handling.\n");
		
		for (int i = 0; i < CONVERT_FLOAT_EXCEPTION_SCENARIOS_INPUT.length; i++) {
			double tc = CONVERT_FLOAT_EXCEPTION_SCENARIOS_INPUT[i];
			String convertedString;
			logger.info("Input: {}\n", tc);

			try {
				convertedString = NumberTextConverter.convert(tc);
			} catch (Exception e) {
				logger.info("Caught an exception as expected: {}.\n", e.toString());
				if(e.getClass() == CONVERT_FLOAT_EXCEPTION_SCENARIOS_EXCEPTIONS[i]) {
					passed++;
				} else {
					failed++;
				}
				continue;

			}
			logger.info("Expected to catch exception, but did not. The converted text is: \n{}\n", convertedString);
			failed++;
		}

		logger.info("{} cases tested, {} cases passed, {} cases failed", CONVERT_FLOAT_EXCEPTION_SCENARIOS_INPUT.length, passed, failed);
		
		return failed;
	}

}
