---
keyflow_id: sys_ffd503ac2365
status: draft
type: ai-generated
---

# Forday Android

## 하네스: Android 앱 개발

**목표:** Clean Architecture 기반 Forday Android 앱의 기능 개발, 버그 수정, 코드 리뷰를 에이전트 팀으로 자동화

**트리거:** Android 개발 관련 작업 (기능 추가, 버그 수정, 코드 검토, 리팩토링, API 연동) 요청 시 `android-dev` 스킬을 사용하라. 단순 개념 질문은 직접 응답 가능.

**프로젝트 상세:** `app/CLAUDE.md` 참조 (아키텍처, 코드 패턴, 컴포넌트 설명)

## 하네스: 종합 코드 리뷰

**목표:** 아키텍처·보안·성능·스타일을 병렬 감사하여 하나의 우선순위 정렬 리포트로 통합

**트리거:** "종합 리뷰", "전체 코드 리뷰", "PR 리뷰", "코드 감사", "아키텍처 검사", "보안 검사", "성능 분석", "스타일 검사" 등의 요청 시 `code-review` 스킬을 사용하라.\
개발 플로우 내 단일 변경 빠른 검토는 `android-dev` 스킬(기존).

**변경 이력:**

| 날짜 | 변경 내용 | 대상 | 사유 |
| --- | --- | --- | --- |
| 2026-04-18 | 초기 구성 | 전체 | Android 앱 개발 하네스 신규 구축 |
| 2026-04-18 | 종합 코드 리뷰 하네스 추가 | 전체 | 아키텍처·보안·성능·스타일 병렬 감사 |
| 2026-04-19 | 트리거 충돌 수정 | android-dev/SKILL.md, code-review/SKILL.md, [android-orchestrator.md](http://android-orchestrator.md) | android-dev와 code-review 스킬 간 "코드 리뷰" 키워드 충돌 해소 |
