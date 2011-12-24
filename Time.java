package tabler;

public class Time {
	
	int startTime;
	int endTime;
	String period;
	
	Time() {
		startTime = 0;
		endTime = 0;
		period = null;
	}
	
	/* 
	 * String format: "1100 -1230 PM"
	 * 
	 * Since Bearfacts schedules do not tell the period of the starting time, theoretically given a time 
	 * period such as 0600 - 0800 PM could very well be from 6 am - 8 pm, but since Berkeley professors 
	 * are not that crazy and would schedule a 14-hour course, I made the assumption that if the difference 
	 * was larger than 1200 (or a 12-hour period), then I would just add 1200 to the starting time.
	 * 
	 * So in the case of 0600 - 0800 PM, I would first convert the end time to military (2000) and find the
	 * difference between the end and starting time (2000-600=1400 > 1200), and I would see that the difference
	 * is larger than 1200, so I would add 1200 to the starting time making the range 1800-2000, which is
	 * correct.
	 */
	Time(String s) {
		String[] times = s.split(" ");
		int start_int = Integer.parseInt(times[0]);
		int end_int = Integer.parseInt(times[1].substring(1, times[1].length()));
				
		if (times[2].compareTo("PM") == 0 && end_int < 1200) { // 1200-2400 Military time
			end_int += 1200;
		}
		
		if (end_int - start_int > 1200) {
			start_int += 1200;
		}
		
		startTime = start_int;
		endTime = end_int;
		period = times[2];
	}

	int getStartTime() {
		return Time.value(startTime);
	}
	
	int getEndTime() {
		return Time.value(endTime);
	}
	
	static int value(int time) {		
		switch(time) {
			case 1000:
				return 0;
			case 1030:
				return 1;
			case 1100:
				return 2;
			case 1130:
				return 3;
			case 1200:
				return 4;
			case 1230:
				return 5;
			case 1300:
				return 6;
			case 1330:
				return 7;
			case 1400:
				return 8;
			case 1430:
				return 9;				
			default:
				if (time < 1000) {
					return -1;
				}
				return 9;
		}
	}
	
	void print() {
		System.out.println(startTime + " / " + endTime + " / " + period);
	}

}
