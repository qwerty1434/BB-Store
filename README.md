# 프로젝트 소개

![](https://velog.velcdn.com/images/qwerty1434/post/c27ed136-9acd-4cfe-a44a-25281973d46f/image.png)

Blooming Blooms는 픽업과 배송이 가능한 컴포저블 서비스로 MSA기반 화훼 쇼핑몰 플랫폼입니다. 다양한 주문(배달주문, 픽업주문, 구독주문)이 존재하며, 몰인몰 형태로 Seller가 입점해 제품을 판매하는 형태의 쇼핑몰입니다.

# Store서비스 소개

Store서비스는 가게 사장(Seller)과 관련된 기능이 구현되어 있는 서비스입니다. Store서비스의 주요 도메인으로는 가게와 관련된 로직을 수행하는 Store, 가게의 재고를 담당하는 Cargo, 가게사장이 생성하고 유저가 사용하는 Coupon, 상품에 대한 문의인 Question이 존재합니다. 

# 서비스 아키텍처

Store서비스의 Layer는 5단계(Controller → Facade → Service → Handler → Repository)로 구성되어 있습니다. 

Service Layer는 핵심 비즈니스 로직이 작성되며 주로 하나의 트랜잭션으로 묶이는 작업들이 존재합니다. 

Facade Layer는 트랜잭션으로 묶이지 않는 FeignClient통신, Kafka Pub/Sub, SQS Pub/Sub등의 작업을 진행합니다. 이를 통해 Service Layer의 트랜잭션이 불필요한 작업으로 길어지는 걸 방지하고 있습니다.

Handler Layer는 Repository Layer의 작업을 한층 더 추상화 해 Service Layer의 코드의 가독성을 높여줍니다. 해당 아이디어는 [Gemini님의 지속 성장 가능한 소프트웨어를 만들어가는 방법](https://geminikims.medium.com/%EC%A7%80%EC%86%8D-%EC%84%B1%EC%9E%A5-%EA%B0%80%EB%8A%A5%ED%95%9C-%EC%86%8C%ED%94%84%ED%8A%B8%EC%9B%A8%EC%96%B4%EB%A5%BC-%EB%A7%8C%EB%93%A4%EC%96%B4%EA%B0%80%EB%8A%94-%EB%B0%A9%EB%B2%95-97844c5dab63)이라는 글을 통해 알게 된 내용이며, 이를 프로젝트에 적용하면서 제가 느꼈던 점들은 [3주차. 내 프로젝트 구조](https://lotteon2.github.io/posts/project-structure/)라는 글에 정리되어 있습니다.

# 주요 기술

- Spring/SpringBoot
- Redis
- Kafka
- Resilience4j
- OpenFeign

# 고민과 문제 해결

****락으로 동시성 문제 해결하기****

- [락을 통한 동시성 제어 - 쿠폰편](https://velog.io/@qwerty1434/%EB%9D%BD%EC%9D%84-%ED%86%B5%ED%95%9C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%A0%9C%EC%96%B4-%EC%BF%A0%ED%8F%B0%ED%8E%B8)
- [락을 통한 동시성 제어 - 설명편](https://velog.io/@qwerty1434/%EB%9D%BD%EC%9D%84-%ED%86%B5%ED%95%9C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%A0%9C%EC%96%B4-%EC%84%A4%EB%AA%85%ED%8E%B8)
- [락을 통한 동시성 제어 - 재고편](https://velog.io/@qwerty1434/%EB%9D%BD%EC%9D%84-%ED%86%B5%ED%95%9C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%A0%9C%EC%96%B4-%EC%9E%AC%EA%B3%A0%ED%8E%B8)
- [테스트 코드 - 멀티쓰레드 환경의 트랜잭션](https://lotteon2.github.io/posts/transaction-in-multi-thread/)

**요청과 폴백메서드**

사용자에게 가게정보를 보여줄 때 `구독상품Id`를 함께 전달해야 합니다. 하지만 구독상품Id는 Store서비스에는 존재하지 않는 데이터이므로 Product Service와의 FeignClient통신을 통해 해당 데이터를 가져와야 합니다. </br>
이때 Product Service가 일시적으로 이상이 생겨 구독상품Id를 반환하지 못할 가능성이 있습니다. </br>
구독상품Id를 받아오지 못했을 때 사용자에게 ‘서버에 일시적인 문제가 발생했습니다. 불편을 드려 죄송합니다’와 같은 에러 문구를 보이기보다 </br>
`구독상품Id를 제외한 나머지 가게정보는 사용자가 모두 확인할 수 있게`만드는 게 사용자 측면에서 더 좋은 경험이 될 거라 판단해 예외가 아닌 Fallback메서드를 정의해 null값을 반환하도록 설계했습니다. </br>
구독상품Id가 null일 때 프론트는 구독신청 버튼을 비활성화 상태로 만들었고, 나머지 가게정보는 사용자가 여전히 확인할 수 있습니다. </br>
