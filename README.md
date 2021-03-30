# NUTEE-AUTH
## NUTEE 구조도
![NUTEE 구조도](https://user-images.githubusercontent.com/47442178/108618442-96779080-7461-11eb-819e-c8dd855a8070.jpg)

## AUTH Server 구조도
![AUTH 구조도](https://user-images.githubusercontent.com/47442178/112944624-0cac9880-916e-11eb-8fab-dd59654e9a50.png)

### 서비스 주요 기능
- NUTEE 통합 서비스의 사용자 계정 관련 API 제공   
- NUTEE 통합 서비스의 인증 API 제공

### 주요 사용 기술
- Spring Boot
- Spring Security
- Json Web Token(JWT)
- MongoDB
- Kafka

### 서비스 설계시 고려사항
- JWT를 사용하여 stateless한 인증 방식을 채택함으로써 서비스 확장에 유연한 환경 제공   


- 사용자 계정의 데이터 변경 시, 올바르게 다른 서비스의 데이터베이스로 트랜잭션이 전파될 수 있도록 kafka를 사용하여 분산 트랜잭션 구현.   


- 인증 서비스 특성상 데이터의 변경이 적고, 데이터의 무결성을 해칠만한 트랜잭션이 발생하지 않는다. 그리고 사용자 정보 하나만을 저장하고 관계를 설정할 필요가 없어 데이터의 정규화가 필요없기 때문에 RDBMS에 비해 성능상의 이점을 챙길 수 있는 MongoDB 선택.


