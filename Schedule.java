package tabler;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class Schedule {

	int weekdays_size;
	int hourslots_size;
	TreeSet<String>[][] schedule = null;
	
	Schedule() {
		weekdays_size = 0;
		hourslots_size = 0;
		schedule = (TreeSet<String>[][]) new TreeSet[0][0];
	}
	
	Schedule(int weekdays, int hourslots) {
		weekdays_size = weekdays;
		hourslots_size = hourslots;
		schedule = (TreeSet<String>[][]) new TreeSet[weekdays][hourslots];
		for (int i = 0; i < weekdays_size; i++) {
			for (int j = 0; j < hourslots_size; j++) {
				schedule[i][j] = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			}
		}
	}
	
	void addName(String name, int weekday, int hourslot) {
		if (!isValidWeekday(weekday) || !isValidHourslot(hourslot)) {
			// Allow bad input but don't do anything, so the table can be populated easier
			//System.err.println("Wrong weekday or hourslot being passed in:" + weekday + " " + hourslot);
			return;
		}
		schedule[weekday][hourslot].add(name);
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
		System.out.println("\tMONDAY\t\tTUESDAY\t\tWEDNESDAY\t\tTHURSDAY\t\tFRIDAY");
		System.out.println("10:00\n");
		printRow(0);
		System.out.println("10:30\n");
		printRow(1);
		System.out.println("11:00\n");
		printRow(2);
		System.out.println("11:30\n");
		printRow(3);
		System.out.println("12:00\n");
		printRow(4);
		System.out.println("12:30\n");
		printRow(5);
		System.out.println(" 1:00\n");
		printRow(6);
		System.out.println(" 1:30\n");
		printRow(7);
		System.out.println(" 2:00\n");
		printRow(8);
		System.out.println(" 2:30\n");
		printRow(9);
	}
	
}
