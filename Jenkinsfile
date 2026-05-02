// pipeline {
//     agent any
//
//     environment {
//         AWS_REGION = 'us-east-1'
//         AWS_ACCOUNT_ID = '529745008516'
//         ECS_CLUSTER = 'random-order-cluster'
//         ORDERS_REPO = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/random-order-orders"
//         FRONTEND_REPO = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/random-order-frontend"
//     }
//
//     stages {
//         stage('Checkout') {
//             steps {
//                 checkout scm
//             }
//         }
//
//         stage('Login to ECR') {
//             steps {
//                 sh """
//                     aws ecr get-login-password --region ${AWS_REGION} | \
//                     docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com
//                 """
//             }
//         }
//
//         stage('Build & Push Orders') {
//             steps {
//                 sh """
//                     docker build --platform=linux/amd64 -t ${ORDERS_REPO}:latest orders/
//                     docker push ${ORDERS_REPO}:latest
//                 """
//             }
//         }
//
//         stage('Build & Push Frontend') {
//             steps {
//                 sh """
//                     docker build --platform=linux/amd64 -t ${FRONTEND_REPO}:latest frontend/
//                     docker push ${FRONTEND_REPO}:latest
//                 """
//             }
//         }
//
//         stage('Deploy Orders') {
//             steps {
//                 sh """
//                     aws ecs update-service --cluster ${ECS_CLUSTER} \
//                         --service random-order-orders \
//                         --force-new-deployment \
//                         --region ${AWS_REGION}
//                 """
//             }
//         }
//
//         stage('Deploy Frontend') {
//             steps {
//                 sh """
//                     aws ecs update-service --cluster ${ECS_CLUSTER} \
//                         --service random-order-frontend \
//                         --force-new-deployment \
//                         --region ${AWS_REGION}
//                 """
//             }
//         }
//     }
//
//     post {
//         success {
//             echo 'Pipeline completed successfully!'
//         }
//         failure {
//             echo 'Pipeline failed!'
//         }
//     }
// }

pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'
        AWS_ACCOUNT_ID = '529745008516'
        EKS_CLUSTER = 'random-order-eks'
        ORDERS_REPO = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/random-order-orders"
        PAYMENTS_REPO = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/random-order-payments"
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

        stage('Build & Push Payments') {
            steps {
                sh """
                    docker build --platform=linux/amd64 -t ${PAYMENTS_REPO}:latest payments/
                    docker push ${PAYMENTS_REPO}:latest
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

        stage('Install kubectl') {
            steps {
                sh '''
                    if ! command -v kubectl &> /dev/null; then
                        curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
                        chmod +x kubectl
                        mkdir -p /var/lib/jenkins/bin
                        mv kubectl /var/lib/jenkins/bin/kubectl
                    fi
                '''
            }
        }

        stage('Configure kubectl') {
            steps {
                sh "aws eks update-kubeconfig --name ${EKS_CLUSTER} --region ${AWS_REGION}"
            }
        }

        stage('Deploy to EKS') {
            steps {
                sh '''
                    export PATH=$PATH:/var/lib/jenkins/bin

                    # Get RDS endpoint
                    RDS_ENDPOINT=$(aws rds describe-db-instances \
                        --db-instance-identifier random-order-postgres \
                        --query 'DBInstances[0].Endpoint.Address' \
                        --output text)

                    # Deploy Kafka
                    kubectl apply -f k8s/kafka.yaml

                    # Deploy Orders
                    sed -e "s|\\${ORDERS_IMAGE}|$ORDERS_REPO:latest|g" \
                        -e "s|\\${RDS_ENDPOINT}|$RDS_ENDPOINT|g" \
                        k8s/orders.yaml | kubectl apply -f -

                    # Deploy Payments
                    sed "s|\\${PAYMENTS_IMAGE}|$PAYMENTS_REPO:latest|g" \
                        k8s/payments.yaml | kubectl apply -f -

                    # Wait for Orders LoadBalancer then deploy Frontend
                    kubectl rollout status deployment/orders --timeout=300s
                    ORDERS_LB=$(kubectl get service orders -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')
                    sed -e "s|\\${FRONTEND_IMAGE}|$FRONTEND_REPO:latest|g" \
                        -e "s|\\${ORDERS_URL}|http://$ORDERS_LB:8080|g" \
                        k8s/frontend.yaml | kubectl apply -f -
                '''
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