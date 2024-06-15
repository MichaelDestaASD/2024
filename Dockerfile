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

# Package the Catalog module
RUN mvn -q -ntp -B -pl Catalog package

# Package the UserManagement module
RUN mvn -q -ntp -B -pl UserManagement package

# Stage 2: Create the Catalog runtime image
FROM eclipse-temurin:17-jre as catalog-runtime

RUN mkdir -p /app/catalog
WORKDIR /app/catalog

# Copy the Catalog JAR
COPY --from=builder /build/Catalog/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "Catalog.jar"]

# Stage 3: Create the UserManagement runtime image
FROM eclipse-temurin:17-jre as usermanagement-runtime

RUN mkdir -p /app/usermanagement
WORKDIR /app/usermanagement

# Copy the UserManagement JAR
COPY --from=builder /build/UserManagement/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "UserManagement.jar"]
