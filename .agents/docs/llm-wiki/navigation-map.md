# Navigation Map

## 목적

화면 추가나 이동 흐름 수정 시 확인해야 할 위치를 설명한다.

## 주요 파일

- NavKey 정의: `app/src/main/java/com/forday/app/core/navigation/Route.kt`
- 메인 흐름: `app/src/main/java/com/forday/app/presentation/main/MainFlow.kt`
- Navigator: `app/src/main/java/com/forday/app/presentation/main/MainNavigator.kt`
- Navigation state: `app/src/main/java/com/forday/app/presentation/main/MainNavigationState.kt`

## 화면 추가 시 확인

1. `Route.kt`에 NavKey가 필요한지 확인한다.
2. NavKey에 복합 파라미터가 있으면 serialization 등록이 필요한지 확인한다.
3. `MainFlow.kt` 또는 feature navigation 파일에 entry를 등록한다.
4. Route/ScreenRoot에서 `onBack`, `onNext`, `navigator.navigate()` 흐름을 연결한다.

## 주의

- Navigation3 NavKey는 직렬화 가능해야 한다.
- 화면 파라미터는 가능한 한 작고 명시적으로 유지한다.
- Presentation Screen은 Navigator를 직접 알지 않고 callback을 받는 구조를 우선한다.
