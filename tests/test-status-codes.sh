#!/bin/bash

BASE_URL="http://localhost:4001"

test_status() {
    local URL="$1"
    local EXPECTED="$2"
    local DESCRIPTION="$3"

    # -W OUTPUTS JUST THE STATUS CODE -O DISCARDS THE BODY
    local ACTUAL=$(curl -s -o /dev/null -w "%{http_code}" "$URL")

    if [ "$ACTUAL" -eq "$EXPECTED" ]; then
        echo "PASS: $DESCRIPTION ($URL) returned $ACTUAL as expected."
    else
        echo "FAIL: $DESCRIPTION ($URL) returned $ACTUAL, expected $EXPECTED."
    fi
}

test_status "$BASE_URL/health" 200 "Health endpoint should return 200 OK"
test_status "$BASE_URL/items" 200 "Items endpoint should return 200 OK"
test_status "$BASE_URL/nonexistent" 404 "Nonexistent endpoint should return 404 Not Found"
test_status "$BASE_URL/items/9999" 404 "Nonexistent item should return 404 Not Found"   