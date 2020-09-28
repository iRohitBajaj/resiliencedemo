FROM adoptopenjdk/openjdk11
ADD . /code
WORKDIR /code
RUN chmod -R 777 .
RUN ./mvnw clean package
CMD ./mvnw spring-boot:run
