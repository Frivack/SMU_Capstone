# 📱 공대친구

대학교 캡스톤디자인 과제로 진행된 안드로이드 앱 프로젝트입니다.
컴퓨터 견적을 간편하게 작성해줍니다.
Kotlin을 사용하여 페이지 단위로 모듈화된 UI를 구성하고,
외부 Flask 서버와의 REST API 통신을 통해 데이터 처리 기능을 구현했습니다.

---

## 📌 주요 기능
- 사용자 로그인 및 회원가입
- 서버로 데이터 요청 및 응답 처리
- 서버와의 실시간 연동 기능

---

## 🛠️ 개발 환경
- Kotlin
- Android Studio
- Jetpack Compose
- SQLite (로컬 데이터 저장)
- Retrofit (서버 통신)
- [Flask (서버)](https://github.com/Frivack/SMU_Capstone_DB_Flask)
  -  별도로 구축된 Flask 서버와 통신 예정입니다.

---

## 🚀 실행 방법
1. Android Studio로 프로젝트 클론
2. 필요 라이브러리 Sync
3. Android 에뮬레이터 또는 실제 기기에서 실행

---

## 🧩 프로젝트 구조
```
app/
├── MainActivity.kt       # 메인 액티비티
├── LoginPage.kt          # 로그인 화면
├── MainPage.kt           # 메인 메뉴 화면
├── AccountPage.kt        # 계정 정보 화면
├── ReviewPage.kt         # 사용자 리뷰 목록
├── ReviewViewPage.kt     # 리뷰 상세 보기
├── WritePage.kt          # 리뷰 작성 화면
├── BudgetPage.kt         # 예산 설정 화면
├── SupportPage.kt        # 고객 지원 화면
├── SettingPage.kt        # 앱 설정 화면
├── DatabaseHelper.kt     # SQLite 데이터베이스 헬퍼
├── Optimizer.kt          # 부품 최적화 알고리즘
├── CommentAdapter.kt     # 댓글 리스트 어댑터
└── ReviewAdapter.kt      # 리뷰 리스트 어댑터
```
