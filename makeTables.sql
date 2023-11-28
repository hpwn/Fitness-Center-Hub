CREATE TABLE member (
	memberID INT PRIMARY KEY, -- member ID
	fname VARCHAR2(100),     -- first name
	lname VARCHAR2(100),     -- last name
	phonenum VARCHAR2(15),             -- phone number
	memlevel VARCHAR2(50),   -- the membership level they bought
	totalspent NUMBER(*,2) -- total amount a member has spent
);

CREATE TABLE course (
	courseID INT PRIMARY KEY, -- the course ID
	courseName VARCHAR(100), -- name of class
	time INT,                -- the start time
	sdate DATE,              -- the start date
	edate DATE        	 -- the end date
);

CREATE TABLE trainer (
	trainerID INT PRIMARY KEY, --trainer ID
	trainername VARCHAR2(100), -- name of trainer
	phonenum INT                -- phone number cause why not
);

CREATE TABLE trainClass (
	trainerID INT, -- trainer ID
	classID INT   -- class ID
);

CREATE TABLE item (
	itemID INT PRIMARY KEY, -- uniquely identify item
	memberID INT,           -- the member who is checking out the item
	checkin INT,           -- check in time of item
	checkout INT,          -- check out time of an item
	qty INT,               -- qty checked out
	lost INT              -- marks if equipment is lost	
);

CREATE TABLE transaction (
	transID INT PRIMARY KEY,  -- the id of the transaction
	amount INT,              --total amount spent in transaction
	transdate DATE,          -- date of the transaction
	transtype VARCHAR2(100)   -- the type of transaction
);

CREATE TABLE coursePackage (
        packagenum INT PRIMARY KEY, -- the ID of the package
        packagename VARCHAR2(100),  -- the name of the package
        firstclassID INT,           --the ID of the first class in the package
        secondclassID INT	    -- the ID of the second class in the package
);
