# RAILDOCK - SERVER

> AI 기반 철도 시설물 유지보수 의사결정 지원 플랫폼 - 백앤드 서버

## 프로젝트 개요

- KT AIVLE School 8기 AI 트랙 7반 14조 빅프로젝트
- 기간: 2026.01 - 2025.02(8주)

## 기술 스택

- 개발: Spring Boot, Kotlin, MySQL
- 배포: Docker, AWS(S3, Pipeline, CodeBuild, CodeDeploy, EC2, RCS)

## 주요 기능

- 세션 기반 Authentication
- 철도 주행영상 업로드 후 결함 탐지 및 분석
- 분석에 사용되는 규정 문서 CRUD
- 규정 문서, 결함 정보들을 RAG로 활용한 지능형 챗봇

## 로컬 환경 및 실행방법
1. docker, java 21 설치
2. application-dev.yml 파일 작성 (aws s3 시크릿키 발급받아서 사용)

```yaml
aws:
  credentials:
    access-key: YOUR_ACCESS_KEY
    secret-key: YOUR_SECRET_KEY
integration:
  llm:
    url: LLM_API_URL
  vision:
    url: VISION_API_URL
```
3. `docker compose up` - MySQL 실행
4. `gradlew bootRun --args='--spring.profiles.active=dev'` - 서버 실행

## 결과 및 성과