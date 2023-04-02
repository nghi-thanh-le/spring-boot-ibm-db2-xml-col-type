CREATE TABLE Companies (
id int NOT NULL,
name varchar(255) DEFAULT '',
url varchar(255) DEFAULT '',
email varchar(255) DEFAULT '',
address varchar(255) DEFAULT '',
postalCode varchar(255) DEFAULT '',
postOffice varchar(255) DEFAULT '',
country varchar(255) DEFAULT '',
contactPerson varchar(255) DEFAULT '',
phone varchar(255) DEFAULT '',
PRIMARY KEY (id)
);

ALTER TABLE Companies ALTER COLUMN id SET generated always as identity;

CREATE TABLE Users (
id int NOT NULL,
username varchar(255) NOT NULL,
password varchar(255) NOT NULL,
firstname varchar(255),
lastname varchar(255),
email varchar(255),
phone varchar(255),
companyId int,
PRIMARY KEY (id),
FOREIGN KEY (companyId)
REFERENCES Companies(id)
);

INSERT INTO Users VALUES
(1,'admin','$2a$10$fiwEhBD7rbJBJi/8232xZOaq1g1k0pjatkvDFYy1oyZcLMhgUmUsC','Admin','Admin','admin@email.com','',1);

ALTER TABLE Users ALTER COLUMN id SET generated always as identity;

CREATE TABLE Roles (
id int NOT NULL,
roleName varchar(255),
roleDescription varchar(255),
PRIMARY KEY (id)
);

INSERT INTO Roles VALUES
(1,'Admin','Admin user');

ALTER TABLE Roles ALTER COLUMN id SET generated always as identity;

CREATE TABLE Join_User_Role (
id int not null generated always as identity,
userId int,
roleId int,
PRIMARY KEY (id),
FOREIGN KEY (userId)
REFERENCES Users(id),
FOREIGN KEY (roleId)
REFERENCES Roles(id)
);

INSERT INTO Join_User_Role(userId, roleId) VALUES
(1,1);

CREATE TABLE Refresh_Tokens (
id int not null generated always as identity,
userId int not null,
token varchar(255) not null,
expiryDate TIMESTAMP NOT NULL,
PRIMARY KEY (id),
FOREIGN KEY (userId)
    REFERENCES Users(id)
);

CREATE TABLE Standard_Bodies (
 id int NOT NULL,
 bodyId varchar(255),
 bodyName varchar(255),
 bodyUrl varchar(255),
 bodyDescription clob,
 PRIMARY KEY (id)
);

ALTER TABLE Standard_Bodies ALTER COLUMN id SET generated always as identity;

CREATE TABLE Standards (
id int NOT NULL,
stdId varchar(255),
stdCode varchar(255),
stdName varchar(255),
stdDescription clob,
stdUrl varchar(255),
bodyId int,
PRIMARY KEY (id),
FOREIGN KEY (bodyId)
 REFERENCES Standard_Bodies(id)
);

ALTER TABLE Standards ALTER COLUMN id SET generated always as identity;


CREATE TABLE RD_Stages (
id varchar(255) not null,
description varchar(255) not null,
primary key (id)
);

CREATE TABLE RDs (
id varchar(150) not null,
fileName varchar(255) not null,
content xml not null,
userId int not null,
stageId varchar(255) not null,
isDeprecated boolean not null,
primary key (id),
FOREIGN KEY (userId)
    REFERENCES Users(id),
FOREIGN KEY (stageId)
    REFERENCES RD_Stages(id)
);

CREATE TABLE RD_Supplementary_Files (
id int not null generated always as identity,
fileName varchar(255) not null,
content blob not null,
resourceId varchar(150) not null,
primary key (id),
foreign key (resourceId)
    references RDs(id)
);

ALTER TABLE RD_Supplementary_Files ALTER COLUMN CONTENT SET DATA TYPE BLOB(100M);

CREATE TABLE RD_Reviews (
id varchar(150) not null,
userId int not null,
review CLOB not null,
reviewAt TIMESTAMP NOT NULL
DEFAULT CURRENT_TIMESTAMP,
primary key (id),
FOREIGN KEY (userId)
    REFERENCES Users(id)
);

CREATE TABLE RD_Validation_Results (
id varchar(150) not null,
state varchar(150) not null,
validationMessage CLOB not null,
primary key (id)
);