# Feature Development Workflow

## Compose/component 패키지 체크

새 화면을 추가하거나 큰 Compose 화면을 리팩토링할 때는 패키지 경계를 먼저 확인한다.

- `presentation/{feature}/compose`에는 Route 또는 ScreenRoot, Screen, 화면 레이아웃 조립 코드를 둔다.
- `presentation/{feature}/component`에는 section, item, dialog, bottom sheet, toolbar처럼 재사용되거나 독립적으로 읽히는 UI 조각을 둔다.
- `component` Composable은 가능한 stateless로 유지하고, state와 callback은 `compose`에서 내려준다.
- `component`에서 ViewModel, Navigator, Repository, UseCase, API를 직접 참조하지 않는다.
- 큰 화면 리팩토링은 동작을 바꾸지 않고 패키지 경계부터 나눈 뒤 빌드나 집중 검증을 실행한다.

## 목적

새 화면, 새 API 연동, 새 ViewModel, 새 UseCase, 새 Repository 흐름을 추가할 때 사용한다.

## 문서 읽기 순서

1. `.agents/docs/workflows/feature-development.md`
2. `.agents/docs/llm-wiki/project-map.md`
3. `.agents/docs/llm-wiki/feature-map.md`
4. `.agents/docs/llm-wiki/add-feature-file-locations.md`
5. `.agents/docs/llm-wiki/navigation-map.md`, 화면 추가 시
6. `.agents/docs/conventions/architecture-conventions.md`
7. `.agents/docs/conventions/compose-style.md`
8. `.agents/docs/conventions/kotlin-style.md`

## 진행 순서

1. 요구사항에서 화면, API, 상태, 네비게이션 필요 여부를 나눈다.
2. 기존 유사 feature를 하나 고른다.
3. Remote가 필요하면 Response/Request, Api, DataSourceImpl부터 확인한다.
4. Data Entity와 RepositoryImpl을 연결한다.
5. Domain Model, Repository interface, UseCase를 만든다.
6. Presentation UiModel, UiState, ViewModel을 만든다.
7. Route 또는 ScreenRoot와 Screen을 분리한다.
8. Navigation entry와 NavKey를 등록한다.
9. DI 모듈에 누락된 binding을 등록한다.
10. 빌드 또는 관련 테스트를 실행한다.

## 완료 기준

- Screen은 ViewModel을 직접 참조하지 않는다.
- UiState 기본값이 안전하다.
- Response -> Entity -> Domain -> UiModel 흐름이 끊기지 않는다.
- Repository interface와 구현체 시그니처가 일치한다.
- Hilt DI 등록이 누락되지 않았다.
- Navigation entry가 필요한 경우 등록되어 있다.
- Preview가 필요한 Screen에 존재한다.
- 실행한 검증 결과를 보고한다.
