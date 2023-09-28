import groovy.json.JsonSlurper


def call(String environment) {
  pipeline {
    agent {
      docker {
        image 'node:18.14.0-alpine3.17'
        reuseNode true
      }
    }
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
            sh "npm install -g anypoint-cli-v4"
            sh "anypoint-cli-v4 --version"

            def jsonSlurper = new JsonSlurper()
            def config = jsonSlurper.parse(new File(workspace + "/manifests/" + environment + "/" + params.Application + ".manifest.json"))
            echo "config = $config"
          }
        }
      }
    }
  }
}
