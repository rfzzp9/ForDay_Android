---
name: bug-analysis
description: >
  Forday Android 버그 원인 분석 스킬. 크래시, 잘못된 UI 상태, API/데이터 불일치,
  Navigation 오류, DI 실패, DataStore 문제에 사용한다.
---

# Bug Analysis

깨진 동작과 원인 분석에 사용한다. 먼저 아래 문서를 읽는다.

1. `.agents/docs/workflows/bugfix.md`
2. `.agents/docs/llm-wiki/feature-map.md`
3. `.agents/docs/llm-wiki/architecture-map.md`
4. `.agents/docs/conventions/architecture-conventions.md`

수정은 원인에 맞는 최소 범위로 제한한다. 새 기능 작업은 `android-dev`로 보낸다.
