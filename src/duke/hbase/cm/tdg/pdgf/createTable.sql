create table if not exists Z (
	ID INTEGER not null,
	UserName VARCHAR not null,
	_0.Address VARCHAR,
	_0.AccBal DECIMAL,
	_0.Comment VARCHAR,
	constraint pk primary key (ID UserName )
);
create table if not exists X (
	IP INTEGER not null,
	_0.Message VARCHAR,
	constraint pk primary key (IP )
);

