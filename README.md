# An example repository for calling Vald APIs from Ktor server

To run Vald Agent NGT,

```sh
❯ wget https://github.com/vdaas/vald/releases/download/v1.1.2/vald-agent-ngt-linux-amd64.zip
❯ unzip vald-agent-ngt-linux-amd64.zip
❯ ./ngt -c assets/run.yaml
```

To run the Ktor server,

```sh
❯ make run
```

To send requests to the server,

```sh
❯ curl -XPOST -d "{\"id\": \"test\", \"vector\": [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]}" --header "Content-Type: application/json" http://localhost:8080/insert
{
  "name_": "d2e91195d1af",
  "uuid_": "test",
  "ips_": [
    "127.0.0.1"
  ],
  "memoizedIsInitialized": 1,
  "unknownFields": {
    "fields": {},
    "fieldsDescending": {}
  },
  "memoizedSize": -1,
  "memoizedHashCode": 0
}

❯ curl -XPOST http://localhost:8080/index
create index finished

❯ curl -XPOST -d "{\"num\": 3, \"radius\": -1.0, \"epsilon\": 0.1, \"vector\": [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]}" --header "Content-Type: application/json" http://localhost:8080/search
{
  "requestId_": "",
  "results_": [
    {
      "id_": "test",
      "distance_": 0.0,
      "memoizedIsInitialized": -1,
      "unknownFields": {
        "fields": {},
        "fieldsDescending": {}
      },
      "memoizedSize": -1,
      "memoizedHashCode": 0
    }
  ],
  "memoizedIsInitialized": 1,
  "unknownFields": {
    "fields": {},
    "fieldsDescending": {}
  },
  "memoizedSize": -1,
  "memoizedHashCode": 0
}
```

Or `requests.sh` is also available.

```sh
❯ assets/requests.sh
```

