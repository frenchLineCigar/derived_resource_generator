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

# originUrl 인덱스를 일반 인덱스로 수정
ALTER TABLE derivedRequest DROP INDEX originUrl, ADD KEY originUrl (originUrl ASC) VISIBLE;

# [참고 사항]
# url : /img?url=https://cdn.worldvectorlogo.com/logos/lorem-lorem-1.svg
# -> originUrl : https://cdn.worldvectorlogo.com/logos/lorem-lorem-1.svg
# 다음과 같이 원본파일 url(originUrl)이 같더라도 width, height, maxWidth 등과 조합되어 다른 케이스로 올 수 있다.
# Case 1) /img?url=https://cdn.worldvectorlogo.com/logos/lorem-lorem-1.svg
# Case 2) /img?width=200&url=https://cdn.worldvectorlogo.com/logos/lorem-lorem-1.svg
# -> 위 2가지 Case는 서로 다른 요청이지만 같은 파일을 저장하게 된다.
# 그러나 https://cdn.worldvectorlogo.com/logos/lorem-lorem-1.svg 란 url의 원본 파일을 이미 가지고 있다면
# 파일을 새로 저장하지 않고, 이미 저장된 파일을 참조하도록 처리하기 위해 originUrl 칼럼을 일반 인덱스로 변경한다.


# genFile 테이블에 파일의 너비(width), 높이(height) 정보에 대한 칼럼 추가
ALTER TABLE genFile ADD COLUMN `width` SMALLINT(2) UNSIGNED NOT NULL AFTER `fileDir`;
ALTER TABLE genFile ADD COLUMN `height` SMALLINT(2) UNSIGNED NOT NULL AFTER `width`;

# derivedRequest 테이블 컬럼명 변경 : url -> requestUrl
ALTER TABLE derivedrequest
CHANGE `url` `requestUrl` CHAR(200) CHARSET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
DROP INDEX `url`,
ADD UNIQUE INDEX `requestUrl` (`requestUrl`) VISIBLE;

# derivedRequest 테이블에 originStatus 칼럼 추가
ALTER TABLE derivedRequest
ADD COLUMN `originStatus` TINYINT(1) UNSIGNED DEFAULT 0 NOT NULL
COMMENT '원본 파일이 이미 저장된 요청인지 여부'
AFTER `originUrl`;

# 관련된 파일 번호 칼럼 추가
ALTER TABLE derivedRequest
ADD COLUMN `genFileId` INT(1) UNSIGNED DEFAULT 0 NOT NULL
COMMENT '요청으로 저장된 파일 번호'
AFTER `originUrl`;
