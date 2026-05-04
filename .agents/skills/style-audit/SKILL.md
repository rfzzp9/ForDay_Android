---
name: style-audit
description: >
  Forday Android 코드 스타일 감사 스킬. style-auditor 에이전트 전용.
  AGENTS.md 네이밍 컨벤션, Preview 함수 존재, UiState 기본값, 함수/파일 크기,
  코드 중복, TODO 방치, 사용하지 않는 import, logEvent 호출 누락을 감사한다.
---

# Style Audit

style-auditor 에이전트가 Forday Android 코드 스타일을 감사할 때 따르는 체크리스트.  
판단 기준: `app/AGENTS.md`의 코딩 규칙 섹션.

## 감사 항목

### 1. 네이밍 컨벤션 (MAJOR)

```bash
# ViewModel 네이밍 패턴 확인
find app/src/main/java/com/forday/app/presentation/ -name "*ViewModel*" \
  | grep -v "Base\|test"

# UiState 네이밍 확인
find app/src/main/java/com/forday/app/presentation/ -name "*UiState*"

# UseCase 네이밍 확인 (동사 + 명사 + UseCase)
find app/src/main/java/com/forday/app/domain/usecase/ -name "*.kt"

# NavKey 클래스명 확인 (Route.kt)
cat app/src/main/java/com/forday/app/core/navigation/Route.kt
```

위반 패턴:
- `FooVM`, `FooViewmodel` → `FooViewModel`
- `FooState` → `FooUiState`  
- `FooEffect` → `FooSideEffect`
- `GetFoo`, `FooUsecase` → `GetFooUseCase`

### 2. Screen 파일 분리 (MAJOR)

```bash
# ScreenRoot 없이 Screen만 있는 feature 탐지
for dir in app/src/main/java/com/forday/app/presentation/*/; do
  has_root=$(find "$dir" -name "*ScreenRoot*" | wc -l)
  has_screen=$(find "$dir" -name "*Screen*" | grep -v "Root" | wc -l)
  if [ "$has_screen" -gt 0 ] && [ "$has_root" -eq 0 ]; then
    echo "ScreenRoot 없음: $dir"
  fi
done
```

### 3. Preview 함수 (MINOR)

```bash
# Screen 파일에서 @Preview 존재 여부
grep -rL "@Preview" \
  app/src/main/java/com/forday/app/presentation/ --include="*Screen.kt" \
  | grep -v "Root\|test"
```

`@Preview` 없는 Screen 파일은 MINOR.

### 4. UiState 필드 기본값 (MAJOR)

```bash
# UiState data class에서 기본값 없는 필드 탐지
grep -rn "data class.*UiState" -A 15 \
  app/src/main/java/com/forday/app/presentation/ --include="*.kt" \
  | grep "val " | grep -v "=\|//\|test"
```

기본값 없는 `val` 필드: MAJOR — `hiltViewModel()` 초기화 전 접근 시 오류 가능.

### 5. 함수/파일 크기 (MINOR)

```bash
# 파일 크기 300줄 초과 탐지
wc -l app/src/main/java/com/forday/app/**/*.kt 2>/dev/null \
  | sort -rn | awk '$1 > 300 {print $0}' | head -20
```

300줄 초과 파일은 MINOR (분리 검토 권장).

### 6. 코드 중복 (MAJOR)

수동 탐지 — 동일 로직이 3개 이상 파일에서 반복되는지 확인:

```bash
# 자주 복사되는 패턴 탐지 (flow { emit() }.catch { }.collect 패턴 외)
grep -rn "flow {" app/src/main/java/com/forday/app/presentation/ --include="*.kt" \
  | wc -l

# 유사 Composable 패턴 탐지 (동일 레이아웃 구조 반복)
grep -rn "ForDayScreenWrapper\|OnboardingLayout" \
  app/src/main/java/com/forday/app/presentation/ --include="*.kt"
```

3회 이상 반복되는 UI 구조나 로직: MAJOR (공통 컴포넌트 추출 권장).

### 7. 방치된 코드 (MINOR)

```bash
# TODO / FIXME 주석 탐지
grep -rn "TODO\|FIXME\|HACK\|XXX" \
  app/src/main/java/ --include="*.kt" | grep -v "//.*TODO.*:"

# 주석 처리된 코드 블록 탐지 (연속 3줄 이상)
grep -rn "^//.*\b\(val\|var\|fun\|class\|if\|for\)" \
  app/src/main/java/ --include="*.kt" | head -30

# 사용하지 않는 import (단순 탐지)
grep -rn "^import " app/src/main/java/ --include="*.kt" \
  | grep "unused\|Unused" | head -20
```

기한 없는 TODO/FIXME: MINOR.

### 8. logEvent 호출 누락 (MINOR)

```bash
# ScreenRoot에서 logEvent 호출 여부
grep -rL "logEvent\|logScreen" \
  app/src/main/java/com/forday/app/presentation/ --include="*ScreenRoot*" \
  | grep -v "test"
```

`logEvent` 없는 ScreenRoot: MINOR (Firebase Analytics 누락).

### 9. @SerialName vs @SerializedName (CRITICAL)

```bash
# Gson 어노테이션 잘못 사용 탐지 (Kotlinx Serialization 프로젝트)
grep -rn "@SerializedName" app/src/main/java/ --include="*.kt" \
  | grep -v "//\|test"
```

발견 시: CRITICAL — 런타임 역직렬화 오류.

## 리포트 형식

```markdown
## 코드 스타일 감사 결과

### CRITICAL
- [파일경로:라인] 문제 설명
  수정: @SerializedName → @SerialName

### MAJOR
- [파일경로:라인] 네이밍 위반: FooVM → FooViewModel
- [파일경로:라인] UiState 기본값 누락: val items: List<Item>

### MINOR
- [파일경로:라인] @Preview 누락
- [파일경로:라인] TODO 주석 방치 (기한 없음)

### 통과 항목
- 네이밍 컨벤션 전반 ✓
- ...
```
