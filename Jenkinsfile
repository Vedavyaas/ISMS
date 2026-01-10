pipeline {
    agent any

    environment {
        FAILURE_STAGE = 'None'
    }

    tools {
        maven 'maven'
    }

    stages {
        stage('Testing') {
            steps {
                script { FAILURE_STAGE = 'Testing' }
                sh 'mvn clean test'
            }
        }
        stage('Build') {
            steps {
                script { FAILURE_STAGE = 'Build' }
                sh 'mvn clean package'
            }
        }
    }
    post {
        failure {
            script {
                if (FAILURE_STAGE == 'Testing') {
                    mail to: "${env.TESTER_EMAIL}",
                         subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                         body: "The Test stage failed. Please check the logs at ${env.BUILD_URL}"
                } else if (FAILURE_STAGE == 'Build') {
                    mail to: "${env.BUILDER_EMAIL}",
                         subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                         body: "The Build/Package stage failed. Please check the logs at ${env.BUILD_URL}"
                }
            }
        }

        success {
            echo 'Build and Test were successful!'
        }
    }
}