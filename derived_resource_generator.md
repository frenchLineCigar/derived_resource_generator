# TIL

---

[2021-05-13] 파일 다운로드 시, 고유한 파일명을 부여해 덮어쓰기 방지
- 파일 다운로드 시, 파일명 중복으로 기존 파일이 덮어쓰기 되지 않도록 처리

> 파일 저장 시, UUID를 통한 고유하고 랜덤한 파일명을 부여해, 기존 파일이 덮어쓰기 되는 경우를 방지한다.

- [자바 파일이름 중복 방지](https://www.google.com/search?q=%EC%9E%90%EB%B0%94+%ED%8C%8C%EC%9D%BC%EC%9D%B4%EB%A6%84+%EC%A4%91%EB%B3%B5+%EB%B0%A9%EC%A7%80+%ED%8C%A8%ED%84%B4&oq=%EC%9E%90%EB%B0%94+%ED%8C%8C%EC%9D%BC%EC%9D%B4%EB%A6%84+%EC%A4%91%EB%B3%B5+%EB%B0%A9%EC%A7%80+%ED%8C%A8%ED%84%B4&aqs=chrome.0.69i59j0i333.689j0j9&sourceid=chrome&ie=UTF-8)
  - [UUID로 파일 이름 중복 방지하기](https://enai.tistory.com/38)
  - [Kakao DB Team: MySQL 서버에서 UUID 활용](https://small-dbtalk.blogspot.com/2014/12/)

---

[2021-05-12] 쿼리 스트링으로 넘어온 URL의 파일 다운로드

- 자바 HTTP 파일 다운로드
  - [java http file download - Google 검색](https://www.google.com/search?q=java+http+file+download&oq=java+http+file+download&aqs=chrome..69i57j0i8i30l4j69i60j69i65j69i60.3848j0j9&sourceid=chrome&ie=UTF-8)
  - [자바 http download](https://www.google.com/search?q=%EC%9E%90%EB%B0%94+http+download&sxsrf=ALeKk03pY7g6UQB6eWECY5eZLDB1XuaN4Q%3A1620796295778&ei=h2ObYO6JL4eS0gTp47G4DQ&oq=%EC%9E%90%EB%B0%94+http+download&gs_lcp=Cgdnd3Mtd2l6EAMyBQgAEM0CMgUIABDNAjoHCAAQsAMQHjoJCAAQsAMQCBAeOgQIIxAnOgYIABAHEB46BAgAEB5QikxYh1Ng-FRoAnAAeACAAZEBiAHbBZIBAzAuNpgBAKABAaoBB2d3cy13aXrIAQbAAQE&sclient=gws-wiz&ved=0ahUKEwiu6dnbsMPwAhUHiZQKHelxDNcQ4dUDCA4&uact=5)
  - **[Download a File From an URL in Java](https://www.baeldung.com/java-download-file)**
    - Downloader for use 구현 시 Using Java NIO 참고함
    - Downloader v1 구현 시 Using Java IO 참고함
    - [tutorials/FileDownload.java at 950bbadc353bdca114befc98cf4a18476352220e · eugenp/tutorials](https://github.com/eugenp/tutorials/blob/950bbadc353bdca114befc98cf4a18476352220e/core-java-modules/core-java-networking-2/src/main/java/com/baeldung/download/FileDownload.java)
    - [tutorials/FileDownloadIntegrationTest.java at 950bbadc353bdca114befc98cf4a18476352220e · eugenp/tutorials](https://github.com/eugenp/tutorials/blob/950bbadc353bdca114befc98cf4a18476352220e/core-java-modules/core-java-networking-2/src/test/java/com/baeldung/download/FileDownloadIntegrationTest.java#L41)
  - Downloader v2 구현 시 참고함
    - [[Java] URL로 부터 File Download 하는 방법](https://jinseongsoft.tistory.com/322)
    - [[Java] 자바 HttpUrlConnection 을 이용하여 파일 다운받기](https://velog.io/@hellonewtry/Java-%EC%9E%90%EB%B0%94-HttpUrlConnection-%EC%9D%84-%EC%9D%B4%EC%9A%A9%ED%95%98%EC%97%AC-%ED%8C%8C%EC%9D%BC-%EB%8B%A4%EC%9A%B4%EB%B0%9B%EA%B8%B0)
    - [개발자의 하루 :: 자바 (Java) HTTP 파일 (File) 다운로드 (Download) 예제 (Example)](https://devday.tistory.com/entry/%EC%9E%90%EB%B0%94-Java-HTTP-%ED%8C%8C%EC%9D%BC-File-%EB%8B%A4%EC%9A%B4%EB%A1%9C%EB%93%9C-Download-%EC%98%88%EC%A0%9C-Example)
  

- Query String 추출
  - [query string parameter](https://www.google.com/search?q=query+string+parameter&oq=query+string+parameter&aqs=chrome.0.0l4.3246j1j7&sourceid=chrome&ie=UTF-8)
  - [올바른 URL 설계 : 1) Query string과 Path Variable 이해하기](https://velog.io/@jcinsh/Query-string-path-variable)
  - [Query String 쿼리스트링이란?](https://velog.io/@pear/Query-String-%EC%BF%BC%EB%A6%AC%EC%8A%A4%ED%8A%B8%EB%A7%81%EC%9D%B4%EB%9E%80)
  - [배우고 나누고 즐기며... :: 쿼리스트링(Query String)](https://ysoh.tistory.com/entry/Query-String)


- [get filename from path java - Google 검색](https://www.google.com/search?q=java+get+file+name+from+path&oq=java+get+file+name+from+path&aqs=chrome..69i57j0i30l2j0i8i30l6j0i5i30.5844j1j7&sourceid=chrome&ie=UTF-8)
  - [How do I get the file name from a String containing the Absolute file path? - Stack Overflow](https://stackoverflow.com/questions/14526260/how-do-i-get-the-file-name-from-a-string-containing-the-absolute-file-path/14526362#14526362)
- [get filename from url java - Google 검색](https://www.google.com/search?q=get+filename+from+url+java&oq=get+filename+from+url+java&aqs=chrome..69i57j0l9.3311j0j7&sourceid=chrome&ie=UTF-8)
  - [Get file name from URL - Stack Overflow](https://stackoverflow.com/questions/605696/get-file-name-from-url/33871029#33871029)


- java.util.regex.PatternSyntaxException: Dangling meta character '?' near index 0
  - [[JAVA] java.util.regex.PatternSyntaxException: Dangling meta character :: 나만의 기록들](https://mine-it-record.tistory.com/309)
  - [replace / replaceFirst, Caused by: java.util.regex.PatternSyntaxException: Dangling meta character '?' near index 0](https://acet.tistory.com/291)
  - [자바 문자열 오류 - java.util.regex.PatternSyntaxException: Dangling meta character 해결](https://blog.naver.com/qbxlvnf11/221170503490)
  - ["?" Dangling metacharacter - Google 검색](https://www.google.com/search?q=%22%3F%22+Dangling+metacharacter&oq=%22%3F%22+Dangling+metacharacter&aqs=chrome..69i57.9224j0j7&sourceid=chrome&ie=UTF-8)
  

- Lorem Picsum
  - 랜덤한 이미지 URL 얻고 싶을 경우 'lorem img' 키워드로 검색
  - [Lorem Picsum](https://picsum.photos/)
  - [사용하기 쉽고 세련된 자리 표시자 로렘 픽숨(Lorem Picsum)](http://websrepublic.co.kr/news/355)
  - [LoremFlickr: free placeholder images](https://loremflickr.com/)
  - 보통 화면 디자인 시 placeholder images 로 이용하곤 한다.
  

- Executing Code on Spring Boot Application Startup
  - [Executing Code on Spring Boot Application Startup](https://reflectoring.io/spring-boot-execute-on-startup/)
  - [스프링 부트 애플리케이션이 구동될 때 코드를 실행하는 방법](https://madplay.github.io/post/run-code-on-application-startup-in-springboot)
  - [애플리케이션 시작시 실행되는 프로그램 설정](https://velog.io/@borab/Spring-boot-%EA%B0%9C%EB%85%90-%EC%A0%95%EB%A6%AC-1#%EC%95%A0%ED%94%8C%EB%A6%AC%EC%BC%80%EC%9D%B4%EC%85%98-%EC%8B%9C%EC%9E%91%EC%8B%9C-%EC%8B%A4%ED%96%89%EB%90%98%EB%8A%94-%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%A8-%EC%84%A4%EC%A0%95)
    - [ApplicationEvent and Listeners](https://velog.io/@borab/Spring-boot-%EA%B0%9C%EB%85%90-%EC%A0%95%EB%A6%AC-1#applicationevent-and-listeners)
  - [토비의 스프링 부트 1 - 스프링 부트 앱에 초기화 코드를 넣는 방법 3가지 - YouTube](https://www.youtube.com/watch?v=f017PD5BIEc)
  - [Running code after Spring Boot starts - Stack Overflow](https://stackoverflow.com/questions/27405713/running-code-after-spring-boot-starts/44923402#44923402)
  - [Guide To Running Logic on Startup in Spring](https://www.baeldung.com/running-setup-logic-on-startup-in-spring)
  - [ApplicationReadyEvent - Google 검색](https://www.google.com/search?q=ApplicationReadyEvent+%EB%B0%B1%EA%B8%B0%EC%84%A0&oq=ApplicationReadyEvent+%EB%B0%B1%EA%B8%B0%EC%84%A0&aqs=chrome..69i57j0l5j0i30l4.2350j0j7&sourceid=chrome&ie=UTF-8)
  - [spring boot on start event - Google 검색](https://www.google.com/search?q=spring+boot+on+start+event&oq=spring+boot+on+start+event&aqs=chrome..69i57j0i8i30j69i60.3391j0j9&sourceid=chrome&ie=UTF-8)
  - [spring boot application ready - Google 검색](https://www.google.com/search?q=spring+boot+application+ready&oq=spring+boot+application+ready&aqs=chrome..69i57j0i19j0i19i30l2j0i8i19i30l2.3600j0j9&sourceid=chrome&ie=UTF-8)
  - [spring boot startup method - Google 검색](https://www.google.com/search?q=spring+boot+startup+method&oq=spring+boot+startup+method&aqs=chrome.0.69i59j0i8i30.312j0j9&sourceid=chrome&ie=UTF-8)


- [Spring @Order 로 Bean 순서 정의하기](https://unhosted.tistory.com/79) 
  

- OS에 따른 Java Directory Separator 처리
  - [java directory separator - Google 검색](https://www.google.com/search?q=java+directory+separator&oq=java+directory+separator&aqs=chrome..69i57.199j0j9&sourceid=chrome&ie=UTF-8)
  - [File.separator 와 File.pathSeparator의 차이는 뭔가요?](https://hashcode.co.kr/questions/570/fileseparator-%EC%99%80-filepathseparator%EC%9D%98-%EC%B0%A8%EC%9D%B4%EB%8A%94-%EB%AD%94%EA%B0%80%EC%9A%94)
  - [File.separatorChar 와 File.separator 차이점 - Google 검색](https://www.google.com/search?q=separatorChar+separator+%EC%B0%A8%EC%9D%B4%EC%A0%90&oq=separatorChar+separator+%EC%B0%A8%EC%9D%B4%EC%A0%90&aqs=chrome..69i57j0i333l2.10412j0j7&sourceid=chrome&ie=UTF-8)
  - [File.separator, File.separatorChar 차이점](https://blog.naver.com/anjdieheocp/20117162010)
  - [파일구분자(File.separator)를 대체하는 Path, Paths 사용하기 :: 허니몬(Honeymon)의 자바guru](https://java.ihoney.pe.kr/342)
  

- Java IO Exception Handling, 자원 해제 close 와 예외처리 (Exception Handling)
  - [Java에서의 Close (feat. Connection, Statement, ResultSet) :: 소림사의 홍반장!](https://androphil.tistory.com/763)
  - [Java - Try-with-resources로 자원 쉽게 해제하기](https://codechacha.com/ko/java-try-with-resources/)
  - [Chapter 8 예외처리(Exception Handling)](https://rebeccacho.gitbooks.io/java-study-group/content/chapter8.html)
  - [자바 8 람다에서 checked exception을 어떻게 구현하면 좋을까? :: SLiPP](https://www.slipp.net/questions/572)
  - [[Java] try catch finally](https://shxrecord.tistory.com/59)
  - [Java 예외(Exception) 처리에 대한 작은 생각](https://www.nextree.co.kr/p3239/)
  - [Java Exception Handle in Stream Operations](https://kihoonkim.github.io/2017/09/09/java/noexception-in-stream-operations/)
  - [Today I Learned. @cheers_hena 치얼스헤나 :: [JAVA] Try-Catch문이란? 예외처리하기/예외던지기](https://cheershennah.tistory.com/147)
  - [코끼리를 냉장고에 넣는 방법 :: [JAVA] 자바 향상된 예외 처리 - try - with - resources 문](https://dololak.tistory.com/67)
  - [자바7에서 마음에 드는 다섯 가지 :: 자바캔(Java Can Do IT)](https://javacan.tistory.com/entry/my-interesting-java7-five-features)
  - [java io exception handling - Google 검색](https://www.google.com/search?q=java+io+exception+handling&oq=java+io+exception+handling&aqs=chrome..69i57j0i8i30j69i60.5203j0j7&sourceid=chrome&ie=UTF-8)
  - [try catch finally 생략 - Google 검색](https://www.google.com/search?q=try+catch+finally+%EC%83%9D%EB%9E%B5&oq=try+catch+finally+%EC%83%9D%EB%9E%B5&aqs=chrome..69i57j0i333l5.8848j1j7&sourceid=chrome&ie=UTF-8)
  - [java try catch finally resource - Google 검색](https://www.google.com/search?q=java+try+catch+finally+resource&oq=java+try+catch+finally+resource&aqs=chrome..69i57j0i8i30l2.250j0j9&sourceid=chrome&ie=UTF-8)