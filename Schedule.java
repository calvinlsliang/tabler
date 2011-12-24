package tabler;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

public class Schedule {

	int weekdays_size;
	int hourslots_size;
	HashSet<String>[][] donotSchedule = null;
	TreeSet<String>[][] schedule = null;
	
	Schedule() {
		weekdays_size = 0;
		hourslots_size = 0;
		schedule = (TreeSet<String>[][]) new TreeSet[0][0];
		donotSchedule = (HashSet<String>[][]) new HashSet[0][0];

	}
	
	Schedule(int weekdays, int hourslots) {
		weekdays_size = weekdays;
		hourslots_size = hourslots;
		schedule = (TreeSet<String>[][]) new TreeSet[weekdays][hourslots];
		donotSchedule = (HashSet<String>[][]) new HashSet[weekdays][hourslots];
		for (int i = 0; i < weekdays_size; i++) {
			for (int j = 0; j < hourslots_size; j++) {
				schedule[i][j] = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
				donotSchedule[i][j] = new HashSet<String>();
			}
		}
	}
	
	// donotschedule, used to check if I'm scheduling in the 1-hour gap between two classes
	void addDNS(String name, int weekday, int hourslot) {
		if (!isValidWeekday(weekday) || !isValidHourslot(hourslot)) {
			// Allow bad input but don't do anything, so the table can be populated easier
			return;
		}
		
		// Already scheduled a one-hour slot. Got to remove, whoops!
		if (schedule[weekday][hourslot].contains(name)) {
			schedule[weekday][hourslot].remove(name);
		}
		donotSchedule[weekday][hourslot].add(name);
	}
	
	void addName(String name, int weekday, int hourslot) {
		if (!isValidWeekday(weekday) || !isValidHourslot(hourslot)) {
			// Allow bad input but don't do anything, so the table can be populated easier
			return;
		}
		
		// check if there's a class during this time
		// It's the one-hour gap between two classes. Abort!
		if (!donotSchedule[weekday][hourslot].contains(name)) {
			schedule[weekday][hourslot].add(name);
		}
	}
	
	boolean isValidWeekday(int weekday) {
		return weekday >= 0 && weekday < weekdays_size;
	}
	
	boolean isValidHourslot(int hourslot) {
		return hourslot >=0 && hourslot < hourslots_size;
	}
	
	void printXspaces(int num) {
		if (num < 0 || num > 25) {
			return;
		}
		for (int i = 0; i < 25-num; i++) {
			System.out.print(" ");
		}
	}
	void printRow(int rownumber) {
		if (!isValidHourslot(rownumber)) {
			System.err.println("Wrong rownumber: " + rownumber);
		}
		
		Iterator<String> iter0 = schedule[0][rownumber].iterator();
		Iterator<String> iter1 = schedule[1][rownumber].iterator();
		Iterator<String> iter2 = schedule[2][rownumber].iterator();
		Iterator<String> iter3 = schedule[3][rownumber].iterator();
		Iterator<String> iter4 = schedule[4][rownumber].iterator();

		String name;
		while (iter0.hasNext() || iter1.hasNext() || 
				iter2.hasNext() || iter3.hasNext() || 
				iter4.hasNext()) {
			System.out.print("\t");
			if (iter0.hasNext()) {
				name = iter0.next();
				System.out.print(name);
				printXspaces(name.length());
			} else {
				printXspaces(0);
			}
			if (iter1.hasNext()) {
				name = iter1.next();
				System.out.print(name);
				printXspaces(name.length());
			} else {
				printXspaces(0);
			}
			if (iter2.hasNext()) {
				name = iter2.next();
				System.out.print(name);
				printXspaces(name.length());
			} else {
				printXspaces(0);
			}
			if (iter3.hasNext()) {
				name = iter3.next();
				System.out.print(name);
				printXspaces(name.length());
			} else {
				printXspaces(0);
			}
			if (iter4.hasNext()) {
				name = iter4.next();
				System.out.print(name);
				printXspaces(name.length());
			} else {
				printXspaces(0);
			}
			System.out.println();
		}
		System.out.println();
	}
	
	void print() {
		System.out.print("\tMONDAY");
		printXspaces("MONDAY".length());
		System.out.print("TUESDAY");
		printXspaces("TUESDAY".length());
		System.out.print("WEDNESDAY");
		printXspaces("WEDNESDAY".length());
		System.out.print("THURSDAY");
		printXspaces("THURSDAY".length());
		System.out.print("FRIDAY");
		printXspaces("FRIDAY".length());
		System.out.println();
		
		System.out.print("10:00");
		printRow(0);
		System.out.print("10:30");
		printRow(1);
		System.out.print("11:00");
		printRow(2);
		System.out.print("11:30");
		printRow(3);
		System.out.print("12:00");
		printRow(4);
		System.out.print("12:30");
		printRow(5);
		System.out.print(" 1:00");
		printRow(6);
		System.out.print(" 1:30");
		printRow(7);
		System.out.print(" 2:00");
		printRow(8);
		System.out.print(" 2:30");
		printRow(9);
	}
	
}
