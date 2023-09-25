def call(Map input) {
  pipeline {
    agent any
    stages {
      stage('Checkout Repository') {
        steps {
          echo 'Hello World'
        }
      }
    }
  }
}
