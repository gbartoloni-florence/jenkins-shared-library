def call(Map input) {
  pipeline {
    stages {
      stage('Checkout Repository') {
        steps {
          shell('echo Hello World!')
        }
      }
    }
  }
}
