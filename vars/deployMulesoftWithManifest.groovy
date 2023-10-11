import org.mule.tools.model.anypoint.Cloudhub2DeploymentSettings
import org.mule.tools.model.anypoint.Http
import org.mule.tools.model.anypoint.Inbound
import org.mule.tools.model.anypoint.Integration
import org.mule.tools.model.anypoint.ObjectStoreV2
import org.mule.tools.model.anypoint.Service
@GrabResolver(name='mulesoft', root='https://repository.mulesoft.org/releases')
@Grab(group='org.mule.tools.maven', module='mule-deployer', version='3.8.7')


import org.yaml.snakeyaml.Yaml
import org.mule.tools.model.anypoint.Cloudhub2Deployment
import org.mule.tools.deployment.DefaultDeployer;
import org.mule.tools.deployment.Deployer;
import it.clivet.cicd.sharedlibrary.utils.CredentialRetriever
import it.clivet.cicd.sharedlibrary.model.ApplicationCloudHub2DeploymentConfiguration
import it.clivet.cicd.sharedlibrary.utils.JenkinsLog

import static org.mule.tools.validation.DeploymentValidatorFactory.createDeploymentValidator;

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
                        // echo "config = $configuration"


                        def creds = CredentialRetriever.getCredentials(configuration.secrets.collect{entry -> entry.value})
                        // creds.each {
                        //    echo it.id + ": " + it.getClass()
                        // }

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
                        // echo "config = $configurationWithSecrets"

                        def appConf = ApplicationCloudHub2DeploymentConfiguration.loadFromYaml(template.toString())

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
                        ch2deployment.setConnectedAppGrantType("client_credentials")
                        ch2deployment.setBusinessGroupId(appConf.businessGroupId)
                        ch2deployment.setProvider("MC")
                        ch2deployment.setReplicas("1") // TODO
                        ch2deployment.setDeploymentTimeout(60000)

                        ch2deployment.setvCores("0.1") // TODO
                        ch2deployment.setProperties(appConf.properties)
                        ch2deployment.setPackaging("mule-application")

                        ch2deployment.setUri("https://" + appConf.host)

                        ch2deployment.setSkipDeploymentVerification(true)

                        ch2deployment.setSecureProperties(appConf.secureProperties)

                        def deploymentSettings = new Cloudhub2DeploymentSettings()
                        deploymentSettings.setGenerateDefaultPublicUrl(true) // TODO
                        deploymentSettings.setLastMileSecurity(false) // TODO
                        if (appConf.ingressPublicUrl != null) {
                            def inbound = new Inbound()
                            inbound.setPublicUrl(appConf.ingressPublicUrl)
                            def http = new Http()
                            http.setInbound(inbound)
                            deploymentSettings.setHttp(http)
                        }
                        ch2deployment.setDeploymentSettings(deploymentSettings)


                        def objectStoreV2 = new ObjectStoreV2()
                        objectStoreV2.setEnabled(true) // TODO
                        def service = new Service()
                        service.setObjectStoreV2(objectStoreV2)
                        def integration = new Integration()
                        integration.setServices(service)
                        ch2deployment.setIntegrations(integration)

                        createDeploymentValidator(ch2deployment).validateMuleVersionAgainstEnvironment()

                        Deployer deployer = new DefaultDeployer(ch2deployment, new JenkinsLog())
                        deployer.deploy()
                    }
                }
            }
        }
    }
}
