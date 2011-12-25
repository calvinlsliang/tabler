package tabler;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * @author Calvin Liang <calvinlsliang@gmail.com>
 */
public class Tabler {
	
	int NUM_WEEKDAYS = 5; // Monday, Tuesday, ..., Friday = 5 weekdays
	int NUM_HOURSLOTS = 10; //10, 10:30, ..., 3 = 10 slots
	Schedule schedule = new Schedule(NUM_WEEKDAYS, NUM_HOURSLOTS);

	private void pickStudents() {
		HashSet<String> students = new HashSet<String>();
		
	}
	
	
	/*
	 * Populates the schedule. Two rules:
	 * 	1) Doesn't schedule tabling when you have classes
	 *  2) Schedules tabling for an hour slot after your class ends
	 *  	2a) Corrects itself if you stack classes one after another
	 *  
	 *  Took out:
	 *  3) Doesn't table if it's an hour slot in between classes. Some people
	 *  	don't care about tabling in between that section, and can always be
	 *  	added to the blacklist if they are troublesome.
	 *  	Fix: Change the for-loop to iterate from startTime-2 to endTime.
	 */
	private void populateSchedule(String name, Day d, Time time) {
		int startTime = time.getStartTime();
		int endTime = time.getEndTime();

		for (int i = startTime; i < endTime; i++) {
//			schedule.addName(name, Day.value(d), i); // Populates with regular schedule
			schedule.addDNS(name, Day.value(d), i);
		}	
		
		// Adds potential tablers to the schedule
		if (endTime >= 0 && endTime <= 8) { // Only need to check from 10-2pm
			schedule.addName(name, Day.value(d), endTime);
			schedule.addName(name, Day.value(d), endTime+1);
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
	private void parseSchedule(String name, String schedule) {
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
					populateSchedule(name, Day.parseDay(weekday, i), new Time(time));
				}
			}
		}
	}
	
	/*
	 * Since Telebear schedules start with an ID, can safely check the first character to
	 * see if it is a character. If so, it's a name and should remember it until the next
	 * character is read, meaning the next schedule is being read.
	 */
	private void parseFile(String filename) {
		try {
			FileInputStream fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String str;
			String name = null;
			while ((str = br.readLine()) != null) {
				if (str.compareTo("") == 0) {
				} else if (java.lang.Character.isLetter(str.charAt(0))) { // Remember thy name
					name = str;
				} else {
					parseSchedule(name, str);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void print() {
//		schedule.printRow(0);
		schedule.print();
	}

	public static void main(String args[]) {
		Tabler t = new Tabler();
		t.parseFile("src/tabler/data_full.txt");
		t.pickStudents();
		t.print();
	}
}
