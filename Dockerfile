# Stage 1: Build the application using Maven
FROM maven:3-openjdk-17-slim as builder

WORKDIR /build
  
  # Copy the dependency specifications
COPY pom.xml pom.xml
COPY Catalog/pom.xml Catalog/pom.xml
COPY UserManagement/pom.xml UserManagement/pom.xml
  
  # Resolve dependencies for `Catalog` module
RUN mvn -q -ntp -B -pl Catalog -am dependency:go-offline
  
  # Copy full sources for `Catalog` module
COPY Catalog Catalog
  
  # Install the Catalog module in the local Maven repo (`.m2`)
RUN mvn -q -B -pl Catalog install
  
  # Resolve dependencies for the main application (`UserManagement`)
RUN mvn -q -ntp -B -pl UserManagement -am dependency:go-offline
  
  # Copy sources for main application
COPY UserManagement UserManagement
  
  # Package the Catalog and UserManagement modules together
RUN mvn -q -ntp -B -pl Catalog,UserManagement package

RUN mkdir -p /jar-layers
WORKDIR /jar-layers
  
  # Extract JAR layers
RUN java -Djarmode=layertools -jar /build/UserManagement/target/*.jar extract
  
  # Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre

RUN mkdir -p /app
WORKDIR /app
  
  # Copy JAR layers
COPY --from=builder /jar-layers/ .

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
