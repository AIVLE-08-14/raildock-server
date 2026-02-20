# RAILDOCK - SERVER
## 프로젝트 개요

- KT AIVLE School 8기 AI 트랙 7반 14조 빅프로젝트
- AI 기반 철도 시설물 유지보수 의사결정 지원 플랫폼 - 백앤드 서버
- 기간: 2026.01 - 2025.02(8주)

### 기여자
| ‍이름 | 담당 업무                                                                                                                                                                                                             |
|----------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **서범수** | • [S3 + CloudFront를 활용한 File 시스템 설계 및 구축](#S3-+-CloudFront를-활용한-File-시스템-설계-및-구축) <br> • [Session 로그인 FrontEnd CI/CD 배포 환경](#Session-로그인-FrontEnd-CI/CD-배포-환경) <br> • Problem / Feedback / Document / User domain 설계 및 구현 <br> • LLM Chat bot API 연동 |
| **윤후성** | • 철도 주행 영상 기반 결함 분석 통합 파이프라인 설계 및 구축 <br> • 클라우드 CI/CD 배포 환경 설계 및 구축 <br>  • Problem Detection Domain 설계 및 구현                                                                                                     |
| **성기현** | • VisionModel Finetunning API 연결                                                                                                                                                                                  |

### 주요 기능
| 번호 | 기능                                    | 설명                                                                                                                                                                                                                                                       |
| -- |---------------------------------------| -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1  | **철도 주행 영상 기반 <br> 결함 분석 통합 파이프라인**   | 결함 탐지 영상 업로드 후 Vision Model Inference 호출 <br> 이상 탐지 후 LLM 모델을 호출하여 결과 분석 <br> Vision Model, LLM 분석 결과(JSON, Image)를 수신하여 내부 도메인 모델로 변환 <br> 결함 상태·심각도·위치 정보 정규화 및 DB 저장 <br> 가공된 분석 데이터를 LLM 질의용 포맷으로 재구성 <br> AI → Backend → LLM → Backend으로 이어지는 통합 처리 흐름 설계 |
| 2  | **S3 + CloudFront 기반 <br> 파일 관리 시스템** | 대용량 영상·이미지·JSON 파일을 AWS S3 Object Storage에 저장 <br> CloudFront CDN 기반 고정 URL 구조로 분산 서빙 아키텍처 구성 <br> Backend는 파일 메타데이터만 관리하도록 책임 분리 <br> 파일 접근 경로와 도메인 데이터 간 참조 구조 설계 <br> CDN 캐싱 전략 적용으로 서버 트래픽 분산 및 병목 제거                                                |
| 3  | **세션 기반 <br> Authentication 아키텍처 설계** | Spring Security 기반 서버 세션 인증 구조 구현 <br> 로그인/로그아웃 및 인증 사용자 정보 조회 API 구성 <br> HttpSession 기반 SecurityContext 관리 <br> 인증 사용자만 핵심 분석 API 접근 가능하도록 접근 제어 설계 <br> 내부 운영 시스템 특성에 맞춘 상태 기반 인증 전략 채택                                                               |
| 4  | **규정 문서 CRUD 및 <br> RAG 데이터 연동 구조 설계** | 유지보수 기준 문서 등록·수정·삭제·조회 API 설계 <br> 문서 데이터 구조화 및 검색 가능한 형태로 DB 저장 <br> 결함 데이터와 규정 문서 간 참조 관계 모델링 <br> RAG 파이프라인에서 활용 가능한 데이터 제공 계층 구현 <br> 운영 단계에서 규정 변경 시 즉시 반영 가능한 구조 설계                                                                                |
| 5  | **엔지니어 피드백 API 및 <br> 데이터 축적 구조 설계**  | 탐지 결과에 대한 수정·보정 정보 입력 API 구현 <br> Bounding Box 및 Severity 수정 이력 관리 <br> 원본 탐지 결과와 피드백 데이터 분리 저장 구조 설계 <br> 운영 데이터 축적을 고려한 확장 가능한 데이터 모델 구성 <br> 향후 모델 고도화를 위한 학습 데이터 레이어 기반 마련                                                                           |

---
## 시스템 구성
| 시스템 구성도 | ERD 다이어그램 |
| ------| ------|
|<img width="800" height="1000" alt="시스템 구성도" src="https://github.com/user-attachments/assets/ae96fd19-a1f9-4293-b96e-aa2045240ce9" /> | <img width="1000" height="400" alt="ERD 다이어그램" src="https://github.com/user-attachments/assets/718bf38c-6466-46f1-ad49-1eabc7dc508b" />|

### [API 명세 - Swagger](http://3.35.8.146:8080/swagger-ui/index.html#/)
<table>
  <tr>
    <td width="50%" valign="top">
      <img src="https://github.com/user-attachments/assets/876a4f44-feea-41ad-aaa1-b4a2f0fbf9f0" width="100%" />
      <img src="https://github.com/user-attachments/assets/d95306d8-3fcb-4840-9b53-f64220b2ea50" width="100%" />
    </td>
    <td width="50%" valign="top">
      <img src="https://github.com/user-attachments/assets/438f9126-c0f0-4a44-8544-87f4a21ae901" width="100%" />
      <img src="https://github.com/user-attachments/assets/6a46e396-f3f3-455f-9ead-36d91902a5cd" width="100%" />
    </td>
  </tr>
</table>

### 기술 스택

- 개발: Spring Boot, Kotlin, MySQL, SpringSecurity
- 배포: Docker, AWS(S3, Pipeline, CodeBuild, CodeDeploy, EC2, RCS)

### 전체 시스템 배포 환경
1. AWS CodePipeline으로 배포 플로우 자동화
  - GitHub push를 트리거로 Pipeline 실행
  - Source -> Build(CodeBuild) -> Deploy(CodeDeploy) 단계로 구성
  - 배포 환경(EC2)을 단일 진입점으로 구성하고, 운영 반영 과정을 표준화
2. CodeBuild로 Docker 이미지 빌드 및 아티팩트 생성
   - 서버 배포는 환경 일관성을 위하여 Docker 기반으로 통일
   - CodeBuild에서 다음 단계 수행
     1. Gradle 빌드
     2. Docker이미지 생성
     3. 빌드된 이미지를 ECR에 Push
     4. CodeDeploy가 사용할 배포 스크립트(Appspec + scripts) 아티팩트 생성
3. CodeDeploy로 EC2 인스턴스에 배포
   - EC2의 CodeDeploy Agent를 이용하여 배포를 자동 수행
   - Deploy 단계에서 수행:
     - 최신 이미지 Pull
     - 기존 컨테이너 중지 및 최신버전 교체
     - docker compose 기반으로 서비스 재기동

### 로컬 환경설정 및 실행방법
1. docker, java 21 설치
2. application-dev.yml 파일 작성 (aws s3 시크릿키 발급받아서 사용)

> [!Note]
> 필요시 Contributor에게 연락해주세요

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
1. [S3 + CloudFront를 활용한 File 시스템 설계 및 구축](#S3-+-CloudFront를-활용한-File-시스템-설계-및-구축)
2. [철도 주행 영상 기반 결함 분석 통합 파이프라인 설계 및 구축](#철도-주행-영상-기반-결함-분석-통합-파이프라인-설계-및-구축)
3. [Session 로그인 FrontEnd CI/CD 배포 환경](#Session-로그인-FrontEnd-CI/CD-배포-환경)

---
### S3 + CloudFront를 활용한 File 시스템 설계 및 구축

#### 1️. File 시스템 설계 배경 상황

AI 탐지 결과(이미지·영상·JSON)는
대용량 정적 리소스이며, 다음과 같은 문제가 존재합니다.

- 백엔드 서버에서 직접 저장·서빙 시 **서버 부하 및 네트워크 병목 발생**
- AI / LLM 서버에서 대용량 파일 접근 시 처리 지연
- 탐지 파일 증가에 따른 확장성 한계

따라서 **파일 전송 책임을 서버와 분리하는 구조**가 필요함.

#### 2️. File 시스템 구현
**1) S3를 원본 저장소로 사용**
- 파일 메타데이터만 DB에서 관리
- 실제 파일은 Object Storage에 저장

**2) CloudFront CDN 기반 파일 제공**
- 대량 요청 시 캐시 활용으로 S3 접근 최소화
- Client / AI / LLM 서버가 직접 접근 가능하도록 구조 분리

**3) Backend는 파일 메타데이터만 관리**
- 서버 트래픽 80% 감소
- 파일 전송 책임 분리로 애플리케이션 안정성 확보

**4) Presigned URL을 왜 사용하지 않았는가?**
- Presigned URL 방식은 보안 측면에서는 유효한 접근 제어 수단이지만 요청시 동적 URL이 생성되어 CDN 캐싱 효과를 활용할 수 없음
- AI 서버 / LLM 서버 / Client가 반복적으로 접근할 경우 Backend가 URL 생성 지점이 되어 병목 발생
- 반면 CloudFront 기반 고정 URL 구조는 CDN 캐시 활용 가능 하며, 반복 접근 시 S3 호출 최소화 Backend를 파일 서빙에서 분리함.
- 따라서 접근 제어 편의성보다 **확장성 · 성능 · 트래픽 분산 구조를 우선하여 CDN 기반 구조를 채택**하였다.

