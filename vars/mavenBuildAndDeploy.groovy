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
            withCredentials([file(credentialsId: input['MAVEN_SETTINGS'], variable: 'MAVEN_SETTINGS_XML')]) {
              sh 'mvn -B -s $MAVEN_SETTINGS_XML clean deploy'
            }
          }
        }
      }
    }
  }
}
