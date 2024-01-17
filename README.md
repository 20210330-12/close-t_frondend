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

***Main***
- 나의 모바일 옷장 상황을 모두 볼 수 있습니다.
- Category는 상의, 하의, 아우터, 원피스, 신발, 가방으로 이루어져 있습니다.
- 옷의 종류는 즐겨 찾는 옷, 사고 싶은 옷, 안 입는 옷으로 나뉩니다.
- 각 항목을 길게 누르면 "안 입는 옷 보관함"으로 이동하게 되며, TAB1에서는 보이지 않게 됩니다.
- 각 항목 상단의 하트 버튼을 누르면, "즐겨 찾는 옷"으로 인식됩니다.
- 각 Category마다 있는 하트 버튼을 누르면, 각 Category 별 즐겨 찾는 옷들만 보이게 됩니다. 다시 누르면 안 입는 옷을 제외한 해당 Category의 전체 옷을 볼 수 있습니다.

***Add Clothes***
- 왼쪽 하단의 + 버튼을 누르면 새로운 옷을 추가하는 Activity로 이동합니다.
- 갤러리에 있는 사진을 포함, 카메라로 자신이 가지고 있는 옷을 찍어 실시간으로 불러올 수도 있습니다.
- 자동으로 옷을 제외한 배경을 지워 다시 앱으로 사진을 불러옵니다.
- 옷의 속성을 모두 선택한 뒤, SAVE 버튼을 눌러 저장합니다.
- 태그는 2개 이상 입력하여야 하며, 사고 싶은 옷의 경우 옷을 구매할 수 있는 Link를 입력할 수 있습니다.
  
***Preview LOOKBOOK***
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

### 🤩 TAP2: LookBook
![Tab2](https://github.com/MadCamp-2ndWeek-BrewStar/BrewStar_FrontEnd/assets/112535704/fb4de4d5-9e67-4372-bf7d-10e98e932c0a)
***Main Features***
- TAB1에서 저장했던 LOOKBOOK을 볼 수 있는 탭입니다.
- 화면 우상단의 큰 하트를 누르면 즐겨 찾는 LOOKBOOK만 모아볼 수 있습니다.
- 각 LOOKBOOK 항목의 하트를 누르면, 즐겨찾기 설정이 가능합니다.
- LOOKBOOK 항목 중 하나를 누르면, 폴라로이드 감성으로 LOOKBOOK이 나타납니다.
- 입력했던 comment와, 처음 옷을 등록할 때 입력했던 style들 중 3개를 볼 수 있습니다.
- LOOKBOOK 화면을 터치하면 사고 싶은 옷을 등록할 때 입력했던 Link가 나타나며, 그 Link를 클릭하면 브라우저와 연결되어 해당 옷을 살 수 있는 사이트로 접속할 수 있습니다.

***Technical Description***
- API와 안드로이드 스튜디오를 연결하여, LOOKBOOK을 모두 볼 수 있도록 했습니다.
- 즐겨찾기 버튼을 누르면 서버에 자동으로 저장됩니다.
- 각 항목은 모두 recyclerView로 구현하였습니다.
- Dialog를 사용하여 Popup창을 구현하였습니다.
- Intent를 이용해 인터넷에 연결할 수 있도록 했습니다.

---

### 🤩 TAP3: My Page
![Tab2](https://github.com/MadCamp-2ndWeek-BrewStar/BrewStar_FrontEnd/assets/112535704/fb4de4d5-9e67-4372-bf7d-10e98e932c0a)
***Main Features***
- My Page에서는, 자신의 카카오톡 프로필 사진 및 이름, 성별, 메일을 모두 볼 수 있습니다.
- 하단의 LOGOUT 버튼을 누르면 로그아웃 할 수 있습니다. 
- 안 입는 옷 보관함, 즐겨 찾는 옷 보관함, 사고 싶은 옷 보관함에 접근할 수 있습니다.
- 안 입는 옷 보관함에서는, TAB1에서 길게 눌러 보관함으로 보냈던 항목들을 Category별로 볼 수 있습니다. 항목을 짧게 누르면 옷을 TAB1으로 다시 꺼낼 수 있고, 길게 누르면 완전히 삭제됩니다.
- 즐겨 찾는 옷 보관함 또한 Category별로 볼 수 있습니다. 길게 누르면 안 입는 옷 보관함으로 보낼 수 있습니다.
- 사고 싶은 옷 보관함도 Category별로 옷이 정렬되어 있습니다. 짧게 누르면, 해당 옷을 구매할 수 있는 Link가 팝업으로 나타납니다. 해당 Link를 누르면 바로 접속할 수 있습니다. 또한, 길게 누르면 완전히 삭제됩니다.

- OOTD를 추천해주는 Activitiy로 넘어갈 수 있습니다.
- OOTD 추천 Activity에도, 사용자의 카카오톡 프로필, 이름, 성별, 메일이 나타납니다.
- 가입 시 입력했던 선택 입력 사항 또한 수정할 수 있습니다. SAVE 버튼을 누르면 저장이 완료되며, OOTD 추천을 받기 위해서는 모두 입력해야 합니다.
- 오늘의 룩 스타일 중 하나를 선택한 뒤, "GET OOTD" 버튼을 클릭하면 Dalle가 추천해주는 OOTD가 화면에 나타납니다.

***Technical Description***
- API와 안드로이드 스튜디오를 연결하여, 각 보관함의 옷들을 모두 볼 수 있도록 했으며 사용자의 정보를 불러왔습니다.
- AlertDialog를 이용하여 Popup Activity를 처리했습니다.
- 각 항목은 모두 recyclerView로 구현하였습니다.
- Intent를 이용해 인터넷에 연결할 수 있도록 했습니다.
- OpenAI API를 이용해서, Dalle를 이용했습니다.



## 📁 Resources
- APK file : https://drive.google.com/file/d/1EUar1K2QcTziJLPwcTxwlnBRU-BCvEXY/view?usp=sharing



## 추후에 더 발전할 수 있는 부분
- TAP2의 옷 배치 수정하기.
- 더 많은 Category를 만들기.
- 만약 가능하다면, 마네킹이나 사용자의 옷에 직접 옷을 입히기.
