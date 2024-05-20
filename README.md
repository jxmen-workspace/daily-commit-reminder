# daily-commit-notifier

오늘 커밋을 했는지 여부를 밤마다 알림을 주는 AWS Lambda 함수

- AWS Lambda + EventBridge + Telegram Bot API

### deploy

- `build.gradle`에 `shadowJar` 플러그인을 추가해야 함
- `build/libs` 디렉토리에 `app-all.jar` 파일이 생성되어 있어야 함
 
```bash
./gradlew shadowJar

aws lambda update-function-code \
    --function-name daily-commit-notifier \
    --zip-file fileb://./app/build/libs/app-all.jar
```

### AWS Lambda 관련 설정

AWS Lambda 사용 시 런타임 설정의 핸들러 명칭을 정확히 잘 적어주어야 동작한다.

![](.README_images/38b9e200.png)

### ToDo

- [x] kotlin lambda function 배포 (hello만 리턴)
- [x] aws eventbridge 크론 작업 연결
    - [x] 인프라 생성
    - [x] 로그 남기기
- [x] 배포 aws cli로 해보기
    - [x] aws access key 발급
    - [x] aws lambda update-function-code로 배포
- [x] 텔레그램 메신저 연동
    - [x] 텔레그램 API 연결
    - [x] 특정 ID (내 ID) 에게 메시지 보내기
- [ ] github api 연결 및 테스트 코드 작성
- [ ] 커밋 정보 확인 후 메신저 메시지로 전달
- [ ] github actions에서 배포 자동화
