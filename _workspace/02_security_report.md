# Security Audit Report
**Scope:** `app/src/main/java/com/forday/app/` + AndroidManifest.xml + build.gradle.kts + proguard-rules.pro

## 1. 하드코딩 시크릿 (CRITICAL)
- **`app/build.gradle.kts:36, 40`**: 
  - `storePassword = "wony2401"` (평문)
  - `keyPassword = "wony2401"` (평문)
  - `local.properties` 기반 `getProperty` 주석처리된 상태
  - **CRITICAL: 릴리스 키스토어 패스워드가 커밋된 소스에 노출됨**
- `BuildConfig.KAKAO_API_KEY`는 local.properties 기반 → OK (양호)
- 코드 내 `api_key`, `secret` 등 하드코딩 없음 (enum property 이름의 `apiKey`는 의도된 enum 값)

## 2. HTTP URL / Cleartext Traffic (MAJOR)
- `AndroidManifest.xml:28`: `android:usesCleartextTraffic="true"` — 프로덕션 릴리스에서 HTTP 허용
  - `networkSecurityConfig`도 설정되지 않음 → 모든 도메인 HTTP 허용
  - 권장: 개발 빌드에만 허용 (BuildType별 manifest 또는 `network_security_config.xml`에 도메인 화이트리스트)

## 3. SSL/TLS 검증 우회
- `hostnameVerifier`, `trustManager`, `SSLContext` 사용 없음 (양호)

## 4. 로그 민감정보 누출 (CRITICAL)
프로덕션 빌드에서도 `Timber.plant(DebugLogTree)`가 그대로 실행됨 (`LoggerModule.kt:67-72`). `BuildConfig.DEBUG` 가드 없음. 누출 발생 지점:

- `data/impl/AuthRepositoryImpl.kt:62-63` — `saveKakaoToken success` (ok이나 `Log.e` 남용)
- `data/impl/AuthRepositoryImpl.kt:115` — `accessToken=${loginResponse.data.accessToken}` **(토큰 평문 출력)**
- `data/impl/AuthRepositoryImpl.kt:192, 199` — `switchAccount 토큰: ${accessToken} ${refreshToken}` **(토큰 평문 출력)**
- `presentation/mypage/MyPageViewModel.kt:689, 705` — `token.accessToken` 평문 출력
- `presentation/mypage/MyPageViewModel.kt:734` — 카카오 토큰 + error message 출력
- `presentation/onboarding/OnboardingViewModel.kt:109` — `accessToken` uiState 값 출력
- `presentation/onboarding/OnboardingViewModel.kt:730, 757, 842` — `accessToken`, `kakaoAccessToken` 출력 **(Log.e 직접 사용)**
- `remote/api/interceptor/TokenInterceptor.kt:30~50` — 모든 요청/응답의 헤더, 본문, Authorization 토큰 평문 출력 (**가장 심각**: 모든 API 응답 바디가 Timber 미사용 원시 `Log.d` 로 출력됨)
- `presentation/main/MainActivity.kt:46, 51, 73, 83-87` — KeyHash 로그 (사용 빈도는 낮지만 릴리스에서 불필요)

## 5. DebugTree 조건부 설치 (MAJOR)
- `LoggerModule.kt:67-72`: `DebugLogTree()`가 **Debug/Release 공통**으로 주입됨 (코멘트 명시)
- 권장: `if (BuildConfig.DEBUG) trees.add(DebugLogTree())` 가드 필수

## 6. BuildConfig 활용
- `BASE_URL`, `KAKAO_API_KEY`, `KAKAO_REDIRECT_URI` → `local.properties` 기반 (양호)
- `buildConfigField`, `resValue` 분리 적절

## 7. 기타 Manifest 보안
- `android:allowBackup="true"` (Line 19) — 민감 데이터가 DataStore 에 저장됨에도 백업 허용
  - `backup_rules.xml`, `data_extraction_rules.xml` 존재 → 내용 확인 필요
- `android:exported` 설정: MainActivity exported="true" (론처이므로 OK)

## 8. ProGuard/R8
- 릴리스 `isMinifyEnabled = true` 설정됨 (양호)
- `-keepattributes SourceFile,LineNumberTable` 켜져있음 (Crashlytics 스택트레이스 위해 필요)
- 모델 클래스 전부 `-keep` 처리 → 공격자가 역공학 시 데이터 구조 노출 용이 (운영 trade-off)

## 9. Gson tolerant mode
- `AuthDataSourceImpl.kt:33` — `GsonBuilder().setLenient()` 등 사용 가능성, 원본 응답 직접 역직렬화 (OK)

## 심각도 요약
- CRITICAL: 2건 (키스토어 패스워드 평문, 릴리스 로그에 토큰 노출)
- MAJOR: 2건 (DebugTree 무조건 설치, cleartext traffic)
- MINOR: 1건 (allowBackup)
