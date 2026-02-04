localhost:7777
## Cấu hình tài khoản SQL Server

Ứng dụng sử dụng **Microsoft SQL Server** chạy trên localhost.  
Trước khi chạy project, vui lòng cập nhật **username** và **password** SQL Server cho phù hợp với máy của bạn.

Mở file:

src/main/resources/application.properties

Chỉnh các cấu hình sau:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=hotel_booking_system;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=12345
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
