---
name: security-audit
description: >
  Forday Android 보안 취약점 감사 스킬. security-auditor 에이전트 전용.
  API 키 하드코딩, 토큰 평문 저장, Timber 로그 민감 정보 유출, HTTP 평문 통신,
  ProGuard 난독화 누락, BuildConfig 시크릿 분리, 딥링크 검증을 감사한다.
---

# Security Audit

security-auditor 에이전트가 Forday Android 보안 취약점을 감사할 때 따르는 체크리스트.

## 감사 항목

### 1. API 키 / 시크릿 하드코딩 (CRITICAL)

```bash
# 소스 코드에 하드코딩된 키 탐지
grep -rn "api_key\|apikey\|secret\|password\|token" \
  app/src/main/java/ --include="*.kt" -i \
  | grep -v "//\|test\|BuildConfig\|local.properties"

# BuildConfig 미사용 시크릿 탐지
grep -rn "\"https://\|\"http://\|\"sk-\|\"Bearer " \
  app/src/main/java/ --include="*.kt"
```

발견 시: CRITICAL — `local.properties` + `BuildConfig`로 이동 필요.

### 2. 토큰 저장 보안 (MAJOR)

```bash
# DataStore에 토큰 저장 코드 확인
grep -rn "accessToken\|refreshToken\|KAKAO" \
  app/src/main/java/com/forday/app/core/datastore/ --include="*.kt"
```

평문 DataStore 저장은 MAJOR (루팅 기기에서 접근 가능). `EncryptedDataStore` 또는 `Android Keystore` 연동 권장.

### 3. 로그 민감 정보 유출 (MAJOR)

```bash
# Timber/Log에 토큰·개인정보 출력 탐지
grep -rn "Timber\.\|Log\." app/src/main/java/ --include="*.kt" \
  | grep -i "token\|password\|email\|phone\|key\|secret"
```

발견 시: MAJOR — 릴리즈 빌드에서 Timber DebugTree가 비활성화되는지 확인 후 판단.

```bash
# 릴리즈 빌드에서 Timber 초기화 확인
grep -rn "DebugTree\|Timber.plant" \
  app/src/main/java/ --include="*.kt"
```

### 4. 네트워크 보안 (MAJOR)

```bash
# HTTP(비암호화) URL 사용 탐지
grep -rn "http://" app/src/main/java/ --include="*.kt" \
  | grep -v "//\|test\|localhost"

# OkHttp 인증서 검증 우회 탐지
grep -rn "hostnameVerifier\|trustManager\|SSLContext" \
  app/src/main/java/ --include="*.kt"
```

`http://` 프로덕션 URL: CRITICAL. 인증서 검증 우회: CRITICAL.

### 5. Network Security Config (MAJOR)

```bash
# AndroidManifest.xml에서 networkSecurityConfig 확인
grep -n "networkSecurityConfig\|usesCleartextTraffic" \
  app/src/main/AndroidManifest.xml
```

`android:usesCleartextTraffic="true"` 프로덕션 빌드: MAJOR.

### 6. ProGuard 설정 (MAJOR)

```bash
# proguard-rules.pro 내용 확인
cat app/proguard-rules.pro
```

확인 항목:
- NavKey 서브클래스 보존 규칙 존재 여부
- Serializable 모델 클래스 보존 규칙
- 카카오 SDK 규칙
- Retrofit/OkHttp 규칙

규칙 누락 시 MAJOR (릴리즈 빌드 크래시 가능).

### 7. 딥링크 파라미터 검증 (MINOR)

```bash
# Intent 파라미터를 직접 사용하는 코드 탐지
grep -rn "intent\?.data\|getStringExtra\|getParcelableExtra" \
  app/src/main/java/ --include="*.kt"
```

검증 없는 딥링크 파라미터 사용: MINOR (입력 검증 추가 권장).

### 8. BuildConfig 시크릿 분리 확인 (INFO)

```bash
# BuildConfig 활용 현황 확인
grep -rn "BuildConfig\." app/src/main/java/ --include="*.kt" \
  | grep -v "//\|test"

# local.properties에서 읽어 BuildConfig에 주입하는지 확인
grep -n "buildConfigField\|getProperty" app/build.gradle.kts
```

시크릿이 모두 `BuildConfig`를 통해 접근되면 ✓.

## 리포트 형식

```markdown
## 보안 취약점 감사 결과

### CRITICAL
- [파일경로:라인] 취약점 설명
  공격 시나리오: ...
  수정 방법: ...

### MAJOR
- [파일경로:라인] 취약점 설명
  위험도: ...
  수정 방법: ...

### MINOR
- [파일경로:라인] 개선 권장

### 통과 항목
- BuildConfig 시크릿 분리 ✓
- ...
```
