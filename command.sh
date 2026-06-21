# 1. Build your application
cd devops-app
mvn clean package

# 2. Build and deploy locally
docker compose up -d

# 3. Test locally
curl http://localhost:8090/actuator/health

# 4. Deploy to Kubernetes
./deploy.sh latest

# 5. Check status
kubectl get pods -n production
kubectl logs -f deployment/curdapp -n production

# 6. Access application
kubectl port-forward deployment/curdapp 8090:8090 -n production
# Now open: http://localhost:8090

# 7. Rollback if needed
./rollback.sh

# 8. Scale application
kubectl scale deployment/curdapp --replicas=5 -n production
