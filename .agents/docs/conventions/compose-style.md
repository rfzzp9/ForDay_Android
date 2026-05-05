# Compose Style

## 목적

Forday Android Compose 화면 작성 규칙을 정리한다.

## Route 또는 ScreenRoot

역할:
- ViewModel 획득
- `collectAsStateWithLifecycle()`로 state 수집
- side effect 수집
- navigation callback 연결
- analytics log 호출

허용:
- `hiltViewModel()` 호출
- `LaunchedEffect`로 side effect 수집
- navigation callback을 Screen으로 전달

금지:
- 복잡한 UI layout 직접 작성
- Remote/Data model 직접 사용
- unrelated business logic 처리

## Screen

역할:
- 순수 UI 렌더링
- state와 callback만 파라미터로 받기
- ViewModel 직접 참조 금지
- Navigator 직접 참조 금지

허용:
- local UI state를 위한 `remember`
- 입력 필드나 일시적 UI 표시 상태

금지:
- `hiltViewModel()` 호출
- Repository, UseCase, DataSource 직접 참조
- API/Domain 호출
- navigation 직접 수행

## Stateful / Stateless 기준

- Route/ScreenRoot는 stateful이어도 된다.
- Screen은 가능한 stateless로 유지한다.
- `remember`는 UI 지역 상태에 한정한다.
- 서버/도메인 상태는 ViewModel과 UiState로 관리한다.

`rememberSaveable`은 화면 회전이나 process recreation 후에도 유지해야 하는 UI 입력값에만 사용한다.
서버에서 다시 가져올 수 있는 값은 `rememberSaveable`에 저장하지 않는다.

`LaunchedEffect` key는 side effect가 다시 실행되어야 하는 조건만 넣는다.
무조건 한 번만 실행할 수집은 `LaunchedEffect(Unit)`을 사용한다.

## 파라미터 순서

권장 순서:

1. 필수 state
2. 필수 callback
3. optional 값
4. `modifier: Modifier = Modifier`

## 파일 내 함수 정렬

1. Public Route 또는 ScreenRoot
2. Public Screen
3. Private section composable
4. Private item composable
5. Preview

## Compose 패키지 분리

화면 구현이 커질 때는 기능 패키지 아래에 `compose`와 `component`를 분리한다.

권장 구조:

```text
presentation/{feature}/
  navigation/
  compose/
    {Feature}Route.kt 또는 {Feature}ScreenRoot.kt
    {Feature}Screen.kt
  component/
    {Feature}Header.kt
    {Feature}Item.kt
    {Feature}Dialog.kt
  model/
```

규칙:
- `compose`는 화면 진입점, Screen, 화면 레이아웃 조립을 담는다.
- `component`는 반복 사용되는 section/item/dialog/bottom sheet/toolbar 등 작은 UI 조각을 담는다.
- `component` 안의 Composable은 가능한 stateless로 유지하고 state와 callback을 파라미터로 받는다.
- `component`에서 ViewModel, Navigator, Repository, UseCase, API를 직접 참조하지 않는다.
- 한 화면에만 쓰이는 작은 private Composable은 같은 `compose` 파일 아래에 둘 수 있다.
- 파일이 커지거나 다른 화면에서도 재사용되면 `component`로 옮긴다.

## Preview

- 화면 단위 Screen에는 Preview를 둔다.
- Preview는 파일 하단에 둔다.
- Preview 함수는 `private`로 둔다.
- Preview에서 `TODO()`를 사용하지 않는다.
- Preview에는 dummy 값을 직접 제공한다.
- Preview가 ViewModel, Repository, API에 의존하지 않게 한다.

## Lifecycle

- Composable에서 Flow state를 수집할 때는 `collectAsStateWithLifecycle()`를 사용한다.
- 단순 `collectAsState()`는 특별한 이유가 있을 때만 사용한다.
