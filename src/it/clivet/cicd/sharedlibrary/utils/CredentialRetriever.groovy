package it.clivet.cicd.sharedlibrary.utils
import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*
import com.cloudbees.plugins.credentials.Credentials
import com.cloudbees.plugins.credentials.CredentialsProvider

class CredentialRetriever {

    // https://scriptcrunch.com/groovy-script-retrieve-jenkins-credentials/
    
    public static List<com.cloudbees.plugins.credentials.Credentials> getCredentials(List<String> secretIds) {
        def list = []

        def jenkinsCredentials = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
                com.cloudbees.plugins.credentials.Credentials.class,
                Jenkins.instance,
                null,
                null
        );
        for (creds in jenkinsCredentials) {
            println(creds.id)
            if (creds.id in secretIds) {
                list << creds
            }
        }
        return list;
    }
}
