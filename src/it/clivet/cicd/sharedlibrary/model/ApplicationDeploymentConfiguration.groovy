package it.clivet.cicd.sharedlibrary.model

import org.yaml.snakeyaml.Yaml

class ApplicationDeploymentConfiguration {

    String artifactId, groupId, version
    String deploymentType
    String anypointClientId, anypointClientSecret, runtimeVersion, applicationName, environment, deploymentTargetId, organization, host, businessGroupId
    Map secrets, properties

    public static ApplicationDeploymentConfiguration loadFromYaml(File file) {
        Yaml parser = new Yaml()
        Map configuration = parser.load(file.text)

        return loadFromMap(configuration)
    }

    public static ApplicationDeploymentConfiguration loadFromYaml(String yaml) {
        Yaml parser = new Yaml()
        Map configuration = parser.load(yaml)

        return loadFromMap(configuration)
    }


    public static ApplicationDeploymentConfiguration loadFromMap(Map configuration) {
        def conf = new ApplicationDeploymentConfiguration()
        conf.artifactId = configuration.artifact.artifactId
        conf.groupId = configuration.artifact.groupId
        conf.version = configuration.artifact.version
        conf.deploymentType = configuration.deploymentType
        conf.anypointClientId = configuration.anypoint.clientId
        conf.anypointClientSecret = configuration.anypoint.clientSecret
        conf.runtimeVersion = configuration.anypoint.runtimeVersion
        conf.applicationName = configuration.anypoint.name
        conf.environment = configuration.anypoint.environment
        conf.deploymentTargetId = configuration.anypoint.deploymentTargetId
        conf.organization = configuration.anypoint.organization
        conf.host = configuration.anypoint.host
        conf.businessGroupId = configuration.anypoint.businessGroupId

        conf.secrets = configuration.secrets
        conf.properties = configuration.properties

        return conf
    }

}
