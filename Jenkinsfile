pipeline {
    agent any

    environment {
        FAILURE_STAGE = 'None'
    }

    tools {
        mvn 'maven'
    }

    stages {
        stage('Testing') {
            script {FAILURE_STAGE = 'Testing'}
            steps {
                sh 'mvn clean test'
            }
        }
        stage('Build') {
            script {FAILURE_STAGE = 'Build'}
            steps {
                sh 'mvn clean package'
            }
        }
    }
    post {
        failure {
            script {
                if (FAILURE_STAGE == 'Testing') {
                    mail to: "${params.TESTER_EMAIL}",
                         subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                         body: "The Test stage failed. Please check the logs at ${env.BUILD_URL}"
                } else if (FAILURE_STAGE == 'Build') {
                    mail to: "${params.BUILDER_EMAIL}",
                         subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                         body: "The Build/Package stage failed. Please check the logs at ${env.BUILD_URL}"
                }
        }
        success {
            echo 'Build and Test were successful!'
        }
    }
}