#### 3. File 시스템 적용 결과 및 인사이트

- S3 + CloudFornt 파일시스템을 적용하여 Client 및 AI **접근 지연 40% 감소**, Backend **트래픽 80% 감소**, LLM/Vision 데이터 **전송 시간 40% 단축** 의 효과를 얻었습니다. 또한 Backend는 파일 서빙에서 벗어나 도메인 로직과 메타데이터 관리에 집중할 수 있게 되었고, 트래픽 증가 상황에서도 **구조 변경 없이 대응 가능한 확장성을 확보**했습니다.
- 구현 과정에서 이미지·영상·JSON과 같은 대용량 정적 리소스는 RDB의 트랜잭션 데이터와 전혀 다른 성격을 가지며, 처음부터 분산 저장과 캐싱을 전제로 설계되어야 한다는 사실을 알았습니다.
- 또한 Presigned URL이 보안성이 좋아 도입을 고려했지만, 실제 도입시, CDN 캐싱을 저해하고 Backend를 병목 지점으로 만들 수 있다는 점을 확인했습니다.
- 이를 통해 더 많은 기술을 적용하는 것이 아니라, **시스템의 목적과 트래픽 구조에 맞는 기술을 선택을 하는 것이 효율적인 설계**라고 느꼈습니다.

---
### 철도 주행 영상 기반 결함 분석 통합 파이프라인 설계 및 구축

