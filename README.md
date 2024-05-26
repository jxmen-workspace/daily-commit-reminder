# daily-commit-notifier

오늘 커밋을 했는지 여부를 밤마다 알림을 주는 AWS Lambda 함수

- AWS Lambda + EventBridge + Telegram Bot API

### AWS Lambda 핸들러 설정

AWS Lambda 사용 시 런타임 설정의 핸들러 명칭을 정확히 잘 적어주어야 동작한다.

![aws lambda handler settings](.README_images/38b9e200.png)

- `App::handleRequest` 라고 적어주었는데, 이는 `App` 클래스의 `handleRequest` 메소드를 호출하겠다는 의미이다.

### ToDo

[./docs/todo.md](./docs/todo.md)
