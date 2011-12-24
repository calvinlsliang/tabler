package tabler;

public enum Day {
	
	MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY;
	
	static int value(Day d) {
		switch(d) {
			case MONDAY: return 0;
			case TUESDAY: return 1;
			case WEDNESDAY: return 2;
			case THURSDAY: return 3;
			case FRIDAY: return 4;
		}
		return -1;
	}
	
	static Day parseDay(char weekday, int pos) {
		switch(weekday) {
		case 'M':
			return Day.MONDAY;
		case 'T':
			if (weekday == 1) { // Tuesday
				return Day.TUESDAY;
			} else { // Thursday
				return Day.THURSDAY;
			}
		case 'W':
			return Day.WEDNESDAY;
		case 'F':
			return Day.FRIDAY;
		}
		return null;
	}
	
}
