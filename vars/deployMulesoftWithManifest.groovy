def call(String environment) {
  pipeline {
    agent any
    stages {
      stage('Package') {
        steps {
          dir('manifests') {
            git(
              url: "https://github.com/gbartoloni-florence/mulesoft-manifests.git",
              branch: "main",
              changelog: true,
              poll: false
            )
          }
          script {
            echo params.Application
            echo environment
          }
        }
      }
    }
  }
}
