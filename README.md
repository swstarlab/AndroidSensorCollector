안드로이드 라이프옴 센서 수집기
=========

안드로이드 라이프옴 센서 수집기는 센서 데이터를 기반으로 사용자의 일상 생활을 실시간으로 기록하는 프로그램이다. 오랜 기간동안 수집된 데이터는 사용자의 생활 패턴을 일반화시키고 예측되는데 응용된다. 본 프로젝트는 안드로이드 앱, 웹 서버(Java Servlet), MySQL 스키마로 이루어져 있다. 이 안드로이드 앱은 다음 목록에 나오는 데이터를 수집한다.

* GPS 좌표 (latitude, longitude)
* 네트워크 좌표 (latitude, longitude)
* 가속도 센서 데이터
* MS 밴드 데이터 (심장 박동수, 가속도, 체온)
* 장소 (사용자가 입력)
* 활동 (사용자가 입력)

안드로이드 앱은 실행 이후 백그라운드 서비스로 돌아가면서 실시간으로 센서 데이터를 서버에 전송한다. 장소와 활동 정보는 사용자가 직접 입력하여 서버에 직접 전송한다. 정확한 장소 명칭을 저장하기 위해 장소 검색 서비스인 포스퀘어의 API를 활용한다. 

설치법
============
MySQL 설치후 데이터베이스 스키마 파일인 lifome_db_YYYY_MM_dd.sql을 실행한다.
```
mysql < lifome_db_2015_10_28.sql
```

SensorWeb은 이클립스로 import한 후, Maven 플러그인을 활성화시켜야 한다.
이 웹 프로직트를 WAR 파일로 export한 후 tomcat의 webapps 폴더 안에 넣으면 웹 서버가 실행되어 센서 데이터를 받아들일 준비가 된다.

안드로이드 앱 프로젝트는 Android Studio로 import하여 사용할 수 있다.


