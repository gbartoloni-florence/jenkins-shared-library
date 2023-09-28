def call(String environment) {
  pipeline {
    agent any
    stages {
      stage('Package') {
        agent {
          docker {
            image 'maven:3.8.6-jdk-8'
          }
        }
        steps {
          script {
            echo params.Application
            echo environment
          }
        }
      }
    }
  }
}
