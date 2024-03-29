# Team_Mapp

[![Build Status](https://app.bitrise.io/app/eee8c8dc2c14666f/status.svg?token=be-fGTmq5oL5HB4d5a7bHw&branch=master)](https://app.bitrise.io/app/eee8c8dc2c14666f)

### 팀구성

- [박가은](https://github.com/pTina) (팀장, 개발자)
- [이지수](https://github.com/ljsgold2001) (개발자)
- [구남현](https://github.com/namhyun-gu) (개발자)
- [김주현](https://github.com/201511094) (개발자)

---

### 프로젝트 주제

- 맛집 관련 App - TV프로그램에서 방송한 맛집과 셀럽들이 추천한 맛집을 소개하는 앱을 구현한다.

---

### 팀의 목표

- 임베디드 소프트웨어2 수업을 통해 학습한 Kotlin 문법을 사용하여 Android App구현한다.
- Kotlin을 이용하여 Android App을 개발하고 비교적 완성도 있는 App을 구현하는 것

---

### 시장 조사

- 기존 App 분석

  - TV 프로그램에서 방송한 맛집을 소개하는 앱은 이미 존재한다.
  - 프로그램 이외에도 특정 키워드 검색을 통해 사용자가 원하는 맛집을 추천하는 기능을 추가한다.
  - 직접 리뷰를 작성하여 게시할 수 있고 이를 다른 사용자들이 열람 가능하다.

- 차별성

  - 최근 여러 연예인이나 유튜버들이 자신들이 직접 방문해본 맛집을 추천하는 경우가 많다. 시청자들은 이러한 음식점이 어디인지 호기심이 생기고 가보고 싶다는 마음이 들기도 하지만, 이러한 맛집들을 중심적으로 소개하는 앱은 현재 존재하지 않는다. 그래서 이번 프로젝트에서는 "TV프로그램" 이외에 "셀럽" 카테고리를 추가하여 셀럽이 추천한 맛집 정보를 소개하는 앱을 구현하려고 한다. 이 앱을 사용함으로써 셀럽들이 추천한 음식점을 모아서 볼 수 있고, 특정 인물이 추천하는 맛집과 관련 정보를 쉽게 얻을 수 있다는 장점이 있다.

---

### 팀일정

- 1주차(10.08~10.14) - 프로젝트 팀 결성 후 주제 선정
- 2주차(10.15~10.21) - UI 구성을 정하고 앱에 들어갈 기능들을 의논, 라이브러리 사용법 익히기
- 3주차(10.22~10.28) - 중간발표 준비
- 4주차(10.29~11.04) - App 설계 마무리, 개발 시작
- 5주차(11.05~11.11) - App 개발
- 6주차(11.12~11.18) - App 개발
- 7주차(11.19~11.25) - App 개발 마무리, 최종발표 준비

---

### Workflow

<img width="1000" alt="스크린샷 2019-10-25 오후 7 54 17" src="https://user-images.githubusercontent.com/48313074/67565811-41d6df00-f761-11e9-9ede-bbe1fc6aa76f.png">

---

### 개발환경

- 형상관리 - Git

- 데이터베이스 - Firebase Cloud Firestore (정보 저장), Firebase Cloud Storage (앱 내 이미지 저장)

  <img width="300" alt="스크린샷 2019-10-25 오후 8 03 39" src="https://user-images.githubusercontent.com/48313074/67566380-9595f800-f762-11e9-9e7e-712049124b66.png">

- 프로젝트 아키텍처 모델: MVVM

  <img width="374" alt="스크린샷 2019-10-25 오후 8 00 42" src="https://user-images.githubusercontent.com/48313074/67566490-dc83ed80-f762-11e9-820c-d295b61ef2ca.png">

  - 라이브러리
    - Android jetpack 라이브러리들
    - Rx(RxKotlin, RxAndroid)
    - Naver map sdk

---

### 업데이트 예정사항

- "리뷰 기능"을 추가하여 자신이 방문한 곳에 대한 의견을 작성한 후 게시할 수 있도록 한다. 또한 다른 사람들이 쓴 리뷰도 볼 수 있도록 하여 사용자들끼리 정보 공유가 가능하도록 한다.
- "길 찾기 기능"을 추가하여 자신의 현재 위치에서 특정 맛집까지 가는 최적의 길을 알려준다.
- 셀럽이 추천한 맛집과 더불어 셀럽이 직접 운영하는 맛집 카테고리를 추가하여 사용자에게 더 많은 정보를 제공할 수 있도록 한다.
