# Kotlin Style

## 목적

Forday Android Kotlin 코드 작성 규칙을 정리한다.

## 네이밍

- 클래스, 인터페이스: PascalCase
- 함수, 변수: camelCase
- 상수: `UPPER_SNAKE_CASE`
- ViewModel: `{Feature}ViewModel`
- UiState: `{Feature}UiState`
- UiEffect 또는 SideEffect: `{Feature}UiEffect` 또는 `{Feature}SideEffect`
- UseCase: `{Verb}{Target}UseCase`
- Repository 구현체: `{Domain}RepositoryImpl`

## 함수 작성

- UseCase는 `operator fun invoke()`를 우선한다.
- suspend 작업은 suspend 함수 또는 Flow로 명확히 드러낸다.
- UI callback 이름은 `onClick`, `onBack`, `onNext`, `onValueChange`처럼 이벤트 중심으로 쓴다.

## Import

- wildcard import를 피한다.
- IDE 정렬을 따르되, 사용하지 않는 import는 남기지 않는다.

## 주석

- 코드가 스스로 설명하면 주석을 달지 않는다.
- TODO는 이유와 후속 작업이 분명할 때만 남긴다.
- 기한 없는 TODO/FIXME는 PR 전 정리한다.
