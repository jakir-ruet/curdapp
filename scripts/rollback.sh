#!/bin/bash
# rollback.sh - Rollback to previous version

set -e

NAMESPACE="production"
DEPLOYMENT="curdapp"

echo "🔄 Rolling back deployment..."
kubectl rollout undo deployment/${DEPLOYMENT} -n ${NAMESPACE}
kubectl rollout status deployment/${DEPLOYMENT} -n ${NAMESPACE}

echo "✅ Rollback completed!"
