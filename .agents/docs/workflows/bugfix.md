# Bugfix Workflow

## 목적

크래시, 잘못된 화면 상태, API 실패, 저장값 불일치, 네비게이션 오류를 수정할 때 사용한다.

## 문서 읽기 순서

1. `.agents/docs/workflows/bugfix.md`
2. `.agents/docs/llm-wiki/feature-map.md`
3. `.agents/docs/llm-wiki/architecture-map.md`
4. `.agents/docs/conventions/architecture-conventions.md`
5. `.agents/docs/conventions/compose-style.md`
6. `_workspace/final_review_report.md`, 최근 리뷰 결과가 관련 있을 때만

## 추적 순서

1. 증상이 발생한 화면의 Route/Screen을 찾는다.
2. ViewModel에서 이벤트와 UiState 변경을 확인한다.
3. 호출하는 UseCase를 확인한다.
4. Repository interface와 구현체를 확인한다.
5. DataSource interface와 구현체를 확인한다.
6. API service, Request, Response를 확인한다.
7. Mapper null 처리와 기본값을 확인한다.
8. DI 또는 Navigation 문제인지 확인한다.

## 수정 원칙

- 원인 레이어에서 최소 범위로 수정한다.
- 관련 없는 리팩토링은 하지 않는다.
- 크래시가 Mapper 문제면 Mapper/null 처리 위주로 수정한다.
- UI 상태 문제면 ViewModel/UiState/Screen 분기 위주로 수정한다.
- Navigation 문제면 NavKey, entry, back behavior를 함께 확인한다.

## 완료 기준

- 재현 원인을 한 문장으로 설명할 수 있다.
- 수정 범위가 원인 레이어에 한정되어 있다.
- 동일 흐름의 Mapper와 null safety를 확인했다.
- 가능한 경우 회귀 테스트 또는 최소 검증 명령을 실행했다.
