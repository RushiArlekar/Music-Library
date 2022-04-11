FROM openjdk:11
COPY . /opt
WORKDIR /opt
ADD target/library-0.0.1-SNAPSHOT.jar library.jar
EXPOSE 8091
ENTRYPOINT ["java","-jar","library.jar"]