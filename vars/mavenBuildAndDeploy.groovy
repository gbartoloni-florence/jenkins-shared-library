def call(Map input) {
  pipeline {
    agent any
    environment {
      SETTINGS_XML = credentials(input['MAVEN_SETTINGS']) //TODO: Create new dedicated credentials for AXPO
    }
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
