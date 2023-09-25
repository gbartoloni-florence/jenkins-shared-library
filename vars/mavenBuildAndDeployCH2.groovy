def call(Map input) {
  pipeline {
    agent any
    stages {
      stage('Package') {
        agent {
          docker.withServer("tcp://docker.for.mac.host.internal:2376") {
            image 'maven:3.8.6-jdk-8'
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
