FROM golang:1.27 AS build

WORKDIR /app

COPY go.mod ./

RUN go mod download

COPY . .

RUN go build -o fieldops-analytics .


FROM debian:bookworm-slim

WORKDIR /app

COPY --from=build /app/fieldops-analytics .

EXPOSE 8081

CMD ["./fieldops-analytics"]
