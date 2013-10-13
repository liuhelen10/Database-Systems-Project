/* Run it like this:
 * Prompt> java Benchmark 10 Extra Stuff
 */

import java.util.*;
public class Benchmark {

	// Do nothing, just return
	public static void benchmark () {
		;
	}

	/* Check Java documentation on System.currentTimeMillis() if you
	 * are not sure of what this method does.  Go ahead and take a 
	 * look.
	 *
	 * We will be using System.currentTimeMillis() to measure execution
	 * time whenever we need to measure the performance of any Java
	 * program that we write.  See how we wrap around a piece of code
	 * with two separate calls to this function.
	 */
	public static long repeat (int count, String[] args) {
		long start = System.currentTimeMillis();

		for (int i = 0; i < count; i++) {
			try {
				MyParser.mySort(args);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (TokenMgrError e) {
				e.printStackTrace();
			}
		}
		return (System.currentTimeMillis() - start);
	}

	public static void main (String[] args) {

		if (args.length < 1) {
			return;
		}

		int count = Integer.parseInt(args[0]);
		//String[] argsForMySort = Arrays.copyOfRange(args, 1, args.length - 1);
		String[] argsForMySort = new String[2];
		argsForMySort[0] = args[1];
		//long time = repeat(count, argsForMySort);
		long time = repeat(count, argsForMySort);

		System.out.println(count + 
				" methods in " + 
				time +
				" milliseconds");

		System.out.println(args[0]);

		System.out.println(args[1]);
	}

}