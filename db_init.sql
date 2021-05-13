# 데이터베이스 생성
DROP DATABASE IF EXISTS derived_resources;
CREATE DATABASE derived_resources;
USE derived_resources;

# derivedRequest 테이블 추가
# http://127.0.0.1:8022/img?maxWidth=100&url=https://i.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI
CREATE TABLE derivedRequest (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, # 번호
	regDate DATETIME DEFAULT NULL, # 작성일
	updateDate DATETIME DEFAULT NULL, # 갱신일
	url CHAR(200) UNIQUE NOT NULL,
	originUrl CHAR(200) UNIQUE NOT NULL,
	width SMALLINT(10) UNSIGNED NOT NULL,
	height SMALLINT(10) UNSIGNED NOT NULL,
	maxWidth SMALLINT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (id)
);

# 파일 테이블 추가
CREATE TABLE genFile (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, # 번호
	regDate DATETIME DEFAULT NULL, # 작성일
	updateDate DATETIME DEFAULT NULL, # 갱신일
	delDate DATETIME DEFAULT NULL, # 삭제일
	delStatus TINYINT(1) UNSIGNED NOT NULL DEFAULT 0, # 삭제상태(0:미삭제,1:삭제)
	relTypeCode CHAR(50) NOT NULL, # 관련 데이터 타입(article, member)
	relId INT(10) UNSIGNED NOT NULL, # 관련 데이터 번호
	originFileName VARCHAR(100) NOT NULL, # 업로드 당시의 파일명
	fileExt CHAR(10) NOT NULL, # 확장자
	typeCode CHAR(20) NOT NULL, # 종류코드 (common)
	type2Code CHAR(20) NOT NULL, # 종류2코드 (attachment)
	fileSize INT(10) UNSIGNED NOT NULL, # 파일 사이즈
	fileExtTypeCode CHAR(10) NOT NULL, # 파일규격코드(img, video)
	fileExtType2Code CHAR(10) NOT NULL, # 파일규격2코드(jpg, mp4)
	fileNo SMALLINT(2) UNSIGNED NOT NULL, # 파일번호 (1) - 다중파일 업로드시 몇번째 파일인지
	fileDir CHAR(20) NOT NULL, # 파일이 저장되는 폴더명
	PRIMARY KEY (id),
	KEY relId (relId,relTypeCode,typeCode,type2Code,fileNo) # 인덱스 지정해 풀스캔 방지
);