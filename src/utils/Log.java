package utils;

/* Very very simple print class, differentiating between verbose and normal
 * of program execution.
 * 
 */
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
