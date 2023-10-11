@GrabResolver(name='mulesoft', root='https://repository.mulesoft.org/releases')
@Grab(group='org.mule.tools.maven', module='mule-deployer', version='3.8.7')


import org.yaml.snakeyaml.Yaml
import org.mule.tools.model.anypoint.Cloudhub2Deployment
import it.clivet.cicd.sharedlibrary.utils.CredentialRetriever


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
                        // sh "npm install -g anypoint-cli-v4"
                        // sh "anypoint-cli-v4 --version"

                        Yaml parser = new Yaml()
                        Map configuration = parser.load((new File(workspace + "/manifests/" + environment + "/" + params.Application + "-" + environment + ".manifest.yaml")).text)
                        echo "config = $configuration"

                        // def appConf = ApplicationDeploymentConfiguration.loadFromYaml(new File(workspace + "/manifests/" + environment + "/" + params.Application + "-" + environment + ".manifest.yaml"))

                        def creds = CredentialRetriever.getCredentials(configuration.secrets.collect{entry -> entry.value})
                        creds.each {
                            echo it.it
                        }

                        Cloudhub2Deployment ch2deployment = new Cloudhub2Deployment()
                        ch2deployment.setvCores("0.1")
                        ch2deployment.setEnvironment("Dev")
                        ch2deployment.setApplicationName(params.Application + "-" + environment)

                        echo ch2deployment.getApplicationName()
                    }
                }
            }
        }
    }
}
