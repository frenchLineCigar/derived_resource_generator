
# 이미지 변환 서비스

> URL의 이미지를 원하는 크기로 변환해 제공합니다.  
> 리사이징 시, 포인트 중심의 스마트 크롭으로 원본 이미지의 퀄리티를 유지합니다.  
> - [이미지(100x100) 노출 시 차이 비교](https://to2.kr/cBu)
> - [이미지(100x700) 노출 시 차이 비교](https://to2.kr/cBv)
> - [이미지가 존재하지 않을 경우 차이 비교](https://to2.kr/cBy)


## 사용법

0\. API Docs (by Springfox Swagger2)
  - http://localhost:8085/api-docs


1\. 너비, 높이 지정
  - http://localhost:8085/img?width=[원하는 너비]&height=[원하는 높이]&url=[변환할 이미지 원본 URL]
  - 높이 생략 가능 (원본 비율에 맞게 환산)
  - 높이만 입력 시 무시함


2\. 최대너비 지정
  - http://localhost:8085/img?maxWidth=[원하는 최대너비]&url=[변환할 이미지 원본 URL]
  - 원본 이미지 너비가 maxWidth 보다 클 경우, 너비로 maxWidth 값을 갖는 이미지 제공


## 사용 예

- http://localhost:8085/img?width=100&height=100&url=https://picsum.photos/seed/picsum/200/300
- http://localhost:8085/img?width=500&height=500&url=https://picsum.photos/seed/picsum/200/300
- http://localhost:8085/img?width=700&url=https://picsum.photos/id/237/200/300
- 


## DONE
- [x] 프로젝트 셋팅
- [x] url 이미지 다운로드
- [x] 파일 다운로드 시, 고유한 파일명을 부여해 덮어쓰기 방지
- [x] 파생 요청 내역을 관리해 신규 url일 경우만 파일 저장
- [x] url 이미지 저장후 보여주기
- [x] 클라이언트 캐시 기능 구현
- [x] 이미지 출처가 중복되는 요청은 원본 다운로드 생략
- [x] 파생 요청 사항을 만족하는 기존 이미지가 있으면 이를 활용
- [x] 이미지 리사이즈 & 크롭
- [x] Untact Community 프로젝트 회원 아바타 이미지 노출 시 본 프로젝트 활용