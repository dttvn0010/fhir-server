# FHIR SERVER 

## Cài đặt 
Thay đổi cấu hình database trong file `src/main/resources/application.properties`  

```
spring.data.mongodb.host=<DB_HOST>
spring.data.mongodb.port=<DB_PORT>

spring.data.mongodb.username=<DB_USERNAME>
spring.data.mongodb.password=<DB_PASSWORD>
spring.data.mongodb.database=<DB_NAME>

```

## Chạy ứng dụng  
```
mvn spring-boot:run
```
## Danh sách Resource hỗ trợ  
 - AllergyIntolerance
 - Condition
 - DiagnosticReport
 - Encounter
 - FamilyMemberHistory
 - Immunization
 - Medication
 - MedicationRequest
 - Observation
 - Patient
 - Practitioner
 - Procedure
 - RelatedPerson
 - ServiceRequest
 - Specimen
 
## Các thao tác với resource  
 - Validate
 - Tạo mới
 - Chỉnh sửa
 - Xóa
 - Tìm kiếm
 - Xem nội dung một resource
 - Xem nội dung một resource theo phiên bản
 
### Validate resouce  
- API URL : `<BASE_URL>/R4/<ResourceType>/$validate`
- Method: POST
- Body: nội dung của resouce
- Ví dụ:
```
curl -H "Content-Type:application/json" -X POST -d "@src/main/resources/sample-resources/patient.json" 'http://127.0.0.1:8080/R4/Patient/$validate'
```
  
### Validate danh mục
Để validate danh mục trong các resouce, cần kết nối với terminology server. Thông số của termninology được đặt trong file `src/main/resources/application.properties`  
```
fhir.terminology.server.url=<TERMINOLOGY_SERVER_URL>
```

Terminology server cần hỗ trợ các API sau:  
 - Lấy danh sách CodeSystem : `<TERMINOLOGY_SERVER_URL>/CodeSystem` :  
   trả về danh sách các resource codesystem với id, url, name, (không cần concept)
 - Lấy nội dung của một CodeSystem theo id : `<TERMINOLOGY_SERVER_URL>/CodeSystem/<codeSystemId>`  
   trả vể nội dung của codeSystem có kèm danh sách concept
 - Lấy danh sách ValueSet: : `<TERMINOLOGY_SERVER_URL>/ValueSet` 
 - Lấy nội dung của một ValueSet theo id : `<TERMINOLOGY_SERVER_URL>/ValueSet/<valueSetId>`

Trong trường hợp không có terminology server, có thể sử dụng các API test đi kèm với fhir server. Các API này được cung cấp trong 2 file `CodeSystemProvider.java` và `ValueSetProvider.java`. Nội dung các CodeSystem và ValueSet được API test đọc ra từ các thư mục `src/main/resources/code-systems` và `src/main/resources/value-sets`, có thể bổ sung thêm các file json chứa nội dung CodeSystem & ValueSet khác vào 2 thư mục này để test. Lưu ý : tên file (không kèm đuôi mở rộng) phải trùng với id của CodeSystem/ValueSet, ví dụ:
```
{
  "resourceType": "CodeSystem",
  "id": "Ethnic-Group",
  "url": "http://fhir.moh.gov.vn/CodeSystem/Ethnic-Group",
  ...
  ``` 
--> tên file danh mục cần đặt là `Ethnic-Group`

### Validate theo profile tùy chọn
Để validate theo profile tùy chọn, đặt tên profile vào trong phần meta của danh mục, ví dụ
```
{  
  "resourceType": "Patient",
  "meta": {
     "profile": [ "http://fhir.moh.gov.vn/StructureDefinition/Patient-vn" ]
  },
...
```
Terminology server phải cung cấp API cho phép lấy về nội dung của StructureDefinition khai báo trong phần profile. Nếu chưa có terminology server và dùng tính năng test của fhir server thì cần đặt file json chứa nội dung của StructureDefinition vào thư mục `src/main/resources/structure-definitions` (tương tự như với CodeSystem và ValueSet)

### Thêm mới resource
- API URL : `<BASE_URL>/R4/<ResourceType>`
- Method: POST
- Body: nội dung của resouce
- Ví dụ:
```
curl -H "Content-Type:application/json" -X POST -d "@src/main/resources/sample-resources/patient.json" http://127.0.0.1:8080/R4/Patient
```
 
### Chỉnh sửa resource
- API URL : `<BASE_URL>/R4/<ResourceType>/<resourceId>`
- Method: PUT
- Body: nội dung của resource
- Ví dụ:
```
curl -H "Content-Type:application/json" -X PUT -d "@src/main/resources/sample-resources/patient.json" http://127.0.0.1:8080/R4/Patient/229189c0-ad05-4b42-909a-5743e9bc5831
```
 Lưu ý: khi chỉnh sửa resource, trong body của json message phải chứa trường id , và giá trị này cần trùng với <resourceId> trên url API  
 Khi update resource thì giá trị cũ không bị xóa mà chuyển sang trạng thái inactive (trường _active bằng false), và vẫn có thể đọc ra bằng hàm đọc lịch sử resource ở phần dưới)
 
 ### Xóa resource
- API URL : `<BASE_URL>/R4/<ResourceType>/<resourceId>`
- Method: DELETE
- Body: none
- Ví dụ:
```
curl -H "Content-Type:application/json" -X DELETE http://127.0.0.1:8080/R4/Patient/229189c0-ad05-4b42-909a-5743e9bc5831
 ```
 Tương tự như khi update, resource bị xóa chỉ chuyển sang trạng thái inactive và vẫn có thể xem bằng hàm xem lịch sử resource
 
 ### Tìm kiếm resource
- API URL : `<BASE_URL>/R4/<ResourceType>?<param1>=<value1>&<param2>=<value2>`
- Method: GET
- Ví dụ:
```
 curl http://127.0.0.1:8080/R4/Patient?name=Nguyễn
 curl http://127.0.0.1:8080/R4/Encounter?patient=229189c0-ad05-4b42-909a-5743e9bc5831
 curl http://127.0.0.1:8080/R4/Observation?encounter=c69ceae4-68bb-4abb-8aef-e01847b39e29
```
 
 ### Lấy nội dung một resource
- API URL : `<BASE_URL>/R4/<ResourceType>/<resourceId>`
- Method: GET
- Ví dụ:
```
curl http://127.0.0.1:8080/R4/Patient/229189c0-ad05-4b42-909a-5743e9bc5831
```
 
 ### Lấy nội dung một resource theo một phiên bản (xem lịch sử)
- API URL : `<BASE_URL>/R4/<ResourceType>/<resourceId>/_history/<version>`
- Method: GET
- Ví dụ:
```
curl http://127.0.0.1:8080/R4/Patient/229189c0-ad05-4b42-909a-5743e9bc5831/_history/1
```
