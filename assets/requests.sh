#!/bin/bash

for i in $(seq 1 100)
do
    curl -XPOST -d "{\"id\": \"test$i\", \"vector\": [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.$i]}" --header "Content-Type: application/json" http://localhost:8080/insert
done

curl -XPOST -d "[{\"id\": \"test-s1\", \"vector\": [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.1]}, {\"id\": \"test-s2\", \"vector\": [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.2]}]" --header "Content-Type: application/json" http://localhost:8080/streaminsert

curl -XPOST http://localhost:8080/index

curl -XPOST -d "{\"num\": 3, \"radius\": -1.0, \"epsilon\": 0.1, \"vector\": [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]}" --header "Content-Type: application/json" http://localhost:8080/search
