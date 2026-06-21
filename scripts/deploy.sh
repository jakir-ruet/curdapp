#!/bin/bash
# deploy-minikube.sh - Deploy to Minikube Cluster

set -e

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}🚀 Deploying to Minikube Cluster${NC}"
echo -e "${BLUE}========================================${NC}"

# Configuration
DOCKER_USERNAME="your-dockerhub-username"  # Change this
IMAGE_NAME="curdapp"
VERSION=${1:-latest}
NAMESPACE="production"
CLUSTER_NAME="minikube"

echo -e "${YELLOW}📋 Configuration:${NC}"
echo "  Docker Username: ${DOCKER_USERNAME}"
echo "  Image: ${DOCKER_USERNAME}/${IMAGE_NAME}:${VERSION}"
echo "  Namespace: ${NAMESPACE}"
echo "  Cluster: ${CLUSTER_NAME}"

# ============================================
# Step 1: Verify Minikube Status
# ============================================
echo -e "\n${GREEN}🔍 Step 1: Verifying Minikube Cluster${NC}"
if ! minikube status > /dev/null 2>&1; then
    echo -e "${RED}❌ Minikube is not running. Starting Minikube...${NC}"
    minikube start --nodes 3 --cpus 4 --memory 8192
fi

kubectl get nodes
echo -e "${GREEN}✅ Minikube cluster is ready${NC}"

# ============================================
# Step 2: Enable Minikube Addons
# ============================================
echo -e "\n${GREEN}🔧 Step 2: Enabling Minikube Addons${NC}"
minikube addons enable ingress
minikube addons enable metrics-server
minikube addons enable dashboard

# ============================================
# Step 3: Build and Push Docker Image
# ============================================
echo -e "\n${GREEN}🐳 Step 3: Building and Pushing Docker Image${NC}"

# Set Docker environment to Minikube
eval $(minikube docker-env)

echo -e "${YELLOW}Building Docker image...${NC}"
docker build -t ${DOCKER_USERNAME}/${IMAGE_NAME}:${VERSION} .
docker tag ${DOCKER_USERNAME}/${IMAGE_NAME}:${VERSION} ${DOCKER_USERNAME}/${IMAGE_NAME}:latest

# If you want to push to Docker Hub (optional)
# docker login -u ${DOCKER_USERNAME}
# docker push ${DOCKER_USERNAME}/${IMAGE_NAME}:${VERSION}
# docker push ${DOCKER_USERNAME}/${IMAGE_NAME}:latest

echo -e "${GREEN}✅ Docker image built${NC}"

