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

## Screen

역할:
- 순수 UI 렌더링
- state와 callback만 파라미터로 받기
- ViewModel 직접 참조 금지
- Navigator 직접 참조 금지

## Stateful / Stateless 기준

- Route/ScreenRoot는 stateful이어도 된다.
- Screen은 가능한 stateless로 유지한다.
- `remember`는 UI 지역 상태에 한정한다.
- 서버/도메인 상태는 ViewModel과 UiState로 관리한다.

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

## Preview

- 화면 단위 Screen에는 Preview를 둔다.
- Preview는 파일 하단에 둔다.
- Preview 함수는 `private`로 둔다.
- Preview에서 `TODO()`를 사용하지 않는다.

## Lifecycle

- Composable에서 Flow state를 수집할 때는 `collectAsStateWithLifecycle()`를 사용한다.
- 단순 `collectAsState()`는 특별한 이유가 있을 때만 사용한다.
