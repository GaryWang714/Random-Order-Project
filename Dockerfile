FROM ubuntu:latest
LABEL authors="garyw"

ENTRYPOINT ["top", "-b"]