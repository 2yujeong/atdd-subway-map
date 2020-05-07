## 지하철 정보 관리
### 1단계 - 지하철 노선 / 인수 테스트
- [x] 노선 CRUD
    - [x] 노선 추가 POST : "/lines"
    - [x] 노선 목록 조회 GET : "/lines"
    - [x] 노선 조회 GET : "/lines/{id}"
    - [x] 노선 수정 PUT : "/lines/{id}"
    - [x] 노선 제거 DELETE : "/lines/{id}"
    
### 2단계 - 지하철 노선 페이지
- [x] 노선 목록 조회 
    - [x] 페이지 호출 시 미리 저장한 지하철 노선 조회
    - [x] 지하철 노선 목록 조회 API 사용
- [ ] 노선 추가
    - [ ] 노선 추가 버튼을 누르면 아래와 같은 팝업화면이 뜸
    - [ ] 노선 이름과 정보를 입력
    - [ ] 지하철 노선 추가 API 사용
- [ ] 노선 상세 정보 조회
    - [ ] 목록에서 노선 선택 시 상세 정보를 조회
- [ ] 노선 수정
    - [ ] 목록에서 우측 수정 버튼을 통해 수정 팝업화면 노출
    - [ ] 수정 팝업 노출 시 기존 정보는 입력되어 있어야 함
    - [ ] 정보 수정 후 지하철 노선 수정 API 사용
- [ ] 노선 삭제
    - [ ] 목록에서 우측 삭제 버튼을 통해 삭제
    - [ ] 지하철 노선 삭제 API 사용
- [ ] 프론트 유효성 검증
    -  [ ] 추가
    -  [ ] 수정