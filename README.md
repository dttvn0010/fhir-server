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
 - 
## Các thao tác với resource  
 - Validate
 - Tạo mới
 - Chỉnh sửa
 - Xóa
 - Tìm kiếm
 
### Validate resouce  
- API URL : <BASE_URL>/R4/<ResourceType>/$validate
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
 - Lấy danh sách CodeSystem : `<TERMINOLOGY_SERVER_URL>/CodeSystem` : trả về danh sách các resource codesystem với id, url, name, (không cần concept)
 - Lấy nội dung của một CodeSystem theo id : `<TERMINOLOGY_SERVER_URL>/CodeSystem/<codeSystemId>` : trả vể nội dung của codeSystem có kèm danh sách concept
 - Lấy danh sách ValueSet: : `<TERMINOLOGY_SERVER_URL>/ValueSet` 
 - Lấy nội dung của một ValueSet theo id : `<TERMINOLOGY_SERVER_URL>/ValueSet/<valueSetId>`

Trong trường hợp không có terminology server, có thể sử dụng các API test đi kèm với fhir server. Các API này được cung cấp trong 2 file `CodeSystemProvider.java` và `ValueSetProvider.java`. Nội dung các CodeSystem và ValueSet được API test đọc ra từ các thư mục `src/main/resources/code-systems` và `src/main/resources/value-sets`, có thể bổ sung thêm các file json chứa nội dung CodeSystem & ValueSet khác vào 2 thư mục này để test. Lưu ý : tên file (không kèm đuôi mở rộng) phải trùng với id của CodeSystem/ValueSet, ví dụ:
```
{
  "resourceType": "CodeSystem",
  "id": "Ethnic-Group",
  "url": "http://fhir.moh.gov.vn/CodeSystem/Ethnic-Group",
  ``` --> tên file danh mục cần đặt là `Ethnic-Group`



### Thêm mới resouce
