# Architecture Conventions

## 목적

Clean Architecture 레이어 규칙과 Mapper/DI 기준을 정리한다.

## 의존성 방향

```text
Presentation -> Domain -> Data -> Remote
```

금지:
- Presentation에서 Data/Remote 구현체 직접 참조
- ViewModel에서 RepositoryImpl 또는 DataSource 직접 참조
- Remote DTO를 Presentation까지 전달

## Mapper

권장 흐름:

```text
Response -> Entity -> Domain -> UiModel
```

규칙:
- Response는 nullable을 안전하게 처리한다.
- Entity와 Domain은 가능한 non-null로 유지한다.
- list 변환은 null-safe 기본값을 둔다.
- Presentation 전용 표시 값은 UiModel에서 만든다.

## UiState

- 화면 상태는 불변 객체로 유지한다.
- 기본값을 안전하게 둔다.
- sealed interface 또는 명확한 data class 구조를 사용한다.
- 상태 변경은 `_uiState.update { ... }`를 우선한다.

## DI

- Repository interface와 구현체는 `RepositoryModule`에 등록한다.
- DataSource interface와 구현체는 `RemoteDataSourceModule`에 등록한다.
- Retrofit API는 `NetworkModule`에서 제공한다.
- 누락된 binding은 빌드 시점 오류를 만들기 쉬우므로 새 흐름 추가 시 반드시 확인한다.
