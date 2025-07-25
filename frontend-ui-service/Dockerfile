# --- Étape 1: La phase de Build ---
FROM maven:3.9-eclipse-temurin-21 AS build

# Le répertoire de travail est la racine du projet parent
WORKDIR /app

# 1. Copier le pom.xml du projet parent
COPY pom.xml .

# 2. Copier les pom.xml de tous les modules
COPY patient-service/pom.xml ./patient-service/
COPY notes-service/pom.xml ./notes-service/
COPY report-service/pom.xml ./report-service/
COPY gateway-service/pom.xml ./gateway-service/
COPY frontend-ui-service/pom.xml ./frontend-ui-service/

# 3. Télécharger les dépendances pour tous les modules (optimisation du cache)
RUN mvn dependency:go-offline

# 4. Copier le code source de TOUS les modules
COPY . .

# 5. Lancer le build pour un module spécifique.
# Le module à construire sera passé en argument.
ARG MODULE_NAME
RUN mvn -pl ${MODULE_NAME} -am clean package -DskipTests


# --- Étape 2: La phase d'Exécution ---
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# On passe à nouveau le nom du module en argument
ARG MODULE_NAME
COPY --from=build /app/${MODULE_NAME}/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]