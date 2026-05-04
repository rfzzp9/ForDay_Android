# Pre PR Check Workflow

## 목적

커밋 또는 PR 생성 직전에 변경사항이 제출 가능한 상태인지 확인한다.

## 문서 읽기 순서

1. `.agents/docs/workflows/pre-pr-check.md`
2. `.agents/docs/conventions/kotlin-style.md`
3. `.agents/docs/conventions/compose-style.md`
4. `.agents/docs/conventions/testing-conventions.md`
5. `.agents/docs/conventions/commit-conventions.md`
6. `.agents/docs/conventions/github-workflow-conventions.md`
7. `.agents/docs/workflows/code-review.md`, 변경 범위가 큰 경우

## 확인 순서

1. 현재 브랜치 확인
2. 변경 파일 확인
3. 의도하지 않은 변경 확인
4. Android 필수 점검
5. 빌드 또는 테스트 실행
6. 커밋 요청이 있었는지 확인
7. 커밋 분리 필요 여부 확인
8. 이슈/브랜치/PR 생성 요청이 명시적이었는지 확인
9. PR 설명 초안 작성

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
- 커밋은 사용자 명시 요청이 있을 때만 만든다는 원칙을 확인했다.
- 이슈, 브랜치, PR 같은 GitHub 쓰기 작업도 사용자 명시 요청이 있을 때만 수행한다.
- 커밋이 하나의 목적을 담는지 확인했다.
- 빌드 또는 테스트 결과를 확인했다.
- 실패한 검증이 있으면 원인과 남은 작업을 적었다.
