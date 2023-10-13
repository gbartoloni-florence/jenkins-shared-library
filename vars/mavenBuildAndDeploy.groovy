def call(Map input) {
  pipeline {
    agent {
      docker {
        image 'maven:3.8.6-jdk-8'
        args '-v $HOME/.m2:/opt/maven/.m2 --env MAVEN_CONFIG=/opt/maven/.m2'
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
    post {
        // Clean after build
        always {
            cleanWs(cleanWhenNotBuilt: false,
                    deleteDirs: true,
                    disableDeferredWipeout: true,
                    notFailBuild: true,
                    patterns: [[pattern: '.gitignore', type: 'INCLUDE'],
                               [pattern: '.propsfile', type: 'EXCLUDE']])
        }
    }
  }
}
