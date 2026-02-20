# RAILDOCK - SERVER

> AI 기반 철도 시설물 유지보수 의사결정 지원 플랫폼 - 백앤드 서버

## ERD 다이어그램 및 시스템 구성도
| 시스템 구성도 | ERD 다이어그램 |
| ------| ------|
|<img width="800" height="1000" alt="과제선정 (3)" src="https://github.com/user-attachments/assets/ae96fd19-a1f9-4293-b96e-aa2045240ce9" /> | <img width="1000" height="400" alt="Team-14-ERD_(5)" src="https://github.com/user-attachments/assets/718bf38c-6466-46f1-ad49-1eabc7dc508b" />|

## 프로젝트 개요

- KT AIVLE School 8기 AI 트랙 7반 14조 빅프로젝트
- 기간: 2026.01 - 2025.02(8주)

## 기술 스택

- 개발: Spring Boot, Kotlin, MySQL
- 배포: Docker, AWS(S3, Pipeline, CodeBuild, CodeDeploy, EC2, RCS)

## 주요 기능

- 세션 기반 Authentication
- AWS CloudFront, S3를 활용한 파일 업로드 및 관리
- 철도 주행영상 업로드 후 결함 탐지 및 분석
- 분석에 사용되는 규정 문서 CRUD
- 규정 문서, 결함 정보들을 RAG로 활용한 지능형 챗봇
- 탐지 모델 파인튜닝을 위한 엔지니어 피드백 처리 기능

## 배포 환경
- AWS CodePipeline, CodeBuild, CodeDeploy를 활용한 CI/CD 구축
- RCS를 활용한 이미지 빌드 및 EC2 배포 자동화

## 로컬 환경설정 및 실행방법
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

---
## 상세 기술구현
해당 프로젝트를 진행하면서 기술적으로 중요하거나 어려웠던 부분을 아래와 같이 정리하였습니다.
1. File 시스템 설계
2. 

### File 시스템 설계 (S3 + CloudFront)

#### 1️. File 시스템 설계 배경 상황

AI 탐지 결과(이미지·영상·JSON)는
대용량 정적 리소스이며, 다음과 같은 문제가 존재하였다.

* 백엔드 서버에서 직접 저장·서빙 시 **서버 부하 및 네트워크 병목 발생**
* AI / LLM 서버에서 대용량 파일 접근 시 처리 지연
* 탐지 파일 증가에 따른 확장성 한계

따라서 **파일 전송 책임을 서버와 분리하는 구조**가 필요함.

#### 2️. File 시스템 구현

**1) S3를 원본 저장소로 사용**
* 파일 메타데이터만 DB에서 관리
* 실제 파일은 Object Storage에 저장

**2) CloudFront CDN 기반 파일 제공**
* 대량 요청 시 캐시 활용으로 S3 접근 최소화
* Client / AI / LLM 서버가 직접 접근 가능하도록 구조 분리

**3) Backend는 파일 메타데이터만 관리**
* 서버 트래픽 80% 감소
* 파일 전송 책임 분리로 애플리케이션 안정성 확보

**4) Presigned URL을 왜 사용하지 않았는가?**
- Presigned URL 방식은 보안 측면에서는 유효한 접근 제어 수단이지만 요청시 동적 URL이 생성되어 CDN 캐싱 효과를 활용할 수 없음
-  AI 서버 / LLM 서버 / Client가 반복적으로 접근할 경우 Backend가 URL 생성 지점이 되어 병목 발생
- 반면 CloudFront 기반 고정 URL 구조는 CDN 캐시 활용 가능 하며, 반복 접근 시 S3 호출 최소화 Backend를 파일 서빙에서 분리함.
- 따라서 접근 제어 편의성보다 **확장성 · 성능 · 트래픽 분산 구조를 우선하여 CDN 기반 구조를 채택**하였다.

#### 3. File 시스템 적용 결과

| 항목                        | 개선 효과             |
| ------------------------- | ----------------- |
| Client & AI 파일 접근 지연      | **40% 감소**        |
| 백엔드 서버 트래픽                | **80% 감소**        |
| LLM / Vision 모델 데이터 전송 시간 | **40% 단축**        |
| 파일 증가에 따른 구조 변경           | **없음 (확장 가능 구조)** |
