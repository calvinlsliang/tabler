package tabler;

import java.util.LinkedHashSet;
import java.util.Set;

public class Timeslot {
	String time;
	Day day;
	Set<String> names;
	
	Timeslot() {
		time = null;
		day = null;
		names = new LinkedHashSet<String>();
	}
	
	void setTime(String s) {
		time = s;
	}
	void setDay(Day d) {
		day = d;
	}
	
	void addName(String s) {
		if (s != null) {
			names.add(s);
		}
	}
}
