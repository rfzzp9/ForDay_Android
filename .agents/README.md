# Agents

## 목적

`.agents`는 AI 협업을 위한 공식 허브다. 공통 문서와 Codex 전용 skills를 한 곳에 둔다.

## 구조

```text
.agents/
  docs/
    llm-wiki/
    conventions/
    workflows/
  skills/
```

## 역할

- `docs/`: Codex, Gemini, Claude가 함께 참조하는 공통 문서
- `skills/`: Codex 실행 지침. 활성 스킬은 `android-dev`, `bug-analysis`, `code-review` 3개만 유지한다.

루트의 `AGENTS.md`, `CLAUDE.md`, `GEMINI.md`는 각 AI가 자동으로 찾는 얇은 진입점이며, 실제 지식은 `.agents/docs/`를 원본으로 한다.
