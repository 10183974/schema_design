create table if not exists Z (
	ID INTEGER not null,
	UserName VARCHAR not null,
	f.Address VARCHAR,
	f.AccBal DECIMAL,
	f.Comment VARCHAR,
	constraint pk primary key (ID UserName )
);
create table if not exists X (
	IP INTEGER not null,
	f.Message VARCHAR,
	constraint pk primary key (IP )
);

