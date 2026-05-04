---
name: android-dev
description: >
  Forday Android 기능 개발 스킬. 화면, ViewModel, UseCase, Repository, API 연동,
  Navigation, Compose UI, 리팩토링, 개발 중 단일 변경 확인 요청에 사용한다.
---

# Android Dev

## 목적

Android 앱 코드를 만들거나, 수정하거나, 연결하거나, 리팩토링할 때 사용한다.

## 트리거 예시

- 기능 만들어줘
- 화면 추가해줘
- API 연동해줘ㄷ
- ViewModel 작성해줘
- 이 흐름 리팩토링해줘
- 구현 수정해줘
- 방금 바꾼 코드 확인해줘

## 사용하지 않는 경우

- 전체 리뷰, PR 리뷰, 아키텍처/보안/성능/스타일 감사: `code-review` 사용
- 크래시나 오동작의 원인 분석: `bug-analysis` 사용
- 코드 변경이 필요 없는 단순 개념 질문

## 먼저 볼 문서

1. `.agents/docs/workflows/feature-development.md`
2. `.agents/docs/llm-wiki/project-map.md`
3. `.agents/docs/llm-wiki/feature-map.md`
4. `.agents/docs/llm-wiki/add-feature-file-locations.md`
5. `.agents/docs/conventions/architecture-conventions.md`
6. `.agents/docs/conventions/compose-style.md`
7. `.agents/docs/conventions/kotlin-style.md`

## 작업 규칙

- 새 구조를 만들기 전에 기존 유사 feature를 먼저 확인한다.
- 변경 범위는 요청한 기능과 직접 필요한 연결 코드로 제한한다.
- 작업이 원인 분석 중심으로 바뀌면 `bug-analysis` 기준을 따른다.
- 독립적인 리뷰 요청은 `code-review` 기준을 따른다.
- 코드 변경 후에는 실행한 검증 명령과 결과를 보고한다.

## 최종 응답

- 변경 요약
- 수정 파일
- 검증 결과
- 남은 위험 또는 후속 작업
