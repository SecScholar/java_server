#!/bin/bash
#  Test the get endpoints of the API server.

# Variables
BASE_URL="http://localhost:4001"
ITEMS_ENDPOINT="$BASE_URL/items"

echo "Testing GET /items..."
echo "URL: $ITEMS_ENDPOINT"
echo ""

# Store curl output in a variable using command substitution
RESPONSE=$(curl -s "$ITEMS_ENDPOINT")

echo "Response from GET /items:"
echo "$RESPONSE"
echo ""