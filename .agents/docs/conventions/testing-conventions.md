# Testing Conventions

## 목적

테스트를 어디에 추가하고 무엇을 우선 검증할지 정리한다.

## 우선순위

1. Mapper null/default 처리
2. UseCase 비즈니스 규칙
3. Repository data flow
4. ViewModel UiState 전환
5. Compose UI preview 또는 UI test

## 위치

- 단위 테스트: `app/src/test/java/`
- Android UI 테스트: `app/src/androidTest/java/`

## 검증 도구

- Flow 검증은 Turbine 사용을 우선한다.
- UI snapshot이 필요한 경우 Roborazzi 사용을 고려한다.
- 외부 의존성은 fake 구현체를 우선한다.

## PR 전 보고

- 실행한 명령
- 통과/실패 결과
- 실패했다면 원인과 남은 작업
