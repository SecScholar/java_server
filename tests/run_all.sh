#!/bin/bash

BASE_DIR="$(dirname "$0")"

for test_file in "$BASE_DIR"/tests/*.sh; do
    echo "Running $(basename "$test_file")"
    bash "$test_file"
    echo ""
done
