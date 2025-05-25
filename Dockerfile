# Gunakan base image OpenJDK 17 slim untuk ukuran yang lebih kecil
FROM openjdk:17-jdk-slim

# Set working directory di dalam container
WORKDIR /app

# Copy JAR aplikasi Anda ke dalam container.
# Asumsi nama JAR setelah 'mvn clean package' adalah 'flix-0.0.1-SNAPSHOT.jar'
# di dalam direktori 'target/'. Pastikan Anda sudah menjalankan perintah build Maven ini.
COPY target/flix-0.0.1-SNAPSHOT.jar app.jar

# Expose port yang digunakan aplikasi Spring Boot Anda.
# Berdasarkan file .env Anda, SERVER_PORT adalah 8081.
EXPOSE 8081

# Command untuk menjalankan aplikasi Spring Boot Anda saat container dimulai.
# Menggunakan 'java -jar app.jar' akan menjalankan aplikasi sebagai executable JAR.
ENTRYPOINT ["java", "-jar", "app.jar"]
