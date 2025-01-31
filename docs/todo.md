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
- [x] commit/issue/pr 개수 세분화
- [x] 텔레그램에 보낼 텍스트 예쁘게 다듬기

### after 1.0.0 (refactoring, test, ci, etc ...)

- [x] github actions aws cli 배포 arn 기반으로 변경
- [x] lambda 버전도 같이 배포 - 설명에 마지막 커밋 값 설정
- [x] commit 가져오는 개수 30개에서 오늘 마지막 커밋까지 가져오도록 수정
- [x] gson deserialize 시 event type enum으로 리팩토링
- [x] repository 생성, fork 시에도 집계하도록 수정
  - [x] repository 생성시 집계
  - [x] fork 시 집계
- [x] 커밋 메시지도 deserialize 대상에 추가
- [x] repository 별로 조회 시 병렬로 조회하여 속도 개선 (coroutines)
- [ ] 테스트 코드 추가
- [ ] CI시 배포할 파일명 환경변수로 분리
- [ ] DeleteEvent이고 ref가 repository일 경우, 오늘 생성한 repository면 집계에서 제외
- [ ] 10시 이후에도 커밋을 하지 않았다면 30분 간격으로 계속 보내기
  - [ ] DB 연결 및 히스토리 저장
- [ ] main branch 처음 push도 집계에 반영
- [ ] repository 이름 변경 시 기존의 이름만 사용하도록 수정
