<img src="https://capsule-render.vercel.app/api?type=soft&color=FFFFFF&height=80&section=header&text=🧥Close-T🧣&fontSize=50&fontColor=000000"/>


# close-t_frondend

📌 BackEnd와 관련된 내용은 https://github.com/MadCamp-2ndWeek-BrewStar/BrewStar_backEnd 로‼️

### 👥 Developers
- 현채정: KAIST 전산학부 21학번
- 송한이: KAIST 전산학부 21학번

### 💻 Tech Stacks
<img src="https://img.shields.io/badge/AndroidStudio-3DDC84?style=flat-square&logo=AndroidStudio&logoColor=white"/> <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=Kotlin&logoColor=white"/>
- minSdkVersion: 26
- targetSdkVersion: 34

## 📢 Description

***Summary***
몰입캠프 3주차 안드로이드 앱 제작 프로젝트입니다.
본 프로젝트는 세 개의 탭으로 구성된 안드로이드 앱 개발을 다루고 있습니다.

---

### 📱 MainActivity
![MainActivity](https://github.com/MadCamp-2ndWeek-BrewStar/BrewStar_FrontEnd/assets/112535704/1e6d4c5c-1ac2-428f-b74d-08bb938b8f16)
***Main Features***
- gif를 이용해 은은한 스플레시 화면을 만들었습니다.
- "Start By Using KakaoTalk"을 눌러 카카오톡으로 로그인합니다.
- 처음 이 앱에 접속하는 USER은 회원가입 절차를 진행하게 됩니다.
- 선택 입력 사항은 기입하지 않아도 되나, TAP3의 "GET OOTD" 기능을 사용하기 위해서는 필요합니다.
- 화면 상단의 네비게이션 바를 이용하여 탭을 전환할 수 있습니다.

***Technical Description***
- Kakaotalk Developers에서 제공하는 Kakao 로그인 API를 사용하였습니다.
- 카카오톡이 설치되어 있으면 카카오톡 앱으로 로그인, 아니면 카카오계정으로 로그인하도록 구현했습니다.
- 카카오 api에서 제공하는 userID를 이용하여 회원을 식별하고, 회원가입 시 기입한 정보들과 함께 DB에 저장됩니다.

---

### ✌️ TAP1: My Closet
![Tab1](https://github.com/MadCamp-2ndWeek-BrewStar/BrewStar_FrontEnd/assets/112535704/63a67328-1d2c-443b-a955-e1dd5fd73553)
***Main Features***
- 나의 모바일 옷장 상황을 모두 볼 수 있습니다.
- Category는 상의, 하의, 아우터, 원피스, 신발, 가방으로 이루어져 있습니다.
- 옷의 종류는 즐겨 찾는 옷, 사고 싶은 옷, 안 입는 옷으로 나뉩니다.
- 각 항목을 길게 누르면 "안 입는 옷 보관함"으로 이동하게 되며, TAB1에서는 보이지 않게 됩니다.
- 각 항목 상단의 하트 버튼을 누르면, "즐겨 찾는 옷"으로 인식됩니다.
- 각 Category마다 있는 하트 버튼을 누르면, 각 Category 별 즐겨 찾는 옷들만 보이게 됩니다. 다시 누르면 안 입는 옷을 제외한 해당 Category의 전체 옷을 볼 수 있습니다.

- 왼쪽 하단의 + 버튼을 누르면 새로운 옷을 추가하는 Activity로 이동합니다.
- 갤러리에 있는 사진을 포함, 카메라로 자신이 가지고 있는 옷을 찍어 실시간으로 불러올 수도 있습니다.
- 자동으로 옷을 제외한 배경을 지워 다시 앱으로 사진을 불러옵니다.
- 옷의 속성을 모두 선택한 뒤, SAVE 버튼을 눌러 저장합니다.
- 태그는 2개 이상 입력하여야 하며, 사고 싶은 옷의 경우 옷을 구매할 수 있는 Link를 입력할 수 있습니다.

- 화면 하단의 BottomSheetBehavior을 스와이프하면, 직접 LOOKBOOK을 꾸며볼 수 있습니다.
- 각 옷을 짧게 클릭하면, LOOKBOOK에 해당 옷이 들어가게 됩니다. 프리뷰로 볼 수 있습니다.
- 아래의 Comment를 입력하여, 각 LOOKBOOK을 언제 입을지 적어둘 수 있습니다.
- SAVE 버튼을 누르면 LOOKBOOK이 저장되며, TAP2에서 볼 수 있습니다.

***Technical Description***
- API와 안드로이드 스튜디오를 연결하여, 사용자의 userID를 이용해서 모든 옷들을 불러올 수 있도록 했습니다.
- 새로운 옷을 만들 때 ClipDrop API를 이용하여, 원하는 사진의 배경을 제거하도록 했습니다.
- 사진을 DB에 저장한 뒤 서버에 띄울 수 있도록 하여, TAB1에서는 사진의 URL을 받아 Picasso 및 Glide를 이용하여 옷의 이미지를 나타냈습니다.
- Clothes라는 data class를 만들어 사용하였습니다.
- BottomSheetBehavior을 이용하여, 심미성을 높이고 공간을 효율적으로 사용하였습니다.
- SwipeRefreshLayout을 통해 스와이프하면 새로고침을 할 수 있습니다.
- 각 항목은 모두 recyclerView로 구현하였습니다.

---

### 🤩 TAP2: All Customs
![Tab2](https://github.com/MadCamp-2ndWeek-BrewStar/BrewStar_FrontEnd/assets/112535704/fb4de4d5-9e67-4372-bf7d-10e98e932c0a)
***Main Features***
- Category별로 나누어서, Coffee, Non-Coffee, Frappuccino 별로 나누어 custom한 item들을 볼 수 있습니다.
  각 Category를 누르면, 새로운 Activity가 뜨면서 item들이 나열됩니다.
- 모든 custom들을 나타낸 Activity에서는, 화면 오른쪽에 All/Coffee/Non-Coffee/Frappuccino를 선택하여 필터링할 수 있도록 했습니다.
  또한, item 검색도 가능합니다.
- 각 item 항목에는 Category에 따른 사진, custom의 이름, 실제 메뉴, 커스텀한 내용, 좋아요 수를 나타냈습니다.
- 하나의 item을 누르면 팝업이 뜨면서 상세정보가 나타납니다. 추가적으로 설명, 만든 사람의 이름도 볼 수 있습니다.
- 팝업의 오른쪽 상단에는 별 모양으로 되어있는 "좋아요" 버튼이 있습니다. 별을 눌러 좋아요 선택 및 해제를 할 수 있습니다.
- 또한, "좋아요" 순으로 현재 순위 Top 10을 보여줍니다. 각 item을 누르면 마찬가지로 상세정보를 볼 수 있습니다.
- Tap1과 마찬가지로, 모든 업데이트 상황은 전체 화면을 스와이프하여 새로고침을 하면 반영됩니다.

***Technical Description***
- API와 안드로이드 스튜디오를 연결하여, 전체 custom item을 볼 수 있도록 했습니다.
- "좋아요"를 누르게 되면 자동적으로 자신의 Favorite Customs DB에 저장됩니다. 마찬가지로 "좋아요"를 해제하면 DB에서 제거됩니다.
- SwipeRefreshLayout을 통해 스와이프하면 새로고침을 할 수 있습니다.
- 각 항목은 모두 recyclerView로 구현하였습니다.



## 📁 Resources
- APK file : https://drive.google.com/file/d/1J1PQ0fvIpA3LQc9COHur7n9mD5tEZmQB/view?usp=sharing



## 수정/보완해야 할 점
- ViewCustoms.kt 이해하고 Recommend Recent 별 sorting 추가하기. 
- loading시간동안 loading표시하는 기능 넣기. 
- 커스텀 메뉴 추가할 때, ?누르면 커스터마이징 가능한 것들 띄울 수 있게 하기. 
- 메뉴당 커스터마이징 가능한 것들 정리해서 custom_guide에 넣어놓기. 
- 메뉴 위아래 위치 옮길 때, 새로고침하고 겹쳐서 안되는 문제 해결하기.
- 스와이프해서 메뉴 삭제할 때, tab전환 스와이프랑 겹처서 안되는 문제 해결하기. 
- custom검색기능 전체 검색 기능 -> category별로 전환하기. 
- 커피, 논커피, 프라푸치노 골라서 add할 수 있게 하기. (현재는 custom_guide에 있는 메뉴 이름으로 등록을 해야 해당 음료 사진이 나옴) 
