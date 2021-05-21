package com.kreative.acpattern;

import java.util.Arrays;
import java.util.List;
import com.kreative.acpattern.robot.PatternBot;
import com.kreative.acpattern.robot.SwitchControl;
import com.kreative.acpattern.robot.WishBot;

public class Main {
	public static void main(String[] args) {
		if (args.length == 0) {
			ACView.main(args);
			return;
		}
		String verb = args[0].trim().toLowerCase();
		if (verb.equals("help") || verb.equals("--help")) help();
		else if (verb.equals("convert")) ACConvert.main(subarray(args, 1));
		else if (verb.equals("view")) ACView.main(subarray(args, 1));
		else if (verb.equals("acnhdebug")) ACNHDebug.main(subarray(args, 1));
		else if (verb.equals("acnldebug")) ACNLDebug.main(subarray(args, 1));
		else if (verb.equals("acqrdebug")) ACQRDebug.main(subarray(args, 1));
		else if (verb.equals("acwwdebug")) ACWWDebug.main(subarray(args, 1));
		else if (verb.equals("switchcontrol")) SwitchControl.main(subarray(args, 1));
		else if (verb.equals("patternbot")) PatternBot.main(subarray(args, 1));
		else if (verb.equals("wishbot")) WishBot.main(subarray(args, 1));
		else ACView.main(args);
	}
	
	private static void help() {
		System.out.println();
		System.out.println("Animal Prints - View and convert Animal Crossing design patterns.");
		System.out.println();
		System.out.println("animalprints convert <options> <files>");
		System.out.println("animalprints view <files>");
		System.out.println();
	}
	
	private static String[] subarray(String[] a, int i) {
		List<String> l = Arrays.asList(a).subList(i, a.length);
		return l.toArray(new String[a.length - i]);
	}
}
