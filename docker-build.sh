./gradlew clean build

docker build -t yaincoding/wheretruck:$1 .
docker push yaincoding/wheretruck:$1