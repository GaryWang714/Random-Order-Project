pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'
        AWS_ACCOUNT_ID = '529745008516'
        ECS_CLUSTER = 'random-order-cluster'
        ORDERS_REPO = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/random-order-orders"
        FRONTEND_REPO = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/random-order-frontend"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Login to ECR') {
            steps {
                sh """
                    aws ecr get-login-password --region ${AWS_REGION} | \
                    docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com
                """
            }
        }

        stage('Build & Push Orders') {
            steps {
                sh """
                    docker build --platform=linux/amd64 -t ${ORDERS_REPO}:latest orders/
                    docker push ${ORDERS_REPO}:latest
                """
            }
        }

        stage('Build & Push Frontend') {
            steps {
                sh """
                    docker build --platform=linux/amd64 -t ${FRONTEND_REPO}:latest frontend/
                    docker push ${FRONTEND_REPO}:latest
                """
            }
        }

        stage('Deploy Orders') {
            steps {
                sh """
                    aws ecs update-service --cluster ${ECS_CLUSTER} \
                        --service random-order-orders \
                        --force-new-deployment \
                        --region ${AWS_REGION}
                """
            }
        }

        stage('Deploy Frontend') {
            steps {
                sh """
                    aws ecs update-service --cluster ${ECS_CLUSTER} \
                        --service random-order-frontend \
                        --force-new-deployment \
                        --region ${AWS_REGION}
                """
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
