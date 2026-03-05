#!/bin/bash
# Test the API server is reachable and responds to requests.

echo "Testing API server connection..."
curl http://localhost:4001/health
echo ""