# 공부친구 - 스마트 독서대
상명대학교 융합전자공학과 "스터디메이트" 팀 졸업프로젝트 레포지토리
## 개요
* 독서대에 IoT와 영상처리 기술을 적용하여 학습자가 공부에 더욱 집중할 수 있게 도와주는 프로젝트

* 센서와 카메라를 통해 학습자의 공부환경을 인식하고, 영상처리 기술을 이용해 사용자가 공부에 집중하고 있는지를 지속적으로 확인하여 학습자가 계속해서 공부에 집중할 수 있도록 피드백

* 모바일 앱이나 LCD 화면을 통해 집중한 시간을 확인할 수 있으며, 앱을 통해 현재 공부중인 다른 사용자의 공부 시간도 같이 확인 가능

* 기존의 학습 도우미 프로그램의 한계는, 바로 어플리케이션이라는 점 때문에 스마트폰을 보도록 유도해서 오히려 공부에 방해가 될 때가 있다는 것임. 스터디메이트 팀은 이를 실제 독서대와 결합한 IoT를 이용해서 해결함
## 조원
| 이름                                    |              역할 |
| --------------------------------------- | ---------------- |
| [안재민](https://github.com/veryneuron) | 서버, 어플리케이션 |
| [이소용](https://github.com/iot-lsy)    | 라즈베리 파이      |
| [최광민](https://github.com/KwangMinChoi1)    | 아두이노     |
| [박성준](https://github.com/park-sungjune)    | 라즈베리 파이 |
## 아키텍처
![UML-아키텍처](https://user-images.githubusercontent.com/29668913/209352908-eb45c195-4d3a-48c7-bbdf-e44e1e31abeb.jpg)
## 유스케이스 다이어그램
![UML-유스케이스](https://user-images.githubusercontent.com/29668913/187206625-accfe3a9-606c-49e0-8cb5-b75dcce30129.jpg)
## 각 기능별 설명
1. [온도, 습도 측정 및 경보](https://github.com/veryneuron/study_mate_project/blob/main/doc/%EC%98%A8%EB%8F%84%2C%20%EC%8A%B5%EB%8F%84%20%EC%B8%A1%EC%A0%95%20%EB%B0%8F%20%EA%B2%BD%EB%B3%B4.md)
2. [공부 시작, 종료 + 딴짓 경보](https://github.com/veryneuron/study_mate_project/blob/main/doc/%EA%B3%B5%EB%B6%80%20%EC%8B%9C%EC%9E%91%2C%20%EC%A2%85%EB%A3%8C%20%2B%20%EB%94%B4%EC%A7%93%20%EA%B2%BD%EB%B3%B4.md)
3. [회원가입 및 로그인](https://github.com/veryneuron/study_mate_project/blob/main/doc/%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85%20%EB%B0%8F%20%EB%A1%9C%EA%B7%B8%EC%9D%B8.md)
4. [온도, 습도, 공부시간 확인](https://github.com/veryneuron/study_mate_project/blob/main/doc/%EC%98%A8%EB%8F%84%2C%20%EC%8A%B5%EB%8F%84%2C%20%EA%B3%B5%EB%B6%80%EC%8B%9C%EA%B0%84%20%ED%99%95%EC%9D%B8.md)
5. [온도, 습도 경계값 및 라즈베리파이 설정](https://github.com/veryneuron/study_mate_project/blob/main/doc/%EC%98%A8%EB%8F%84%2C%20%EC%8A%B5%EB%8F%84%20%EA%B2%BD%EA%B3%84%EA%B0%92%20%EB%B0%8F%20%EB%9D%BC%EC%A6%88%EB%B2%A0%EB%A6%AC%ED%8C%8C%EC%9D%B4%20%EC%84%A4%EC%A0%95.md)
6. [타인 공부시간 확인](https://github.com/veryneuron/study_mate_project/blob/main/doc/%ED%83%80%EC%9D%B8%20%EA%B3%B5%EB%B6%80%EC%8B%9C%EA%B0%84%20%ED%99%95%EC%9D%B8.md)
7. [딴짓 감지](https://github.com/veryneuron/study_mate_project/blob/main/doc/%EB%94%B4%EC%A7%93%20%EA%B0%90%EC%A7%80.md)
8. [공부방 입장 및 퇴장](https://github.com/veryneuron/study_mate_project/blob/main/doc/%EA%B3%B5%EB%B6%80%EB%B0%A9%20%EC%9E%85%EC%9E%A5%20%EB%B0%8F%20%ED%87%B4%EC%9E%A5.md)
9. [기타 요소](https://github.com/veryneuron/study_mate_project/blob/main/doc/%EA%B8%B0%ED%83%80%20%EC%9A%94%EC%86%8C.md)

