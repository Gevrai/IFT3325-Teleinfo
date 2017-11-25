package utils;

public class Log {
	
	private final static boolean isVerbose = true;
	
	public static void verbose(String s) {
		if (isVerbose)
			System.out.println(s);
	}
	
	public static void println(String s) {
		System.out.println(s);
	}
}
