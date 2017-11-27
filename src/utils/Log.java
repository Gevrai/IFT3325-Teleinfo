package utils;

/* Very very simple print class, differentiating between verbose and normal
 * of program execution.
 * 
 */
public class Log {
	
	private static boolean isVerbose = false;
	
	public static void verbose(String s) {
		if (isVerbose)
			System.out.println(s);
	}
	
	public static void println(String s) {
		System.out.println(s);
	}
	
	public static void setVerbose(boolean b) { isVerbose = b; }
}
