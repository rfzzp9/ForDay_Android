---
name: bug-analysis
description: >
  Forday Android 버그 원인 분석 스킬. 크래시, 잘못된 UI 상태, API/데이터 불일치,
  Navigation 오류, DI 실패, DataStore 문제에 사용한다.
---

# Bug Analysis

## 목적

깨진 동작의 원인을 분석하고 최소 범위로 수정할 때 사용한다.

## 트리거 예시

- 앱이 크래시 나
- 저장하면 앱이 죽어
- 화면 상태가 이상해
- 표시 데이터가 원본과 안 맞아
- 왜 안 되는지 분석해줘

## 사용하지 않는 경우

- 새 기능 구현: `android-dev` 사용
- 전체 리뷰 또는 감사: `code-review` 사용

## 먼저 볼 문서

1. `.agents/docs/workflows/bugfix.md`
2. `.agents/docs/llm-wiki/feature-map.md`
3. `.agents/docs/llm-wiki/architecture-map.md`
4. `.agents/docs/conventions/architecture-conventions.md`
5. `.agents/docs/conventions/compose-style.md`

## 작업 규칙

- 먼저 증상을 재현하거나 명확히 설명한다.
- 원인이 명확히 지역적이지 않으면 Presentation -> Domain -> Data -> Remote 순서로 추적한다.
- 증상을 설명하는 가장 작은 레이어에서 수정한다.
- 디버깅 중 관련 없는 리팩토링은 하지 않는다.
- 원인, 변경 파일, 검증 결과를 함께 보고한다.

## 최종 응답

- 증상
- 원인
- 수정 내용
- 검증 결과
- 재발 방지 또는 남은 위험
