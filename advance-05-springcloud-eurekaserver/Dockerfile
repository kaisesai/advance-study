#FROM openjdk:8-jdk-alpine
#RUN addgroup -S spring && adduser -S spring -G spring
#USER spring:spring
#
#ARG JAR_FILE=build/libs/*.jar
#COPY ${JAR_FILE} app.jar
#
#ARG JAVA_OPTS='-Xms128m -Xmx128m'
#
## 请注意${0}“命令”（在本例中为第一个程序参数）和${@}“命令参数”（其余程序参数）的使用。
#ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${0} ${@}"]

# 2. 分层打包
FROM openjdk:8-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

#ARG JAR_FILE=build/libs/*.jar
#COPY ${JAR_FILE} app.jar

ARG JAVA_OPTS='-Xms128m -Xmx128m'

ARG DEPENDENCY=build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
# 请注意${0}“命令”（在本例中为第一个程序参数）和${@}“命令参数”（其余程序参数）的使用。
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS}","-cp","app:app/lib/*","com.kaige.advance.springcloud.erurekaserver.EurekaServerApplication ${0} ${@}"]




# 3. 以下是提高docker 编译效率的分层手段
# syntax=docker/dockerfile:experimental
#FROM openjdk:8-jdk-alpine AS build
##FROM gradle:6.9.1-jdk8 AS build
#WORKDIR /workspace/app
#
#COPY . /workspace/app
#
#RUN --mount=type=cache,target=/root/.gradle /workspace/app/gradle clean build
#RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)
#
#FROM openjdk:8-jdk-alpine
#RUN addgroup -S spring && adduser -S spring -G spring
#USER spring:spring
#
#VOLUME /tmp
#ARG DEPENDENCY=/workspace/app/build/dependency
#COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
#COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
#COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
#ENTRYPOINT ["java","-cp","app:app/lib/*","com.kaige.advance.springcloud.erurekaserver.EurekaServerApplication"]



