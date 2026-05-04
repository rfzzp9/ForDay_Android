---
name: code-review
description: >
  Forday Android 코드 리뷰와 감사 스킬. 전체 코드 리뷰, PR 리뷰, 변경사항 리뷰,
  아키텍처 감사, 보안 감사, 성능 감사, 스타일 감사에 사용한다.
---

# Code Review

## 목적

직접 구현보다 발견 사항, 위험, 누락된 검증을 우선해야 하는 독립 리뷰 작업에 사용한다.

## 트리거 예시

- 전체 코드 리뷰
- PR 리뷰
- 변경 파일 리뷰
- 아키텍처 감사
- 보안 감사
- 성능 감사
- 스타일 감사
- 이 변경사항 리뷰해줘

## 사용하지 않는 경우

- 일반 개발 흐름에서 기능을 만들거나 수정하는 작업: `android-dev` 사용
- 보고된 버그의 원인 분석: `bug-analysis` 사용

## 먼저 볼 문서

1. `.agents/docs/workflows/code-review.md`
2. `.agents/docs/llm-wiki/project-map.md`
3. `.agents/docs/llm-wiki/architecture-map.md`
4. `.agents/docs/conventions/architecture-conventions.md`
5. `.agents/docs/conventions/compose-style.md`
6. `.agents/docs/conventions/kotlin-style.md`
7. `.agents/docs/conventions/testing-conventions.md`

## 리뷰 관점

- 아키텍처: 레이어 방향, Mapper 체인, DI, Navigation
- 보안: secret, token 처리, 민감정보 로그, HTTP/deeplink 위험
- 성능: Compose recomposition, lifecycle-aware collection, list key, image loading
- 스타일: 네이밍, 파일 크기, Preview, TODO/FIXME, import, UiState 기본값

## 작업 규칙

- 심각도 높은 발견 사항부터 제시한다.
- 각 finding에는 파일과 라인을 포함한다.
- 단순 취향 위주의 코멘트는 피한다.
- 같은 원인의 중복 finding은 하나로 합친다.
- 검증하지 못한 테스트나 빌드가 있으면 명시한다.

## 최종 응답

- finding 우선
- severity
- file/line
- risk
- suggested fix
- test gap
