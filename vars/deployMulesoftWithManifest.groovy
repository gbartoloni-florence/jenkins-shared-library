@GrabResolver(name='mulesoft', root='https://repository.mulesoft.org/releases')
@Grab(group='org.mule.tools.maven', module='mule-deployer', version='3.8.7')


import java.util.logging.Logger
import org.yaml.snakeyaml.Yaml
import org.mule.tools.model.anypoint.Cloudhub2Deployment
import org.mule.tools.deployment.DefaultDeployer;
import org.mule.tools.deployment.Deployer;
import it.clivet.cicd.sharedlibrary.utils.CredentialRetriever
import it.clivet.cicd.sharedlibrary.model.ApplicationDeploymentConfiguration


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
                        def applicationDefaultName = params.Application + "-" + environment
                        // sh "npm install -g anypoint-cli-v4"
                        // sh "anypoint-cli-v4 --version"

                        Yaml parser = new Yaml()
                        def manifest = (new File(workspace + "/manifests/" + environment + "/" + params.Application + "-" + environment + ".manifest.yaml")).text
                        Map configuration = parser.load(manifest)
                        echo "config = $configuration"


                        def creds = CredentialRetriever.getCredentials(configuration.secrets.collect{entry -> entry.value})
                        creds.each {
                            echo it.id + ": " + it.getClass()
                        }

                        def binding = [:]

                        creds.each { cred ->
                            def secret = configuration.secrets.find{it.value == cred.id}
                            println secret
                            binding[secret.key] = cred
                        }

                        println binding

                        def engine = new groovy.text.SimpleTemplateEngine()
                        def template = engine.createTemplate(manifest).make(binding)

                        Map configurationWithSecrets = parser.load(template.toString())
                        echo "config = $configurationWithSecrets"

                        def appConf = ApplicationDeploymentConfiguration.loadFromYaml(manifest)

                        Cloudhub2Deployment ch2deployment = new Cloudhub2Deployment()
                        ch2deployment.setArtifactId(appConf.artifactId)
                        ch2deployment.setGroupId(appConf.groupId)
                        ch2deployment.setVersion(appConf.version)

                        ch2deployment.setApplicationName(applicationDefaultName)
                        ch2deployment.setMuleVersion(appConf.runtimeVersion)
                        ch2deployment.setTarget(appConf.deploymentTargetId)
                        ch2deployment.setEnvironment(appConf.environment)
                        ch2deployment.setConnectedAppClientId(appConf.anypointClientId)
                        ch2deployment.setConnectedAppClientSecret(appConf.anypointClientSecret)

                        ch2deployment.setvCores("0.1")
                        ch2deployment.setProperties(appConf.properties)

                        echo ch2deployment.getApplicationName()
                        Logger log = Logger.getLogger('org.example.jobdsl')

                        Deployer deployer = new DefaultDeployer(ch2deployment, log);
                        deployer.deploy();
                    }
                }
            }
        }
    }
}
