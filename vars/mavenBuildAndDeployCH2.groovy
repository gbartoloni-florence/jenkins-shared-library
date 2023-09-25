def call(Map input) {
  pipeline {
    agent any
    stages {
      stage('Package') {
        agent {
          docker {
            image 'maven:3.8.6-jdk-8'
            server("tcp://docker.for.mac.host.internal:2376")
          }
        }
        steps {
          script {
            sh 'mcn clean package'
          }
        }
      }
    }
  }
}
