# 프로젝트 소개

![](https://velog.velcdn.com/images/qwerty1434/post/c27ed136-9acd-4cfe-a44a-25281973d46f/image.png)

Blooming Blooms는 픽업과 배송이 가능한 컴포저블 서비스로 MSA기반 화훼 쇼핑몰 플랫폼입니다. 다양한 주문(배달주문, 픽업주문, 구독주문)이 존재하며, 몰인몰 형태로 Seller가 입점해 제품을 판매하는 형태의 쇼핑몰입니다.

# Store서비스 소개

Store서비스는 가게 사장(Seller)과 관련된 기능이 구현되어 있는 서비스입니다. Store서비스의 주요 도메인으로는 가게와 관련된 로직을 수행하는 Store, 가게의 재고를 담당하는 Cargo, 가게사장이 생성하고 유저가 사용하는 Coupon, 상품에 대한 문의인 Question이 존재합니다. 

# 서비스 아키텍처

Store서비스의 Layer는 5단계(Controller → Facade → Service → Handler → Repository)로 구성되어 있습니다. 

Service Layer는 핵심 비즈니스 로직이 작성되며 주로 하나의 트랜잭션으로 묶이는 작업들이 존재합니다. 

Facade Layer는 트랜잭션으로 묶이지 않는 FeignClient통신, Kafka Pub/Sub, SQS Pub/Sub등의 작업을 진행합니다. 이를 통해 Service Layer의 트랜잭션이 불필요한 작업으로 길어지는 걸 방지하고 있습니다.

Handler Layer는 Repository Layer의 작업을 한층 더 추상화 해 Service Layer의 코드의 가독성을 높여줍니다. 해당 아이디어는 [Gemini님의 지속 성장 가능한 소프트웨어를 만들어가는 방법](https://geminikims.medium.com/%EC%A7%80%EC%86%8D-%EC%84%B1%EC%9E%A5-%EA%B0%80%EB%8A%A5%ED%95%9C-%EC%86%8C%ED%94%84%ED%8A%B8%EC%9B%A8%EC%96%B4%EB%A5%BC-%EB%A7%8C%EB%93%A4%EC%96%B4%EA%B0%80%EB%8A%94-%EB%B0%A9%EB%B2%95-97844c5dab63)이라는 글을 통해 알게 된 내용이며, 이를 프로젝트에 적용하면서 제가 느꼈던 점들은 [여기](https://velog.io/@qwerty1434/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EA%B5%AC%EC%A1%B0%EC%97%90-%EB%8C%80%ED%95%9C-%EA%B3%A0%EB%AF%BC1-%EB%B9%84%EC%A6%88%EB%8B%88%EC%8A%A4-%EB%A1%9C%EC%A7%81%EC%9D%84-%EC%96%B4%EB%96%BB%EA%B2%8C-%EC%9E%98-%EB%B3%B4%EC%97%AC%EC%A4%84-%EC%88%98-%EC%9E%88%EC%9D%84%EA%B9%8C)에 정리되어 있습니다.

# 전체 ERD 구조
![image](https://github.com/qwerty1434/BB-Store/assets/25142537/5180465a-a173-4306-88f4-a798abdc3e94)



# 고민과 문제 해결

## 쿠폰 발급의 동시성 문제 해결 

- 요구사항 : 1인당 1장만 발급 가능하며 한정된 수량의 쿠폰만 발급해야 함
- 문제 : 멀티쓰레드 환경에서 쿠폰을 발급했을 때 동시성 문제로 쿠폰 초과 발급 문제 발생
- 고려한 사항 : 비관락, Redisson, Redis의 Set, Redis의 트랜잭션, Redis의 Lua Script
    - MSA환경의 확장성을 고려해 비관락이 아닌 분산락 선택
    - Redisson : 1인 1발급 요구사항을 확인하기 위해 RDB를 한번 더 탐색하는 과정이 필요해 사용하지 않음
    - Set자료구조 : 발급 개수를 확인하고 추가로 발급하는 작업이 원자적이지 못해 사용하지 않음
    - Redis트랜잭션 : 트랜잭션 작업이 완료돼야만 결과값을 활용할 수 있어 사용하지 않음
- 해결 방법 : Lua Script를 작성하고 이를 실행하는 방법으로 멀티쓰레드 환경의 동시성 문제 해결
- 검증 : 멀티쓰레드 환경의 테스트 코드 작성, 실서버에서 JMeter 동시 요청을 이용한 테스트

## Redis를 활용한 쿠폰 시스템의 유효기간 설정 

- 요구사항 : 쿠폰을 발급받은 유저 정보를 쿠폰의 유효기간 동안 Redis에 set자료구조로 보관해야 함
- 문제 : 쿠폰의 유효기간은 쿠폰을 생성하는 시점에 결정되지만, 실제로 Redis에 데이터를 저장하고 TTL을 설정하는 작업은 유저가 쿠폰을 발급 받는 시점에 가능함
- 해결 방법 : 쿠폰 생성과 동시에 더미 데이터를 넣은 set데이터를 생성해 쿠폰 생성과 Redis에 데이터를 넣는 시점의 불일치 해소


# 프로젝트를 진행하면서 글로 정리한 내용

- [https://velog.io/@qwerty1434/락을-통한-동시성-제어-쿠폰편](https://velog.io/@qwerty1434/%EB%9D%BD%EC%9D%84-%ED%86%B5%ED%95%9C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%A0%9C%EC%96%B4-%EC%BF%A0%ED%8F%B0%ED%8E%B8)
- [https://velog.io/@qwerty1434/락을-통한-동시성-제어-재고편](https://velog.io/@qwerty1434/%EB%9D%BD%EC%9D%84-%ED%86%B5%ED%95%9C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%A0%9C%EC%96%B4-%EC%9E%AC%EA%B3%A0%ED%8E%B8)
- [https://velog.io/@qwerty1434/락을-통한-동시성-제어-설명편](https://velog.io/@qwerty1434/%EB%9D%BD%EC%9D%84-%ED%86%B5%ED%95%9C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%A0%9C%EC%96%B4-%EC%84%A4%EB%AA%85%ED%8E%B8)
- [https://velog.io/@qwerty1434/테스트-코드-멀티쓰레드-환경의-트랜잭션](https://velog.io/@qwerty1434/%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C-%EB%A9%80%ED%8B%B0%EC%93%B0%EB%A0%88%EB%93%9C-%ED%99%98%EA%B2%BD%EC%9D%98-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98)
- [https://velog.io/@qwerty1434/Resilience4j와-CircuitBreaker-기초](https://velog.io/@qwerty1434/Resilience4j%EC%99%80-CircuitBreaker-%EA%B8%B0%EC%B4%88)
- [https://velog.io/@qwerty1434/카프카-개념-정리](https://velog.io/@qwerty1434/%EC%B9%B4%ED%94%84%EC%B9%B4-%EA%B0%9C%EB%85%90-%EC%A0%95%EB%A6%AC)
- [https://velog.io/@qwerty1434/Spring-Jpa환경에서-복합키-사용-시-Duplicate-Exception이-발생하지-않는-이유](https://velog.io/@qwerty1434/Spring-Jpa%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-%EB%B3%B5%ED%95%A9%ED%82%A4-%EC%82%AC%EC%9A%A9-%EC%8B%9C-Duplicate-Exception%EC%9D%B4-%EB%B0%9C%EC%83%9D%ED%95%98%EC%A7%80-%EC%95%8A%EB%8A%94-%EC%9D%B4%EC%9C%A0)
- [https://velog.io/@qwerty1434/반정규화-테이블-관리하기](https://velog.io/@qwerty1434/%EB%B0%98%EC%A0%95%EA%B7%9C%ED%99%94-%ED%85%8C%EC%9D%B4%EB%B8%94-%EA%B4%80%EB%A6%AC%ED%95%98%EA%B8%B0)
- [https://velog.io/@qwerty1434/MSA환경에서-트랜잭션-관리하기](https://velog.io/@qwerty1434/MSA%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98-%EA%B4%80%EB%A6%AC%ED%95%98%EA%B8%B0)
- [https://velog.io/@qwerty1434/프로젝트-구조에-대한-고민1-비즈니스-로직을-어떻게-잘-보여줄-수-있을까](https://velog.io/@qwerty1434/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EA%B5%AC%EC%A1%B0%EC%97%90-%EB%8C%80%ED%95%9C-%EA%B3%A0%EB%AF%BC1-%EB%B9%84%EC%A6%88%EB%8B%88%EC%8A%A4-%EB%A1%9C%EC%A7%81%EC%9D%84-%EC%96%B4%EB%96%BB%EA%B2%8C-%EC%9E%98-%EB%B3%B4%EC%97%AC%EC%A4%84-%EC%88%98-%EC%9E%88%EC%9D%84%EA%B9%8C)
- [https://velog.io/@qwerty1434/프로젝트-구조에-대한-고민2-트랜잭션을-최대한-짧게-유지하기](https://velog.io/@qwerty1434/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EA%B5%AC%EC%A1%B0%EC%97%90-%EB%8C%80%ED%95%9C-%EA%B3%A0%EB%AF%BC2-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98%EC%9D%84-%EC%B5%9C%EB%8C%80%ED%95%9C-%EC%A7%A7%EA%B2%8C-%EC%9C%A0%EC%A7%80%ED%95%98%EA%B8%B0)
