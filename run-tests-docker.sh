#!/bin/bash
set -e

echo "Building Docker image..."
docker build -t redis-server-test .

echo "Running tests in Docker container..."
docker run --rm redis-server-test