#### 1) 설계 배경
- 철도 주행 영상에서 결함을 분석하고 이를 저장하기 위해서는 영상 업로드 -> Vision Model Inference -> LLM 분석 -> Backend 정규화 -> DB 저장 흐름으로 이루어지는 처리가 필요함.
- Vision Model과 LLM 분석 처리의 경우, 각각 API 호출을 통한 느슨한 결합으로 설계되어 있고, 각 단계는 처리 시간이 긴 작업임.
- 이에 따라 동기 처리시 서버의 스레드 블로킹이 발생하고, 상태 관리 없이 단순 호출 시 진행 상태의 추적이 어려워지는 문제가 존재하기에, **상태 기반 비동기 처리 파이프라인 설계**가 필요하였음

#### 2) 통합 파이프라인 아키텍처 설계

**1. 상태 기반 처리 모델 도입**
- ProblemDetection 엔티티에 CREATED/PENDING/RUNNING/COMPLETED/FAILED 상태를 도입
- 각 처리 단계에서 상태 전환 및 진행 상황을 기록
- 프론트엔드에서 API를 호출하여 진행 상황 추적 가능
- Worker 기반 비동기 처리 구조와 자연스럽게 연결

**2. Worker 기반 비동기 처리 구조 설계**
- Spring Scheduler를 활용하여 주기적으로 ProblemDetection 상태를 모니터링
- Vision Model Inference의 비동기 처리를 담당하는 DetectWorker와 LLM 분석을 담당하는 LLMWorker로 역할 분리
- ProblemDetection의 상태에 따라 적절한 Worker가 처리하도록 설계
- 긴 작업 시간에도 API 호출이 블로킹되지 않고, 상태 기반으로 진행 상황을 관리 가능

#### 3) 적용 결과 및 인사이트
- 장시간 추론 작업에도 API의 응답 지연이 없이, 상태 기반으로 진행 상황을 관리할 수 있게 되어 사용자 경험이 크게 향상됨
- 상태 기반 설계를 통해 운영중 장애 복구 가능
- 멀티 인스턴스로 확장 시에도 상태 관리로 작업 분배 가능
- AI 서버와 Backend 간의 느슨한 결합을 유지하면서도, 통합된 처리 흐름을 구축할 수 있었음.

특히 처리 시간이 긴 작업에 대한 책임을 분리하고, 비동기식으로 처리하는 구조가 시스템의 안정성과 확장성에 매우 효과적이라는 점을 깨달았음. 

---

### Session 로그인 FrontEnd CI/CD 배포 환경

