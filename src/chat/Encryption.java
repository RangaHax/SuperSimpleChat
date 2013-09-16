package chat;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Encryption {

	/*
	public static void main(String[] args) {
		System.out.println("Testing Obfuscation...");
		String text = "Hello my name is Andrew!";
		System.out.println("Clear Text: "+text);
		text = obfuscate(text);
		System.out.println("Code String: "+text);
		text = unobfuscate(text);
		System.out.println("Clear Text: "+text);
		System.out.println();

		System.out.println("Testing Encryption...");
		String code = "Hello how are you today?";
		System.out.println("Clear Text: "+code);
		code = encrypt(code);
		System.out.println("Code String: "+code);
		code = decrypt(code);
		System.out.println("Clear Text: "+code);
	}
	*/


	private Encryption() {} //private so you cant create an instance of this class
	private static final String INTERLACE = "supasecretpassword";

	public static String encrypt(String clearText) {
		SimpleDateFormat sdf = new SimpleDateFormat("SSSssmmHHdd");
		Date now = new Date();
		String code = sdf.format(now);
		clearText = code+clearText;
		int[] clearInts = stringToInts(clearText);
		int[] lols = stringToInts(INTERLACE);
		int[] nums = new int[clearInts.length*2];
		for(int i = 0, j = 0; i < clearInts.length; i++,j=j+2) {
			nums[j] = clearInts[i];
			nums[j+1] = lols[i%INTERLACE.length()];
		}
		String code2 = intsToString(nums);
		code2 = obfuscate(code2);
		return code2;
	}
	public static String decrypt(String codeText) {
		String code2 = unobfuscate(codeText);
		int[] nums = stringToInts(code2);
		if(nums.length%2 != 0) return null;
		int[] clearInts = new int[nums.length/2];
		int[] lols = new int[nums.length/2];
		for(int i = 0, j = 0; i < clearInts.length; i++,j=j+2) {
			clearInts[i] = nums[j];
			lols[i] = nums[j+1];
		}
		String interlacedKey = intsToString(lols);
		for(int i = 0; i < interlacedKey.length(); i++) {
			if(interlacedKey.charAt(i) != INTERLACE.charAt(i%INTERLACE.length())) {
				return null;
			}
		}
		String clearText = intsToString(clearInts);
		return clearText.substring(11);
	}

	//Worker Methods
	private static String obfuscate(String clearText) {
		int[] nums = stringToInts(clearText);
		int totalOffest = nums.length;
		for(int i = 0; i < nums.length; i++) {
			int temp = nums[i];
			nums[i] = nums[i]+totalOffest;
			totalOffest = totalOffest+temp;
		}
		return intsToString(nums);
	}
	private static String unobfuscate(String codeText) {
		int[] nums = stringToInts(codeText);
		int totalOffest = nums.length;
		for(int i = 0; i < nums.length; i++) {
			nums[i] = nums[i]-totalOffest;
			totalOffest = totalOffest+nums[i];
		}
		return intsToString(nums);
	}


	//Helper Methods
	private static int[] stringToInts(String text) {
		int[] nums = new int[text.length()];
		for(int i = 0; i < text.length(); i++) {
			nums[i] = ((int) text.charAt(i))-32;
			//System.out.println("SI Input: "+text.charAt(i)+" -> "+nums[i]);
		}
		return nums;
	}
	private static String intsToString(int[] ints) {
		for(int i = 0; i < ints.length; i++) {
			//System.out.print("IS Input: "+ints[i]);
			while(ints[i] < 0) ints[i] = ints[i]+94;
			ints[i] = ints[i] % 94;
			//System.out.println(" -> "+ints[i]+" -> "+((char) (ints[i]+32)));
		}
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < ints.length; i++) {
			char output = (char) (ints[i]+32);
			b.append(output);
		}
		return b.toString();
	}
}