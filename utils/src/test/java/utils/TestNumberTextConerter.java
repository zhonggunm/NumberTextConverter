package utils;

import com.zhong.projects.NumberTextConverter;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisplayName("Test the number to text converter")
class TestNumberTextConerter {
	private final static Logger logger = LoggerFactory.getLogger(TestNumberTextConerter.class);
	
	private final static String[][] basicScenarios = {
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

			
			{ "23", "twenty three dollars" }, 
			{ "-45", "minus forty five dollars" },
			{"0","zero dollar"},
			{ "123.46", "one hundred twenty three dollars and forty six cents" }, 
			{ "0.345", "thirty five cents" },
			{ "0.244", "twenty four cents" },

			/*
			 * 3. Negative conditions - Spaces in the input - non numerical characters in
			 * the input - multiple '.' in input - empty string - null
			 */

	};
	
	private final static String[][] boundaryScenarios = {
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
	 * 3.Testing the method taking a float as input.
	 */
	private final static double[] floatInput = { 
			-102234f,
			999.99f,
	};

	private final static String[] floatExpectedOutput = { 
			"minus one hundred two thousand two hundred thirty four dollars",
			 "nine hundred ninety nine dollars and ninety nine cents",
	};



	/*
	 * 4. Negative conditions 
	 * - Spaces in the input 
	 * - non numerical characters in the input 
	 * - multiple '.' in input - empty string
	 * - Max + 1
	 * - Min - 1
	 */
	private final static String[] exceptionScenarios = { 
			"34567 55", 
			"52345x35.35", 
			"535.353.5",
			"1000000000000000001",
			"-1000000000000000001",
	};
	
	private final static Class[] expectedExceptions = { 
			NumberFormatException.class, 
			NumberFormatException.class, 
			NumberFormatException.class, 
			NumberTextConverter.NumberOutOfRangeException.class,
			NumberTextConverter.NumberOutOfRangeException.class,
	};

	
	public static void main(String[] args) {
		logger.info("Test the converter");

		int failed = 0;
		failed += testBasicFunctions();
		failed += testBoundaryHandling();
		failed += testExceptionScenarios();
		failed += testConvertFloat();
		
		logger.info("Test completed. \n" +
		        "{} tested, {} failed.\n", 
				basicScenarios.length + boundaryScenarios.length + exceptionScenarios.length + floatInput.length,
				failed);
	}

	/**
	 * Test the basic scenarios of the converting function.
	 * @return
	 */
	static int testBasicFunctions() {
		int passed = 0;
		int failed = 0;


		logger.info("Start testing basic function handling.\n");

		for (String[] tc : basicScenarios) {

			try {

				String textString = NumberTextConverter.convert(tc[0]);
				if (!tc[1].equals(textString)) {
					logger.info("Case failed, number is {}, \n" + 
							"converted text is {}\n" + 
							"expected  text is {}\n",
							tc[0], 
							textString, 
							tc[1]);

					failed++;
				}
				
				passed++;
			} catch (Exception e) {
				failed++;
				logger.info("Caught an exception: {}", e.toString());
				continue;
			}
		}

		logger.info("{} cases tested, {} cases passed, {} cases failed.\n", basicScenarios.length, passed, failed);

		return failed;
	}

	/**
	 * Test the boundary scenarios of the converting function.
	 * @return - the number of failed cases.
	 */
	static int testBoundaryHandling() {
		int passed = 0;
		int failed = 0;


		logger.info("Start testing boundary handling.\n");

		for (String[] tc : boundaryScenarios) {
			try {
				String textString = NumberTextConverter.convert(tc[0]);

				if (!tc[1].equals(textString)) {
					logger.info("!!!Case failed: input number is {}\nexpected text: {},\n" + "returned text: {} \n",
							tc[0], tc[1], textString);

					failed++;
				}
			} catch (Exception e) {

				logger.info("Processing {}, caught an exception: {}", tc[0], e.toString());
				failed++;
				continue;
			}
			passed++;
		}

		logger.info("{} cases tested, {} cases passed, {} cases failed.\n", boundaryScenarios.length, passed, failed);

		return failed;

	}

	/**
	 * Test the exception scenarios of the converting function.
	 * @return - the number of failed cases.
	 */
	static int testExceptionScenarios() {

		int passed = 0;
		int failed = 0;

		logger.info("Start testing number format error handling.\n");

		for (int i = 0; i < exceptionScenarios.length; i++) {
			String tc = exceptionScenarios[i];
			String convertedString;
			logger.info("Input: {}\n", tc);

			try {
				convertedString = NumberTextConverter.convert(tc);
			} catch (Exception e) {
				logger.info("Caught an exception as expected: {}.\n", e.toString());
				if(e.getClass() == expectedExceptions[i]) {
					passed++;
				} else {
					failed++;
				}
				continue;

			}
			logger.info("Expected to catch exception, but did not. The converted text is: \n{}\n", convertedString);
			failed++;
		}
		
		logger.info("{} cases tested, {} cases passed, {} cases failed", exceptionScenarios.length, passed, failed);
		
		return failed;
	}

	/**
	 * Test the converting method which takes a float as input.
	 * @return - the number of failed cases.
	 */
	static int testConvertFloat() {
		int passed = 0;
		int failed = 0;
		logger.info("Start testing input is a float number.\n");

		for(int i = 0; i < floatInput.length; i++) {
			String convertedString = NumberTextConverter.convert(floatInput[i]);
			if(convertedString.equals(floatExpectedOutput[i])) {
				passed++;
			} else {
				failed++;
				logger.info("!!!Case failed: input number is {}\nexpected text: {},\n" + "returned text: {} \n",
						floatInput[i], floatExpectedOutput[i], convertedString);
			}
		}
			
		logger.info("{} cases passed, {} cases failed.\n", passed, failed);
		return failed;
	}

}
