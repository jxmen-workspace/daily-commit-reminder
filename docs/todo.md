# ToDo

### features for 1.0.0

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
- [x] github api 연결 및 테스트 코드 작성
    - [x] github api 호출 전용 토큰 발급
    - [x] 외부 api 호출 클라이언트 라이브러리 선정 및 설치
    - [x] github api 호출 후 결과 string 만들기
    - [x] 텔레그램에 커밋 숫자 전달
- [x] 커밋 숫자 정확하게 가져오도록 변경
- [x] 에러 발생시 텔레그램으로 메세지 전송
- [x] github actions에서 배포 자동화
- [x] Pull Reqeust 생성한 이벤트만 집계에 추가
- [x] issue/pull reuqest/commit 개수 세분화
- [x] 텔레그램에 보낼 텍스트 예쁘게 다듬기

### after 1.0.0 (refactoring, test, ci, etc ...)

- [x] github actions aws cli 배포 arn 기반으로 변경
- [x] lambda 버전도 같이 배포 - 설명에 마지막 커밋 값 설정
- [ ] 테스트 코드 추가
