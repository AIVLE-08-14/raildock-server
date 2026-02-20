# RAILDOCK - SERVER
## 프로젝트 개요

- KT AIVLE School 8기 AI 트랙 7반 14조 빅프로젝트
- AI 기반 철도 시설물 유지보수 의사결정 지원 플랫폼 - 백앤드 서버
- 기간: 2026.01 - 2025.02(8주)

### 기여자
| ‍이름 | 담당 업무                                                                                                                                                           |
|----------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **서범수** | • [S3 + CloudFront를 활용한 File 시스템 설계 및 구축](#S3-+-CloudFront를-활용한-File-시스템-설계-및-구축) <br> • Session 로그인 FrontEnd CI/CD 배포 환경 <br> • Problem / Feedback / Document / User domain 설계 및 구현 <br> • LLM Chat bot API 연동 |
| **윤후성** | • 여기내용 추가좀 <br> 가능하면 밑에 상세내용이랑 동일하게  <br>  • Problem Detection Domain 설계 및 구현 |
| **성기현** | • VisionModel Finetunning API 연결 |

### 주요 기능
| 번호 | 기능                                       | 설명                                                                                                                                                                                                                                                       |
| -- | ---------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1  | **철도 영상 기반 <br> 분석 통합 파이프라인 설계**         | 결함 탐지 영상 업로드 후 Vision Model 탐지 호출 <br> Vision Model 탐지 후 LLM 모델 호출 <br> Vision Model, LLM 모델 결과(JSON, Image)를 수신하여 내부 도메인 모델로 변환 <br> 결함 상태·심각도·위치 정보 정규화 및 DB 저장 <br> 가공된 분석 데이터를 LLM 질의용 포맷으로 재구성 <br> AI → Backend → LLM → Backend으로 이어지는 통합 처리 흐름 설계 |
| 2  | **S3 + CloudFront 기반 <br> 파일 관리 시스템 설계** | 대용량 영상·이미지·JSON 파일을 AWS S3 Object Storage에 저장 <br> CloudFront CDN 기반 고정 URL 구조로 분산 서빙 아키텍처 구성 <br> Backend는 파일 메타데이터만 관리하도록 책임 분리 <br> 파일 접근 경로와 도메인 데이터 간 참조 구조 설계 <br> CDN 캐싱 전략 적용으로 서버 트래픽 분산 및 병목 제거                                                |
| 3  | **세션 기반 <br> Authentication 아키텍처 설계**    | Spring Security 기반 서버 세션 인증 구조 구현 <br> 로그인/로그아웃 및 인증 사용자 정보 조회 API 구성 <br> HttpSession 기반 SecurityContext 관리 <br> 인증 사용자만 핵심 분석 API 접근 가능하도록 접근 제어 설계 <br> 내부 운영 시스템 특성에 맞춘 상태 기반 인증 전략 채택                                                               |
| 4  | **규정 문서 CRUD 및 <br> RAG 데이터 연동 구조 설계**   | 유지보수 기준 문서 등록·수정·삭제·조회 API 설계 <br> 문서 데이터 구조화 및 검색 가능한 형태로 DB 저장 <br> 결함 데이터와 규정 문서 간 참조 관계 모델링 <br> RAG 파이프라인에서 활용 가능한 데이터 제공 계층 구현 <br> 운영 단계에서 규정 변경 시 즉시 반영 가능한 구조 설계                                                                                |
| 5  | **엔지니어 피드백 API 및 <br> 데이터 축적 구조 설계**     | 탐지 결과에 대한 수정·보정 정보 입력 API 구현 <br> Bounding Box 및 Severity 수정 이력 관리 <br> 원본 탐지 결과와 피드백 데이터 분리 저장 구조 설계 <br> 운영 데이터 축적을 고려한 확장 가능한 데이터 모델 구성 <br> 향후 모델 고도화를 위한 학습 데이터 레이어 기반 마련                                                                           |


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

### 배포 환경
- AWS CodePipeline, CodeBuild, CodeDeploy를 활용한 CI/CD 구축
- RCS를 활용한 이미지 빌드 및 EC2 배포 자동화

### 로컬 환경설정 및 실행방법
1. docker, java 21 설치
2. application-dev.yml 파일 작성 (aws s3 시크릿키 발급받아서 사용)
3. 혹은 필요시 Contributor에게 연락

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

## 상세 기술구현
해당 프로젝트를 진행하면서 기술적으로 중요하거나 어려웠던 부분을 아래와 같이 정리하였습니다.
1. File 시스템 설계 및 구축 (S3 + CloudFornt)
2. 

### S3 + CloudFront를 활용한 File 시스템 설계 및 구축

#### 1️) File 시스템 설계 배경 상황

AI 탐지 결과(이미지·영상·JSON)는
대용량 정적 리소스이며, 다음과 같은 문제가 존재합니다.

- 백엔드 서버에서 직접 저장·서빙 시 **서버 부하 및 네트워크 병목 발생**
- AI / LLM 서버에서 대용량 파일 접근 시 처리 지연
- 탐지 파일 증가에 따른 확장성 한계

따라서 **파일 전송 책임을 서버와 분리하는 구조**가 필요함.

#### 2️) File 시스템 구현
**1. S3를 원본 저장소로 사용**
- 파일 메타데이터만 DB에서 관리
- 실제 파일은 Object Storage에 저장

**2. CloudFront CDN 기반 파일 제공**
- 대량 요청 시 캐시 활용으로 S3 접근 최소화
- Client / AI / LLM 서버가 직접 접근 가능하도록 구조 분리

**3. Backend는 파일 메타데이터만 관리**
- 서버 트래픽 80% 감소
- 파일 전송 책임 분리로 애플리케이션 안정성 확보

**4. Presigned URL을 왜 사용하지 않았는가?**
- Presigned URL 방식은 보안 측면에서는 유효한 접근 제어 수단이지만 요청시 동적 URL이 생성되어 CDN 캐싱 효과를 활용할 수 없음
- AI 서버 / LLM 서버 / Client가 반복적으로 접근할 경우 Backend가 URL 생성 지점이 되어 병목 발생
- 반면 CloudFront 기반 고정 URL 구조는 CDN 캐시 활용 가능 하며, 반복 접근 시 S3 호출 최소화 Backend를 파일 서빙에서 분리함.
- 따라서 접근 제어 편의성보다 **확장성 · 성능 · 트래픽 분산 구조를 우선하여 CDN 기반 구조를 채택**하였다.

#### 3) File 시스템 적용 결과 및 인사이트

- S3 + CloudFornt 파일시스템을 적용하여 Client 및 AI **접근 지연 40% 감소**, Backend **트래픽 80% 감소**, LLM/Vision 데이터 **전송 시간 40% 단축** 의 효과를 얻었습니다. 또한 Backend는 파일 서빙에서 벗어나 도메인 로직과 메타데이터 관리에 집중할 수 있게 되었고, 트래픽 증가 상황에서도 **구조 변경 없이 대응 가능한 확장성을 확보**했습니다.
- 구현 과정에서 이미지·영상·JSON과 같은 대용량 정적 리소스는 RDB의 트랜잭션 데이터와 전혀 다른 성격을 가지며, 처음부터 분산 저장과 캐싱을 전제로 설계되어야 한다는 사실을 알았습니다.
- 또한 Presigned URL이 보안성이 좋아 도입을 고려했지만, 실제 도입시, CDN 캐싱을 저해하고 Backend를 병목 지점으로 만들 수 있다는 점을 확인했습니다.
- 이를 통해 더 많은 기술을 적용하는 것이 아니라, **시스템의 목적과 트래픽 구조에 맞는 기술을 선택을 하는 것이 효율적인 설계**라고 느꼈습니다.

---
