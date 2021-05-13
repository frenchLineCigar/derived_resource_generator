# 유저정보 조회
SELECT * FROM mysql.user;

# 유저명만 조회
SELECT `User` FROM mysql.user;

# MySQL 5.0 버전을 사용하는 경우
# 동시에 줄 수 있음 : GRANT ALL PRIVILEGES ON db이름.* TO 계정이름@`%` IDENTIFIED BY '암호';
# grant all privileges on *.* to drg@`%` identified by '123456A@';

# MySQL 8.0 버전을 사용하는 경우
# 계정 생성 : CREATE USER 'connectuser'@'%' IDENTIFIED BY 'password';
# 권한 부여 : GRANT ALL ON connectdb.* TO 'connectuser'@'%';
CREATE USER drg@`%` IDENTIFIED BY '123456A@';
GRANT ALL ON *.* TO drg@`%`;

# 계정에 부여된 권한 확인
SELECT `Host`, `User`, `authentication_string` FROM MYSQL.USER;
SHOW GRANTS FOR root@localhost;
SHOW GRANTS FOR drg;
SHOW GRANTS FOR CURRENT_USER;