def call(Map input) {
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
            sh 'mvn -B clean deploy'
          }
        }
      }
    }
  }
}
