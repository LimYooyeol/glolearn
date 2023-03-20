#CREATE DATABASE IF NOT EXISTS glolearn_dev DEFAULT CHARACTER SET UTF8MB4;

use glolearn_dev;

DROP TABLE IF EXISTS COMMENTS;
DROP TABLE IF EXISTS ENROLLMENT;
DROP TABLE IF EXISTS LECTURE;
DROP TABLE IF EXISTS CONTENTS;
DROP TABLE IF EXISTS COURSE;
DROP TABLE IF EXISTS INTRODUCTION;
DROP TABLE IF EXISTS AUTH_INFO;
DROP TABLE IF EXISTS MEMBERS;


CREATE TABLE MEMBERS(
	member_id		BIGINT			PRIMARY KEY AUTO_INCREMENT,
    oauth_id		VARCHAR(255)	NOT NULL,
    oauth_domain	VARCHAR(255)	NOT NULL,
    nickname		VARCHAR(50)		NOT NULL UNIQUE,
    UNIQUE(oauth_id, oauth_domain)
);

CREATE TABLE AUTH_INFO(
	token_id		CHAR(36)		PRIMARY KEY,
    member_id		BIGINT			NOT NULL,
    expire_date		DATETIME		NOT NULL,
    FOREIGN KEY	(member_id)	REFERENCES MEMBERS(member_id) ON UPDATE CASCADE ON DELETE CASCADE
);

# separated from COURSE(rarely accessed)
CREATE TABLE INTRODUCTION(
	introduction_id		BIGINT			PRIMARY KEY	AUTO_INCREMENT,
    introduction		VARCHAR(10000)	NOT NULL DEFAULT ''
);

CREATE TABLE COURSE(
	course_id			BIGINT			PRIMARY KEY AUTO_INCREMENT,
    lecturer			BIGINT			NOT NULL,
    title				VARCHAR(100)	NOT NULL,
    introduction_id		BIGINT			NOT NULL,
    reg_date			DATETIME		NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_published		BOOLEAN			NOT NULL DEFAULT FALSE,
	category			VARCHAR(100)	NOT NULL,
    cover				VARCHAR(255)	NOT NULL,
    num_student			BIGINT			NOT NULL DEFAULT 0,
    price				BIGINT			,
    FOREIGN KEY	(lecturer) REFERENCES MEMBERS(member_id)	ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY	(introduction_id)	REFERENCES INTRODUCTION(introduction_id)
);

#seperated from lecture
CREATE TABLE CONTENTS(
	contents_id		BIGINT			PRIMARY KEY AUTO_INCREMENT,
    contents		VARCHAR(10000)	NOT NULL DEFAULT ''
);

CREATE TABLE LECTURE(
	lecture_id			BIGINT			PRIMARY KEY AUTO_INCREMENT,
    course_id			BIGINT			NOT NULL,
    title				VARCHAR(100)	NOT NULL,
    contents_id			BIGINT			NOT NULL,  
    reg_date			DATETIME		NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_update_date	DATETIME		NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY	(course_id) REFERENCES COURSE(course_id)	ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (contents_id)	REFERENCES	CONTENTS(contents_id)
);

CREATE TABLE ENROLLMENT(
	enrollment_id		BIGINT		PRIMARY KEY AUTO_INCREMENT,
	member_id			BIGINT		NOT NULL,
	course_id			BIGINT		NOT NULL,
	enroll_date			DATETIME	NOT NULL DEFAULT CURRENT_TIMESTAMP,
	order_id			CHAR(36)	NOT NULL,
	FOREIGN KEY	(member_id)	REFERENCES MEMBERS(member_id)	ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (course_id) REFERENCES COURSE(course_id)	ON UPDATE CASCADE ON DELETE CASCADE,
	UNIQUE (member_id, course_id)
);

CREATE TABLE COMMENTS(
	comment_id			BIGINT		PRIMARY KEY AUTO_INCREMENT,
    member_id			BIGINT		NOT NULL,
    lecture_id			BIGINT		NOT NULL,
    reg_date			DATETIME	NOT NULL DEFAULT CURRENT_TIMESTAMP,
    root_id				BIGINT		,
    contents			VARCHAR(300),	
	FOREIGN KEY	(member_id)	REFERENCES MEMBERS(member_id)	ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (lecture_id) REFERENCES LECTURE(lecture_id)	ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY	(root_id)	REFERENCES COMMENTS(comment_id)	ON UPDATE CASCADE ON DELETE CASCADE
);
