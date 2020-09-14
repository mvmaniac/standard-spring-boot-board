# Standard Spring Boot Board

### 1. 처음 배우는 스프링 부트 2 책 실습 예제 기반

* [처음 배우는 스프링 부트 2](http://www.kyobobook.co.kr/product/detailViewKor.laf?ejkGb=KOR&mallGb=KOR&barcode=9791162241264&orderClick=LEa&Kc=# "처음 배우는 스프링 부트 2") 참고

### 2. 차이점

* Spring Boot 2, Gradle 6 기반
* EditorConfig 설정 추가
* 아래 3개의 모듈은 별도의 어플리케이션으로 동작함 
    * server-rest
    * server-batch
    * server-web
* common 모듈은 domain과 repository만 있음
* 최대한 이해한 부분만 적용함

### 3. TODO

* server-web
    * form 로그인, 네이버 로그인 추가  
    * oauth2 로그아웃 처리(?)  
    * 기본 error -> errors 폴더 변경 시 처리   
    * 목록 페이지 ajax 처리(?)
    
* server-rest 
    * DTO 클래스 추가
    * HATEOAS 적용
    * 회원인증

