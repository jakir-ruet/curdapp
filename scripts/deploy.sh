#!/bin/bash
# deploy.sh - Production Deployment Script

set -e

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}🚀 Starting Production Deployment${NC}"

# Configuration
NAMESPACE="production"
DOCKER_REGISTRY="your-docker-registry.com"
IMAGE_NAME="curdapp"
VERSION=${1:-latest}

# Build Docker image
echo -e "${YELLOW}🐳 Building Docker image...${NC}"
docker build -t ${DOCKER_REGISTRY}/${IMAGE_NAME}:${VERSION} .

# Push to registry
echo -e "${YELLOW}📤 Pushing image to registry...${NC}"
docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:${VERSION}

# Apply Kubernetes manifests
echo -e "${GREEN}☸️ Deploying to Kubernetes...${NC}"
kubectl apply -f k8s/configmap.yaml -n ${NAMESPACE}
kubectl apply -f k8s/secrets.yaml -n ${NAMESPACE}
kubectl apply -f k8s/pvc.yaml -n ${NAMESPACE}
kubectl apply -f k8s/postgres-deployment.yaml -n ${NAMESPACE}

# Update deployment
kubectl set image deployment/curdapp curdapp=${DOCKER_REGISTRY}/${IMAGE_NAME}:${VERSION} -n ${NAMESPACE}

# Wait for rollout
echo -e "${YELLOW}⏳ Waiting for rollout...${NC}"
kubectl rollout status deployment/curdapp -n ${NAMESPACE} --timeout=300s

# Verify
echo -e "${GREEN}✅ Deployment completed!${NC}"
kubectl get pods -n ${NAMESPACE} -l app=curdapp