# ============================================
# Step 4: Create Namespace
# ============================================
echo -e "\n${GREEN}📁 Step 4: Creating Namespace${NC}"
kubectl create namespace ${NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -

# ============================================
# Step 5: Deploy Database
# ============================================
echo -e "\n${GREEN}🗄️ Step 5: Deploying PostgreSQL${NC}"
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/pvc.yaml
kubectl apply -f k8s/postgres-statefulset.yaml

# Wait for database to be ready
echo -e "${YELLOW}⏳ Waiting for PostgreSQL to be ready...${NC}"
kubectl wait --for=condition=ready pod -l app=postgres -n ${NAMESPACE} --timeout=120s || {
    echo -e "${RED}❌ PostgreSQL failed to start. Checking logs...${NC}"
    kubectl logs -l app=postgres -n ${NAMESPACE}
    exit 1
}

# ============================================
# Step 6: Deploy Application
# ============================================
echo -e "\n${GREEN}☸️ Step 6: Deploying Application${NC}"

# Update deployment with image
sed -i "s|\${DOCKER_USERNAME}|${DOCKER_USERNAME}|g" k8s/deployment.yaml
sed -i "s|\${IMAGE_TAG}|${VERSION}|g" k8s/deployment.yaml

kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml

# Wait for deployment
echo -e "${YELLOW}⏳ Waiting for application to be ready...${NC}"
kubectl rollout status deployment/curdapp -n ${NAMESPACE} --timeout=300s

# ============================================
# Step 7: Verify Deployment
# ============================================
echo -e "\n${GREEN}🔍 Step 7: Verifying Deployment${NC}"

# Check pods
echo -e "\n${YELLOW}📊 Pod Status:${NC}"
kubectl get pods -n ${NAMESPACE} -o wide

# Check services
echo -e "\n${YELLOW}📊 Service Status:${NC}"
kubectl get svc -n ${NAMESPACE}

# Check ingress
echo -e "\n${YELLOW}📊 Ingress Status:${NC}"
kubectl get ingress -n ${NAMESPACE}

# ============================================
# Step 8: Health Check
# ============================================
echo -e "\n${GREEN}🏥 Step 8: Running Health Check${NC}"

# Get service IP
SERVICE_IP=$(kubectl get svc curdapp-service -n ${NAMESPACE} -o jsonpath='{.spec.clusterIP}')

# Test health endpoint
echo -e "${YELLOW}Testing health endpoint...${NC}"
kubectl run test-curl --image=curlimages/curl --rm -it --restart=Never -n ${NAMESPACE} -- \
    curl -f http://curdapp-service/actuator/health || {
    echo -e "${RED}❌ Health check failed${NC}"
    kubectl logs -l app=curdapp -n ${NAMESPACE} --tail=50
    exit 1
}

echo -e "${GREEN}✅ Health check passed!${NC}"

# ============================================
# Step 9: Show Access Information
# ============================================
echo -e "\n${BLUE}========================================${NC}"
echo -e "${GREEN}✅ Deployment Completed Successfully!${NC}"
echo -e "${BLUE}========================================${NC}"

echo -e "\n${YELLOW}📋 Access Information:${NC}"

# Get Minikube IP
MINIKUBE_IP=$(minikube ip)
echo -e "  🌐 Minikube IP: ${MINIKUBE_IP}"

# Get NodePort if using NodePort service
NODE_PORT=$(kubectl get svc curdapp-service -n ${NAMESPACE} -o jsonpath='{.spec.ports[0].nodePort}' 2>/dev/null || echo "")
if [ ! -z "${NODE_PORT}" ]; then
    echo -e "  🔗 Application URL: http://${MINIKUBE_IP}:${NODE_PORT}"
fi

echo -e "  🔗 Ingress URL: http://curdapp.local"
echo -e "  🔗 Health Check: http://curdapp.local/actuator/health"
echo -e "  🔗 Swagger UI: http://curdapp.local/swagger-ui.html"

echo -e "\n${YELLOW}📊 Useful Commands:${NC}"
echo -e "  # Get pods:"
echo -e "  kubectl get pods -n ${NAMESPACE} -o wide"
echo -e "\n  # View logs:"
echo -e "  kubectl logs -f deployment/curdapp -n ${NAMESPACE}"
echo -e "\n  # View database logs:"
echo -e "  kubectl logs -f statefulset/postgres -n ${NAMESPACE}"
echo -e "\n  # Scale application:"
echo -e "  kubectl scale deployment/curdapp --replicas=5 -n ${NAMESPACE}"
echo -e "\n  # Access database:"
echo -e "  kubectl exec -it statefulset/postgres -n ${NAMESPACE} -- psql -U postgres -d curdappdb"
echo -e "\n  # Port forward for local access:"
echo -e "  kubectl port-forward deployment/curdapp 8090:8090 -n ${NAMESPACE}"

# ============================================
# Step 10: Optional - Enable Dashboard
# ============================================
echo -e "\n${YELLOW}📊 Minikube Dashboard:${NC}"
echo -e "  Run: minikube dashboard"
echo -e "  Or: kubectl proxy and open http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/http:kubernetes-dashboard:/proxy/"

echo -e "\n${GREEN}🎉 Deployment complete!${NC}"
