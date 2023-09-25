def call(Map input) {
  pipeline {
    stages {
      stage('Checkout Repository') {
        steps {
          echo 'Hello World'
        }
      }
    }
  }
}
