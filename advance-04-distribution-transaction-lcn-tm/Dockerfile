FROM frolvlad/alpine-oraclejdk8:slim
RUN ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' > /etc/timezone
#COPY txlcn-tm-5.0.2.RELEASE.jar tm.jar

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ARG JAVA_OPTS='-Xms128m -Xmx128m'

# 请注意${0}“命令”（在本例中为第一个程序参数）和${@}“命令参数”（其余程序参数）的使用。
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${0} ${@}"]
