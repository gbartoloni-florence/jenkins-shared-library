def call(Map input) {
  pipeline {
    agent {
      docker {
        image 'maven:3.8.7-ibmjava-8'
      }
    }
    stages {
      stage('Package') {
        steps {
          script {
            String mavenCommand = 'mvn -B -s $MAVEN_SETTINGS_XML clean deploy'
            if (input['MAVEN_PROFILES'] != null) {
              mavenCommand = mavenCommand + ' -P ' + input['MAVEN_PROFILES']
            }
            withCredentials([file(credentialsId: input['MAVEN_SETTINGS'], variable: 'MAVEN_SETTINGS_XML')]) {
              sh mavenCommand
            }
          }
        }
      }
    }
  }
}
