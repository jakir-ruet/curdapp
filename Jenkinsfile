// Jenkinsfile - Production Grade CI/CD Pipeline
// For your Spring Boot CRUD Application (curdapp-0.0.1-SNAPSHOT.jar)

pipeline {
    agent any

    environment {
        // Build Information
        BUILD_NUMBER = "${env.BUILD_NUMBER}"
        BUILD_TIMESTAMP = sh(script: "date +%Y%m%d%H%M%S", returnStdout: true).trim()
        COMMIT_HASH = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()

        // Docker Registry (Update these with your registry)
        DOCKER_REGISTRY = 'your-docker-registry.com'  // e.g., docker.io/yourusername
        DOCKER_IMAGE = "${DOCKER_REGISTRY}/curdapp"
        DOCKER_TAG = "${BUILD_TIMESTAMP}-${env.BUILD_NUMBER}"

        // Maven Configuration
        MAVEN_OPTS = '-Xmx2048m'
        MAVEN_HOME = tool name: 'maven-3', type: 'maven'

        // Kubernetes Namespace
        K8S_NAMESPACE = 'production'
        K8S_DEPLOYMENT = 'curdapp'

        // Slack Notifications
        SLACK_CHANNEL = '#ci-cd-alerts'
    }

    parameters {
        choice(
            name: 'DEPLOY_ENV',
            choices: ['dev', 'staging', 'production'],
            description: 'Select deployment environment'
        )
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: false,
            description: 'Skip tests (use with caution)'
        )
        booleanParam(
            name: 'ROLLBACK',
            defaultValue: false,
            description: 'Rollback to previous version'
        )
    }

    stages {
        // ============================================
        // Stage 1: Checkout
        // ============================================
        stage('Checkout') {
            steps {
                checkout scm
                echo "📝 Building version: ${DOCKER_TAG}"
                echo "📝 Commit: ${COMMIT_HASH}"
            }
        }

        // ============================================
        // Stage 2: Build Application
        // ============================================
        stage('Build Application') {
            when {
                expression { !params.ROLLBACK }
            }
            steps {
                script {
                    echo "🔨 Building Spring Boot Application..."
                    sh '''
                        # Clean and build
                        mvn clean compile
                        mvn package -DskipTests=${SKIP_TESTS}

                        # Verify JAR exists
                        ls -la target/*.jar
                    '''
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        // ============================================
        // Stage 3: Run Unit Tests
        // ============================================
        stage('Unit Tests') {
            when {
                expression { !params.SKIP_TESTS && !params.ROLLBACK }
            }
            steps {
                script {
                    echo "🧪 Running Unit Tests..."
                    sh '''
                        mvn test
                        mvn jacoco:report
                    '''
                }
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    publishHTML([
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'Test Coverage Report'
                    ])
                }
            }
        }

        // ============================================
        // Stage 4: Security Scan
        // ============================================
        stage('Security Scan') {
            when {
                expression { !params.SKIP_TESTS && !params.ROLLBACK }
            }
            steps {
                script {
                    echo "🔒 Running Security Scans..."
                    sh '''
                        # OWASP Dependency Check
                        mvn org.owasp:dependency-check-maven:check \
                            -Dformat=HTML \
                            -DoutputDirectory=target/dependency-check \
                            -DfailBuildOnCVSS=7 || true
                    '''
                }
            }
            post {
                always {
                    publishHTML([
                        reportDir: 'target/dependency-check',
                        reportFiles: 'dependency-check-report.html',
                        reportName: 'Security Vulnerability Report'
                    ])
                }
            }
        }

        // ============================================
        // Stage 5: Build Docker Image
        // ============================================
        stage('Build Docker Image') {
            when {
                expression { !params.ROLLBACK }
            }
            steps {
                script {
                    echo "🐳 Building Docker Image..."
                    sh '''
                        # Build image with your JAR
                        docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                        docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest

                        # Verify image
                        docker images ${DOCKER_IMAGE}
                    '''
                }
            }
        }

        // ============================================
        // Stage 6: Push to Registry
        // ============================================
        stage('Push to Registry') {
            when {
                expression { !params.ROLLBACK && params.DEPLOY_ENV != 'dev' }
            }
            steps {
                script {
                    echo "📤 Pushing to Docker Registry..."
                    withCredentials([string(
                        credentialsId: 'docker-registry-password',
                        variable: 'REGISTRY_PASSWORD'
                    )]) {
                        sh """
                            echo ${REGISTRY_PASSWORD} | docker login \
                                ${DOCKER_REGISTRY} -u admin --password-stdin
                            docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
                            docker push ${DOCKER_IMAGE}:latest
                        """
                    }
                }
            }
        }

        // ============================================
        // Stage 7: Deploy to Kubernetes
        // ============================================
        stage('Deploy to Kubernetes') {
            steps {
                script {
                    if (params.ROLLBACK) {
                        echo "🔄 Rolling back to previous version..."
                        sh """
                            kubectl rollout undo deployment/${K8S_DEPLOYMENT} \
                                -n ${K8S_NAMESPACE}
                        """
                    } else {
                        echo "☸️ Deploying to Kubernetes..."

                        // Update deployment with new image
                        sh """
                            # Update deployment
                            kubectl set image deployment/${K8S_DEPLOYMENT} \
                                curdapp=${DOCKER_IMAGE}:${DOCKER_TAG} \
                                -n ${K8S_NAMESPACE}
                        """
                    }

                    // Wait for rollout
                    sh """
                        kubectl rollout status deployment/${K8S_DEPLOYMENT} \
                            -n ${K8S_NAMESPACE} --timeout=300s
                    """
                }
            }
        }

        // ============================================
        // Stage 8: Health Check
        // ============================================
        stage('Health Check') {
            steps {
                script {
                    echo "🏥 Verifying deployment..."
                    sh '''
                        # Check pod status
                        kubectl get pods -n ${K8S_NAMESPACE} -l app=curdapp

                        # Check service
                        kubectl get svc curdapp-service -n ${K8S_NAMESPACE}

                        # Test health endpoint
                        kubectl run test-curl --image=curlimages/curl --rm -it --restart=Never -- \
                            curl -f http://curdapp-service/actuator/health || exit 1
                    '''
                }
            }
        }

        // ============================================
        // Stage 9: Smoke Tests
        // ============================================
        stage('Smoke Tests') {
            when {
                expression { params.DEPLOY_ENV == 'production' && !params.ROLLBACK }
            }
            steps {
                script {
                    echo "🔥 Running Smoke Tests..."
                    sh '''
                        # Test API endpoints
                        curl -f http://curdapp-service/api/products || exit 1
                        curl -f http://curdapp-service/swagger-ui.html || exit 1
                        curl -f http://curdapp-service/actuator/info || exit 1

                        echo "✅ All smoke tests passed!"
                    '''
                }
            }
        }

        // ============================================
        // Stage 10: Cleanup
        // ============================================
        stage('Cleanup') {
            steps {
                script {
                    echo "🧹 Cleaning up..."
                    sh '''
                        # Keep last 5 images
                        docker images ${DOCKER_IMAGE} --format "{{.Tag}}" | \
                            tail -n +6 | xargs -r docker rmi -f || true

                        # Clean build artifacts
                        mvn clean
                    '''
                }
            }
        }
    }

    // ============================================
    // POST ACTIONS
    // ============================================
    post {
        success {
            script {
                slackSend(
                    channel: env.SLACK_CHANNEL,
                    color: 'good',
                    message: """
✅ **Deployment Successful**
• Application: ${env.JOB_NAME}
• Version: ${DOCKER_TAG}
• Environment: ${params.DEPLOY_ENV}
• URL: http://api.curdapp.com
• Build: ${env.BUILD_URL}
                    """.stripIndent()
                )
            }
        }

        failure {
            script {
                slackSend(
                    channel: env.SLACK_CHANNEL,
                    color: 'danger',
                    message: """
❌ **Deployment Failed**
• Application: ${env.JOB_NAME}
• Version: ${DOCKER_TAG}
• Stage: ${env.STAGE_NAME}
• URL: ${env.BUILD_URL}
• Action Required: Investigate logs
                    """.stripIndent()
                )

                // Auto-rollback on failure
                script {
                    try {
                        sh """
                            kubectl rollout undo deployment/${K8S_DEPLOYMENT} \
                                -n ${K8S_NAMESPACE}
                        """
                        slackSend(
                            channel: env.SLACK_CHANNEL,
                            color: 'warning',
                            message: "🔄 **Automatic Rollback Initiated**"
                        )
                    } catch (err) {
                        echo "⚠️ Rollback failed: ${err.message}"
                    }
                }
            }
        }

        always {
            script {
                cleanWs()
            }
        }
    }
}
