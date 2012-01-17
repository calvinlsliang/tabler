package tabler;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * @author Calvin Liang <calvinlsliang@gmail.com>
 */
public class Tabler {
	
	int NUM_WEEKDAYS = 5; // Monday, Tuesday, ..., Friday = 5 weekdays
	int NUM_HOURSLOTS = 10; //10am, 10:30am, ..., 3 = 10 slots
	Schedule schedule = new Schedule(NUM_WEEKDAYS, NUM_HOURSLOTS);
	Schedule finalSchedule = new Schedule(NUM_WEEKDAYS, NUM_HOURSLOTS);
	HashSet<String> studentNames = new HashSet<String>();
	
	// Chosen students are put into a hash so they do not get chosen again
	HashSet<String> chosenStudents = new HashSet<String>();
	
	// Hashtable with K: students, V: the two cells they are placed in
	Hashtable<String, Cell[]> chosenStudentsHash = new Hashtable<String, Cell[]>();
	

	enum Type {
		NORMAL, BLACKLIST, WHITELIST
	}
	
	/*
	 * Since Telebear schedules start with an ID, can only safely check the first character to
	 * see if it is a character. If so, it's a name and should remember it until the next
	 * character is read, meaning the next schedule is being read.
	 */
	private void parseFile(String filename, Type type) {
		try {
			FileInputStream fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String str;
			String name = null;
			while ((str = br.readLine()) != null) {
				if (str.compareTo("") == 0) {
					name = null;
				} else if (java.lang.Character.isLetter(str.charAt(0)) &&
						name == null) { // Remember thy name
					name = str;
				} else {
					parseSchedule(name, str, type);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* 
	 * Parses schedules pass in the form of:
	 * NAME [COMMITTEE]
	 * 05253	ASAMST R2A	 01 LEC	4	 Letter Grade	-T-T-	 1100 -1230 PM	0151 BARROWS	 Enrolled 
	 * ...
	 * 
	 * Regex finds the dates and times and passes them to populateSchedule()
	 */
	private void parseSchedule(String name, String schedule, Type type) {
		if (name == null) {
			return;
		}
				
		Pattern day_patt = Pattern.compile("[M-][T-][W-][T-][F-]");
		Matcher day_m = day_patt.matcher(schedule);
		
		Pattern time_patt = Pattern.compile("\\d{4} -\\d{4} [AP]M");
		Matcher time_m = time_patt.matcher(schedule);
		
		while (day_m.find() && time_m.find()) {
			String weekdays = day_m.group();
			String time = time_m.group();
			for (int i = 0; i < NUM_WEEKDAYS; i++) {
				char weekday = weekdays.charAt(i);
				if (weekday != '-') {
					populateSchedule(name, Day.parseDay(weekday, i), new Time(time), type);
				}
			}
		}
	}
	
	/*
	 * Populates the schedule. Two rules:
	 * 	1) Doesn't schedule tabling when you have classes
	 *  2) Schedules tabling for an hour slot after your class ends
	 *  	2a) Corrects itself if you stack classes one after another
	 *  
	 */
	private void populateSchedule(String name, Day d, Time time, Type type) {
		int startTime = time.getStartTime();
		int endTime = time.getEndTime();

		if (type == Type.NORMAL) {
			for (int i = startTime; i < endTime; i++) {
//				schedule.addFinalName(name, Day.value(d), i); //Populates with regular schedule
				schedule.addDNS(name, Day.value(d), i);
			}	
			
			/*
			 * Skips if only adding blacklisted people to the doNotSchedule schedule.
			 */
			if (endTime >= 0 && endTime <= 8) { // Only need to check from 10-2pm
				studentNames.add(name); // Adds valid students to the studentNames set
				schedule.addName(name, Day.value(d), endTime);
			}
		} else if (type == Type.BLACKLIST){
			for (int i = startTime; i < endTime; i++) {
				schedule.addDNS(name, Day.value(d), i);
			}	
		} else if (type == Type.WHITELIST) {
			chosenStudents.add(name);
			for (int i = time.getStartTime(); i < time.getEndTime(); i++) {
				finalSchedule.addFinalName(name, new Cell(Day.value(d), i));
			}
			// Don't add to the chosenStudentsHash so they can't get removed
			
		}
	}
	
	/*
	 * First picks students from the problem cells (currently set to only 6 or less available
	 * students at any current cell). If there exists a case where there's only one available
	 * student at a slot, it will randomly choose one of the already-selected students and "unselect"
	 * him or her and add that student to table at the current time. It will continue until
	 * all the problem cells are satisfied. Afterwards, it iteratively checks each tabling slot
	 * and picks any available student at that time and adds to the slot.
	 */
	private void pickStudents() {
		if (!schedule.checkSchedule()) {
			System.err.println("There exists inadequate tabling spots!");
			return;
		}		
		
		// Initial problem cells that must be looked at first
		HashSet<Cell> problemCells = schedule.getProblemCells(6);
		
		// Vectors that adjust the problemCells depending on constraints
		Vector<Cell> toAdd = new Vector<Cell>();
		Vector<Cell> toRemove = new Vector<Cell>();
		
		/*
		 * Work on problem cells first
		 * problemCells will dynamically change size as they are removed
		 */
		while (problemCells.size() > 0) {
			Iterator<Cell> iter = problemCells.iterator();
			while (iter.hasNext()) {
				Cell cell = iter.next();
				TreeSet<String> listOfStudents = finalSchedule.getCell(cell);
				if (listOfStudents.size() < 2) {
					Iterator<String> iter2 = schedule.getCell(cell).iterator();
					boolean foundOne = false;
					while (iter2.hasNext()) {
						String name = iter2.next();
						if (!chosenStudents.contains(name)) {
							chosenStudents.add(name);
							Cell[] cells = new Cell[2];
							cells[0] = cell;
							Cell secondCell = schedule.getAccompanyingCell(name, cell);
							cells[1] = secondCell;
							chosenStudentsHash.put(name, cells);
							finalSchedule.addFinalName(name, cell);
							finalSchedule.addFinalName(name, secondCell);
							foundOne = true;
							break;
						}
					}
					
					/*
					 * If no suitable student was found, pick a random student to add and
					 * remove that student from his original cell. Hopefully it will the cells
					 * will correct themselves over time?
					 */
					if (foundOne == false) {
						int nameNum = new Random().nextInt(schedule.getCell(cell).size());
						int i = 0;
						String name = null;
						for (String s : schedule.getCell(cell)) {
							if (i == nameNum) {
								name = s;
							}
							i++;
						}
						Cell[] oldCells = chosenStudentsHash.get(name);
						if (oldCells != null) {
							finalSchedule.removeName(name, oldCells[0]);
							finalSchedule.removeName(name, oldCells[1]);
							chosenStudentsHash.remove(name);
							
							finalSchedule.addFinalName(name, cell);
							Cell secondCell = schedule.getAccompanyingCell(name, cell);
							finalSchedule.addFinalName(name, secondCell);
	
							Cell[] cells = new Cell[2];
							cells[0] = cell;
							cells[1] = secondCell;
							chosenStudentsHash.put(name, cells);
							toAdd.add(oldCells[0]);
							toAdd.add(oldCells[1]);
						}
					}
				} else {
					// Change the size of the number of problem cells
					toRemove.add(cell);
				}
			}
			
			Iterator<Cell> iter3 = toRemove.iterator();
			while (iter3.hasNext()) {
				problemCells.remove(iter3.next());
			}
			toRemove.clear();
			
			iter3 = toAdd.iterator();
			while (iter3.hasNext()) {
				problemCells.add(iter3.next());
			}
			toAdd.clear();
		}
		
		/*
		 * Then tackle the rest of the cells, making sure they all have the same number
		 * or if they're already added to the finished set.
		 * 
		 * Very similar code to the segment above. Don't kill me DRY principle.
		 */
		int numStudents = getNumberOfStudents();
		problemCells = schedule.getAllCells();
		while (chosenStudents.size() != numStudents) {
//			finalSchedule.print();
//			System.out.println(chosenStudents.size() + " " + numStudents);
//			try {
//			Thread.sleep(5000);
//			} catch (Exception e) {
//				
//			}
			Iterator<Cell> iter = problemCells.iterator();
			while (iter.hasNext()) {
				Cell cell = iter.next();
				TreeSet<String> listOfStudents = finalSchedule.getCell(cell);
				if (listOfStudents.size() < 2) {
					Iterator<String> iter2 = schedule.getCell(cell).iterator();
					boolean foundOne = false;
					while (iter2.hasNext()) {
						String name = iter2.next();
						if (!chosenStudents.contains(name)) {
							chosenStudents.add(name);
							Cell[] cells = new Cell[2];
							cells[0] = cell;
							Cell secondCell = schedule.getAccompanyingCell(name, cell);
							cells[1] = secondCell;
							chosenStudentsHash.put(name, cells);
							finalSchedule.addFinalName(name, cell);
							finalSchedule.addFinalName(name, secondCell);
							foundOne = true;
							break;
						}
					}
					
					/*
					 * If no suitable student was found, pick a random student to add and
					 * remove that student from his original cell. Hopefully it will the cells
					 * will correct themselves over time?
					 */
					if (foundOne == false) {
						int nameNum = new Random().nextInt(schedule.getCell(cell).size());
						int i = 0;
						String name = null;
						for (String s : schedule.getCell(cell)) {
							if (i == nameNum) {
								name = s;
							}
							i++;
						}
						Cell[] oldCells = chosenStudentsHash.get(name);
						if (oldCells != null) {
							finalSchedule.removeName(name, oldCells[0]);
							finalSchedule.removeName(name, oldCells[1]);
							
							finalSchedule.addFinalName(name, cell);
							Cell secondCell = schedule.getAccompanyingCell(name, cell);
							finalSchedule.addFinalName(name, secondCell);
	
							Cell[] cells = new Cell[2];
							cells[0] = cell;
							cells[1] = secondCell;
							chosenStudentsHash.put(name, cells);
							toAdd.add(oldCells[0]);
							toAdd.add(oldCells[1]);
						}
					}
				} else {
					/*
					 * Just add whenever there's an opening. Don't care.
					 */
					Iterator<String> iter2 = schedule.getCell(cell).iterator();
					while (iter2.hasNext()) {
						String name = iter2.next();
						if (!chosenStudents.contains(name)) {
							chosenStudents.add(name);
							Cell[] cells = new Cell[2];
							cells[0] = cell;
							Cell secondCell = schedule.getAccompanyingCell(name, cell);
							cells[1] = secondCell;
							chosenStudentsHash.put(name, cells);
							finalSchedule.addFinalName(name, cell);
							finalSchedule.addFinalName(name, secondCell);
							break;
						}
					}
				}
			}
			
			Iterator<Cell> iter3 = toRemove.iterator();
			while (iter3.hasNext()) {
				problemCells.remove(iter3.next());
			}
			toRemove.clear();
			
			iter3 = toAdd.iterator();
			while (iter3.hasNext()) {
				problemCells.add(iter3.next());
			}
			toAdd.clear();
		}
		return;
	}
	
	/*
	 * Useful if you want to find any discrepancies between the total student set
	 * and your current set.
	 */
	@SuppressWarnings("unused")
	private void difference(HashSet<String> big, HashSet<String> small) {
		HashSet<String> difference = new HashSet<String>(big);
		difference.removeAll(small);
		Iterator<String> iter = difference.iterator();
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
	}
	
	private int getNumberOfStudents() {
		return studentNames.size();
	}
	
	void print() {
		finalSchedule.print();
	}

	public static void main(String args[]) {
		Tabler t = new Tabler();
		t.parseFile("../data_full.txt", Type.NORMAL);
		t.parseFile("../blacklist.txt", Type.BLACKLIST);
		t.parseFile("../whitelist.txt", Type.WHITELIST);
		t.pickStudents();
		t.print();
	}
}