#### 1. JWT가 아닌 Session 인증 방식을 선택한 이유
| Session 기반 인증의 장점
- 여러 내부 API와 AI 서버 호출을 중앙 Backend에서 통제하는 구조에 적합
- 사용자 수가 폭발적으로 증가하는 공개 서비스가 아닌, 관리형 시스템에 최적화된 인증 방식
- 인증 상태를 서버가 직접 관리함으로써 접근 통제 및 세션 무효화 제어가 용이
- 모노리스 또는 중앙 집중형 아키텍처에서 실무적으로 많이 사용되는 방식

초기에는 확장성과 분산 환경에 적합한 JWT 기반 인증을 고려하였습니다.
그러나 RailDock은 다수의 AI 서버(Vision, LLM 등)를 하나의 Backend에서 통합 호출·관리하는 구조이며, 외부에 개방된 대규모 사용자 서비스가 아닌 내부 운영 중심의 관리형 시스템이라는 특성을 가지고 있습니다.

이러한 구조에서는 인증 상태를 각 요청에 위임하는 Stateless 방식보다,
**서버가 사용자 인증 상태를 직접 관리하는 Session 기반 인증 방식이 더 적합하다고 판단**하였습니다.

#### 2. FrontEnd 배포 환경 구성 (S3 + CloudFront)

FrontEnd는 React 기반 SPA로 구성하였으며,
정적 리소스 특성을 고려하여 서버와 분리된 CDN 기반 배포 구조를 채택하였습니다.

React 애플리케이션은 빌드 이후 HTML, JS, CSS로 구성된 정적 파일 집합이므로,
애플리케이션 서버(EC2)에서 직접 서빙하는 것보다
S3 + CDN 구조가 비용·성능 측면에서 더 효율적이라고 판단하였습니다.

이에 따라 다음과 같은 구조를 설계하였습니다.

- S3를 정적 리소스 저장소로 사용
- CloudFront를 통해 캐싱 기반 배포
- CodePipeline, CodeBuild를 활용한 CI/CD 자동화 구성

**설계 근거**

- 정적 리소스는 서버 자원이 아닌 CDN이 처리하는 것이 구조적으로 적합
- EC2 인스턴스와 정적 자산을 분리하여 책임 경계 명확화
- 배포 자동화를 통해 운영 중 수동 개입 최소화


#### 3. Session 인증과 CloudFront 환경에서 발생한 문제

Session 기반 인증의 핵심 제약은 다음과 같습니다.

> 세션 쿠키는 동일 Origin 정책(Same-Origin Policy)을 따른다.

로컬 환경에서는 React dev server의 **proxy 설정**을 통해
Backend와 동일 Origin처럼 동작하도록 구성하여 문제없이 동작하였습니다.

그러나 S3 + CloudFront로 배포한 이후 FrontEnd 도메인과 Backend EC2 도메인이 서로 달라지면서 
<br> **세션 인증은 동작하지만, 배포 환경에서는 쿠키가 전달되지 않는 구조적 충돌**이 발생 하였습니다.

#### 4️. 해결 방법 – CloudFront 기반 통합 도메인 구성

이 해당하는 문제를 해결하기 위해 다음과 같이 구조를 변경하였습니다.
- Backend도 동일한 CloudFront를 통해 접근하도록 구성
- CloudFront의 Behavior(라우팅) 기능을 활용하여 분기처리

  - **`/` → S3 (React 정적 파일)**
  - **`/api/*` → EC2 Backend**


**적용 결과**
- Session 기반 인증을 유지하면서도 CDN 기반 배포 구조와 충돌 없이 통합
- FrontEnd와 Backend가 **동일 도메인 하에서 동작**
- 세션 쿠키가 정상 전달
- 별도의 CORS 우회 없이 안정적인 인증 구조 확보

#### 5️. 인사이트
- 브라우저의 Same-Origin Policy, 쿠키 전달 조건(Secure, SameSite), CDN 라우팅 구조가 실제 서비스 환경에서 어떻게 상호작용하는지 경험하였습니다.
- 개발 환경에서는 정상 동작하던 기능이 운영 환경에서 실패하는 원인을 추적하면서, 코드 레벨이 아닌 인프라·도메인 레벨에서 문제를 바라보는 시야를 갖게 되었습니다.
- 인증 방식 선택은 단순한 기술 선택이 아니라 브라우저 정책, 도메인 구조, CDN 설정까지 포함한 전체 시스템 아키텍처 결정이라는 것을 깨닫게 되었습니다.
