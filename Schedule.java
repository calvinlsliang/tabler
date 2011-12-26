package tabler;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

/*
 * @author Calvin Liang <calvinlsliang@gmail.com>
 */
public class Schedule {
	
	/* 
	 * I wanted two sets for each cell in the grid.
	 * One set would remember the people who absolutely cannot table during that time, whether
	 * it is from classes or clubs. The other set would remember people who are being
	 * considered for that time, mainly the hour after they finish class.
	 */
	int weekdays_size;
	int hourslots_size;
	HashSet<String>[][] donotSchedule = null;
	TreeSet<String>[][] schedule = null; // To keep them alphabetical order
	
	@SuppressWarnings("unchecked")
	Schedule() {
		weekdays_size = 0;
		hourslots_size = 0;
		schedule = (TreeSet<String>[][]) new TreeSet[0][0];
		donotSchedule = (HashSet<String>[][]) new HashSet[0][0];

	}
	
	@SuppressWarnings("unchecked")
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
	
	HashSet<Cell> getProblemCells(int num) {
		HashSet<Cell> cells = new HashSet<Cell>();
		for (int i = 0; i < weekdays_size; i++) {
			for (int j = 0; j < hourslots_size; j++) {
				if (schedule[i][j].size() <= num) {
					cells.add(new Cell(i, j));
				}
			}
		}
		return cells;
	}
	
	TreeSet<String> getCell(Cell c) {
		return schedule[c.getWeekday()][c.getHourslot()];
	}
	
	TreeSet<String> getCell(int weekday, int hourslot) {
		if (!isValidWeekday(weekday) || !isValidHourslot(hourslot)) {
			return null;
		}
		return schedule[weekday][hourslot];
	}
	
	public Cell getAccompanyingCell(String name, Cell cell) {
		TreeSet<String> ts = getCell(cell.getWeekday(), cell.getHourslot()-1);
		if (ts != null && ts.contains(name)) {
			return new Cell(cell.getWeekday(), cell.getHourslot()-1);
		}
		
		ts = getCell(cell.getWeekday(), cell.getHourslot()+1);
		if (ts != null && ts.contains(name)) {
			return new Cell(cell.getWeekday(), cell.getHourslot()+1);
		}
		
		return null;
	}
	
	/*
	 * Do not schedule during these times. The students have either classes or other
	 * obligations that prevent them from tabling.
	 */
	void addDNS(String name, int weekday, int hourslot) {
		if (!isValidWeekday(weekday) || !isValidHourslot(hourslot)) {
			// Allow bad input but don't do anything, so the table can be populated easier
			return;
		}
		
		/*
		 * An easy way to fix the mistake of assign tabling when they have class.
		 * Since the parser reads the schedules sequentially, it does not know if the 
		 * student will have class during a specific slot, so it removes it if the student
		 * eventually does.
		 */
		
		if (schedule[weekday][hourslot].contains(name)) {
			schedule[weekday][hourslot].remove(name);
		}
		donotSchedule[weekday][hourslot].add(name);
	}
	
	/* 
	 * Adds tabling spots indiscriminately. Assigns tabling one hour after the student's
	 * class finishes. addDNS() will correct the potentially tabling spot if the student
	 * has class.
	 */
	void addName(String name, int weekday, int hourslot) {
		if (!isValidWeekday(weekday) || !isValidHourslot(hourslot)) {
			// Allow bad input but don't do anything, so the table can be populated easier
			return;
		}

		if (!donotSchedule[weekday][hourslot].contains(name)) {
			schedule[weekday][hourslot].add(name);
		}
	}
	
	void addName(String name, Cell cell) {
		addName(name, cell.getWeekday(), cell.getHourslot());
	}
	
	void removeName(String name, int weekday, int hourslot) {
		if (!isValidWeekday(weekday) || !isValidHourslot(hourslot)) {
			return;
		}
		schedule[weekday][hourslot].remove(name);
	}
	
	void removeName(String name, Cell cell) {
		removeName(name, cell.getWeekday(), cell.getHourslot());
	}
	
	boolean isValidWeekday(int weekday) {
		return weekday >= 0 && weekday < weekdays_size;
	}
	
	boolean isValidHourslot(int hourslot) {
		return hourslot >=0 && hourslot < hourslots_size;
	}
	
	boolean checkSchedule() {
		for (int i = 0; i < weekdays_size; i++) {
			for (int j = 0; j < hourslots_size; j++) {
				if (schedule[i][j].size() < 2) {
					return false;
				}
			}
		}
		return true;
	}
	
	/*
	 * Prints out the names followed by (25-name.length()) spaces, since tabs do not
	 * work well with varying sized names.
	 */
	void printXspaces(String name) {
		int num = name.length();
		System.out.print(name);
		for (int i = 0; i < 25-num; i++) {
			System.out.print(" ");
		}
	}
	
	void printXspaces() {
		for (int i = 0; i < 25; i++) {
			System.out.print(" ");
		}
	}
	
	/*
	 * Crappy design using five iterators and iterating one at a time while printing the
	 * names out. At least the size of the HashSet does not need to be remembered.
	 * An alternate implementation is using JTable, which would also use a GUI that
	 * could be modified in real time.
	 * 
	 * http://stackoverflow.com/questions/8621873/how-to-print-a-2d-array-with-varying-sized-cells
	 */
	void printRow(int rownumber) {
		if (!isValidHourslot(rownumber)) {
			System.err.println("Wrong rownumber: " + rownumber);
		}
		
		Iterator<String> iter0 = schedule[0][rownumber].iterator();
		Iterator<String> iter1 = schedule[1][rownumber].iterator();
		Iterator<String> iter2 = schedule[2][rownumber].iterator();
		Iterator<String> iter3 = schedule[3][rownumber].iterator();
		Iterator<String> iter4 = schedule[4][rownumber].iterator();

		while (iter0.hasNext() || iter1.hasNext() || 
				iter2.hasNext() || iter3.hasNext() || 
				iter4.hasNext()) {
			System.out.print("\t");
			if (iter0.hasNext()) {
				printXspaces(iter0.next());
			} else {
				printXspaces();
			}
			if (iter1.hasNext()) {
				printXspaces(iter1.next());
			} else {
				printXspaces();
			}
			if (iter2.hasNext()) {
				printXspaces(iter2.next());
			} else {
				printXspaces();
			}
			if (iter3.hasNext()) {
				printXspaces(iter3.next());
			} else {
				printXspaces();
			}
			if (iter4.hasNext()) {
				printXspaces(iter4.next());
			} else {
				printXspaces();
			}
			System.out.println();
		}
		System.out.println();
	}
	
	void print() {
		System.out.print("\t");
		printXspaces("MONDAY");
		printXspaces("TUESDAY");
		printXspaces("WEDNESDAY");
		printXspaces("THURSDAY");
		printXspaces("FRIDAY");
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
