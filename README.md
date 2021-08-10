# 계단정복지도 서버

건물의 accessibility 정보를 수집하고 공유하기 위한 프로젝트인 계단정복지도의 서버 프로젝트.

## 계단정복지도 서버 띄우기

### 서버 배포 방법

1. 테라폼을 실행한다.
```
$ cd terraform
$ terraform plan -out "plan.tfplan" # AWS credential이 세팅되어 있어야 한다.
$ terraform apply plan.tfplan
```
2. EC2에 ssh로 접근해서 이 레포지토리를 클론받는다.
3. 실서버의 properties 파일을 `./deploy/prod/application-prod.properties`에 저장한다. 참고로, 현재 이 파일은 레포지토리 관리자의 로컬과 EC2 내부에서 수기로 관리하고 있다.
4. 배포 스크립트를 실행하여 배포한다.
```
$ ./bin/docker-push.sh # 도커 이미지 생성 & ECR에 푸시; 로컬에서 실행하려면 AWS credentials 세팅이 되어 있어야 한다.
$ ./bin/deploy.sh # ECR에서 도커 이미지 받아오기 & 도커 실행; EC2 내부가 아니면 RDS에 붙을 수 없으므로 오류가 난다.
```

이 중 1~3번은 수기로 진행해야 하고, 4번 과정은 github action(`cicd.yaml`)에 의해 main branch 통합이 발생할 경우 자동으로 이루어지고 있다.
github action에 의한 CD가 제대로 동작하려면 `cicd.yaml`에 있는 secret을 미리 설정해주어야 한다.

*로컬에서 띄우려면 our-map-server gradle project의 OurMapServerKt.main을 실행하면 된다.

### 장소/건물 데이터 붓기

Building / Place 데이터를 부어야 정상적으로 동작한다. 장소/건물 데이터를 붓는 SQL 스크립트는 git에 포함하지 않고 레포지토리 관리자가 별도로 관리하고 있다.
데이터를 부으려면 일단 서버를 한 번 띄우고 liquibase migration을 실행시켜서 테이블을 생성해야 한다.
