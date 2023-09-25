def call(Map input) {
  pipeline {
    agent {
      docker {
        image 'maven:3.8.6-jdk-8'
      }
    }
    stages {
      stage('Checkout Repository') {
        steps {
          script {
            sh 'mcn clean package'
          }
        }
      }
    }
  }
}
