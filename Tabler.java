package tabler;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tabler {
	
	int NUM_WEEKDAYS = 5; // Monday, Tuesday, ..., Friday = 5 weekdays
	int NUM_HOURSLOTS = 10; //10, 10:30, ..., 3 = 10 slots
	Schedule schedule = new Schedule(NUM_WEEKDAYS, NUM_HOURSLOTS);

	private void populateSchedule(String name, Day d, Time time) {
		for (int i = time.getStartTime()-2; i < time.getEndTime(); i++) {
//			System.out.println(name + " " + i + " " + time.getEndTime() + " ");
			schedule.addDNS(name, Day.value(d), i);
		}
		
//		int startTime = time.getStartTime();
//		if (startTime >= 2 && startTime <= 9) { // Only need to check from 11-3pm
//			schedule.addDNS(name, Day.value(d), startTime-1);
//			schedule.addDNS(name, Day.value(d), startTime-2);
//		}
		
		
		int endTime = time.getEndTime();
		if (endTime >= 0 && endTime <= 8) { // Only need to check from 10-2pm
			schedule.addName(name, Day.value(d), endTime);
			schedule.addName(name, Day.value(d), endTime+1);
		}
	}
		
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
//			System.out.println(name + " " + weekdays + " " + time);
		}
		
	}
	
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
		t.parseFile("src/tabler/data1.txt");
		
//		Time time = new Time("1100 -1230 PM");
//		time.print();
		
		t.print();
		
	}
}
