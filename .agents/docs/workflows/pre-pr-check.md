# Pre PR Check Workflow

## 목적

커밋 또는 PR 생성 직전에 변경사항이 제출 가능한 상태인지 확인한다.

## 문서 읽기 순서

1. `.agents/docs/workflows/pre-pr-check.md`
2. `.agents/docs/conventions/kotlin-style.md`
3. `.agents/docs/conventions/compose-style.md`
4. `.agents/docs/conventions/testing-conventions.md`
5. `.agents/docs/workflows/code-review.md`, 변경 범위가 큰 경우

## 확인 순서

1. 현재 브랜치 확인
2. 변경 파일 확인
3. 의도하지 않은 변경 확인
4. Android 필수 점검
5. 빌드 또는 테스트 실행
6. PR 설명 초안 작성

## 의도하지 않은 변경 후보

- 포맷만 바뀐 파일
- generated 파일
- `local.properties`
- keystore, secret, token
- `_workspace` 임시 리포트

## Android 필수 점검

- Route/Screen 분리 유지
- `collectAsStateWithLifecycle()` 사용
- Hilt module binding 누락 없음
- Navigation3 entry 누락 없음
- Mapper 체인 완성
- nullable response 안전 처리
- Preview 필요 화면 확인

## 완료 기준

- `git status` 기준 변경 파일을 모두 설명할 수 있다.
- 의도하지 않은 파일이 포함되지 않았다.
- 빌드 또는 테스트 결과를 확인했다.
- 실패한 검증이 있으면 원인과 남은 작업을 적었다.
