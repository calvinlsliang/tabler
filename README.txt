ABOUT
Contact Calvin Liang at calvinlsliang@gmail.com

VERSION HISTORY
0.1
	- Basic tabling functionality completed
	- Blacklisting

0.2
	- Whitelisting
	
WIP
	- Duplicate tabling spots

TODO
	- Vacation days

DESCRIPTION
Automated tabling schedule for Phi Beta Lambda tabling.

Parses every student's telebears schedule into a computer-friendly format, and from there
chooses students based on their availability.

HOW TO USE
Depending on the Type that you specify when you parse the document, the tabler will do something different.

Type.NORMAL
The schedule in a generic format, followed as such:

	Name
	<course ID> <class information> <days available> <times available> <rest of class information>
	...
	<course ID> <class information> <days available> <times available> <rest of class information>
	
The tabler will check the first character of each line. If it's an alphabet character, then the tabler assumes 
it's a new entry and will remember the name. Otherwise it will assume that the tabler is looking at classes the
student is in, and will parse the dates available and times available and assign tabling based on that.

Type.WHITELIST
The schedule will be in the format as such:

	Name
	<days available> <times available>
	...
	<days available> <times available>
	
The whitelist is used for students who specifically request a tabling time, usually due to particular gaps in classes
or the need for extra tabling.

Type.BLACKLIST
The schedule will be in the format as such:

	Name
	<days available> <times available>
	...
	<days available> <times available>
	
The blacklist is used for students who specifically cannot table during that time, usually due to lunch.


Type.DUPLICATES
The schedule will be in the format as such:

	Name
	<number>
	
The duplicates list will be used for students who need to table multiple time, usually due to the first weeks of school
or to make up tabling.
	
