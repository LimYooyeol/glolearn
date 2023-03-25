# 글로런

텍스트와 이미지로 된 강의를 수강하거나, 등록할 수 있는 강의 플랫폼입니다.

* 개발 인원 -  개인 프로젝트
* 개발 기간 - 23/02 ~ 23/03
* 링크 - [글로런 서비스 이용하기](http://13.124.113.195:8080/){:target="_blank"} (PC 환경에서 정상적으로 이용 가능)

# 개발환경
* 백엔드:
  * Java 17, Spring Boot 2.7.8
  * MySQL
  * JPA
* 프론트엔드:
  * HTML, CSS, JavaScript
  * Thymeleaf
  * Bootstrap 4.4.1
* 클라우드
 * AWS RDS
 * AWS S3
 * AWS EC2
  
# 주요기능
* 회원
  * 소셜 로그인
* 코스(일반회원)
  * 출시된 코스 조회
  * 코스 결제 후 강의 수강가능
* 강의(일반회원)
  * 첫번째 강의 미리보기 가능
  * 수강한 코스의 모든 강의 수강 가능
  * 이어보기 가능
* 코스(강사)
  * 코스 출시/수정/삭제
  * 출시된 코스는 삭제 불가
* 강의(강사)
  * 강의 등록/수정/삭제
* 질의응답
  * 강의 별 묻고 답하기 제공
  
# DB 설계
<img src = "https://user-images.githubusercontent.com/71579787/227470551-228c6fc3-c97c-42d3-ba57-e2dc09697468.png"></img>

# 세부기능 및 API
![image](https://user-images.githubusercontent.com/71579787/227471055-69b3b07b-550e-448f-a360-fd23b671b825.png)
![image](https://user-images.githubusercontent.com/71579787/227471154-d100f767-1917-40f0-9a30-f1f72e0996bc.png)




