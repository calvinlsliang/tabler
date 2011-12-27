package tabler;

/*
 * @author Calvin Liang <calvinlsliang@gmail.com>
 */
public class Cell {

	int weekday;
	int hourslot;
	
	Cell() {
		weekday = -1;
		hourslot = -1;
	}
	
	Cell(int weekday, int hourslot) {
		this.weekday = weekday;
		this.hourslot = hourslot;
	}
	
	int getWeekday() {
		return weekday;
	}
	
	int getHourslot() {
		return hourslot;
	}
	
	String print() {
		return "\t" + weekday + " " + hourslot;
	}
}
