package com.kreative.acpattern;

import java.util.Arrays;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		if (args.length == 0) {
			ACView.main(args);
			return;
		}
		String verb = args[0].trim().toLowerCase();
		if (verb.equals("convert")) {
			ACConvert.main(subarray(args, 1));
		} else if (verb.equals("view")) {
			ACView.main(subarray(args, 1));
		} else {
			ACView.main(args);
		}
	}
	
	private static String[] subarray(String[] a, int i) {
		List<String> l = Arrays.asList(a).subList(i, a.length);
		return l.toArray(new String[a.length - i]);
	}
}
