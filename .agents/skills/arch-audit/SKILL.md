---
name: arch-audit
description: >
  Forday Android Clean Architecture 감사 스킬. architecture-auditor 에이전트 전용.
  레이어 의존성 방향, 레이어 역전 위반, Mapper 체인 완결성, Hilt DI 등록,
  ScreenRoot/Screen 분리, UseCase 우회, UiState 불변성을 검사한다.
  상세 위반 패턴과 수정 예시는 references/arch-violations.md 참조.
---

# Architecture Audit

architecture-auditor 에이전트가 Forday Android 아키텍처를 감사할 때 따르는 체크리스트와 판단 기준.

## 감사 항목

### 1. 레이어 의존성 방향 (CRITICAL)

올바른 방향: `Presentation → Domain ← Data ← Remote`

```bash
# 위반 탐지: Presentation이 data/remote 패키지를 import하는지 확인
grep -r "import com.forday.app.data" app/src/main/java/com/forday/app/presentation/
grep -r "import com.forday.app.remote" app/src/main/java/com/forday/app/presentation/
```

발견 시: CRITICAL — 레이어 역전. UseCase/Repository interface를 통해 접근해야 함.

### 2. UseCase 우회 (MAJOR)

```bash
# ViewModel이 Repository를 직접 의존하는지 확인
grep -rn "Repository" app/src/main/java/com/forday/app/presentation/ \
  | grep -v "//\|test\|Test"
```

비즈니스 로직이 포함된 Repository 직접 의존은 MAJOR. 단순 조회(UseCase wrapping 불필요)는 MINOR.

### 3. Mapper 체인 완결성 (MAJOR)

각 레이어별 필수 구현 확인:

```bash
# RemoteMapper 미구현 Response 탐지
grep -rn "data class.*Response" app/src/main/java/com/forday/app/remote/model/response/ \
  | grep -v "RemoteMapper"

# DataMapper 미구현 Entity 탐지
grep -rn "data class.*Entity" app/src/main/java/com/forday/app/data/model/ \
  | grep -v "DataMapper"
```

### 4. Hilt DI 등록 누락 (CRITICAL)

```bash
# RepositoryImpl 목록 vs RepositoryModule @Binds 비교
ls app/src/main/java/com/forday/app/data/impl/
cat app/src/main/java/com/forday/app/data/di/RepositoryModule.kt

# DataSourceImpl 목록 vs RemoteDataSourceModule @Binds 비교
ls app/src/main/java/com/forday/app/remote/impl/
cat app/src/main/java/com/forday/app/remote/di/RemoteDataSourceModule.kt
```

`*Impl` 파일 수 > 해당 Module의 `@Binds` 수이면 등록 누락.

### 5. ScreenRoot / Screen 분리 (MAJOR)

```bash
# Screen 파일(ScreenRoot 아님)에서 hiltViewModel() 호출 탐지
grep -rn "hiltViewModel()\|viewModel()" \
  app/src/main/java/com/forday/app/presentation/ \
  | grep -v "ScreenRoot\|Root\|test"
```

### 6. UiState 불변성 (MAJOR)

```bash
# UiState에 var 필드 탐지
grep -rn "data class.*UiState" -A 20 \
  app/src/main/java/com/forday/app/presentation/ \
  | grep "var "
```

`var` 필드 발견 시 MAJOR — 모든 필드는 `val`, 변경은 `copy()`로.

### 7. StateFlow 업데이트 패턴 (MAJOR)

```bash
# _uiState.value = 직접 재할당 탐지
grep -rn "_uiState\.value\s*=" \
  app/src/main/java/com/forday/app/presentation/
```

`_uiState.update { }` 대신 직접 재할당 시 MAJOR.

## 리포트 형식

```markdown
## 아키텍처 감사 결과

### CRITICAL
- [파일경로:라인] 문제 설명
  수정 방향: ...

### MAJOR
- [파일경로:라인] 문제 설명
  수정 방향: ...

### MINOR
- [파일경로:라인] 개선 제안

### 통과 항목
- 레이어 의존성 방향 ✓
- ...
```

상세 위반 패턴 및 수정 예시: `references/arch-violations.md`
