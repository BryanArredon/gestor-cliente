// TEMPORAL: sonda para comprobar que steps/credenciales existen en este Jenkins
import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob

def jenkins = Jenkins.get()
def name = 'probe-sonar-step'
def job = jenkins.getItem(name)
if (job == null) {
    job = jenkins.createProject(WorkflowJob.class, name)
}

def script = '''
pipeline {
    agent any
    stages {
        stage('probe') {
            steps {
                script {
                    try {
                        withSonarQubeEnv('sonarServer') {
                            echo "STEP_OK withSonarQubeEnv"
                            sh 'env | grep -iE "sonar" | sed -e "s/=.*/=<set>/" || true'
                        }
                    } catch (Throwable t) {
                        echo "STEP_ERROR: " + t.getClass().getName() + " :: " + t.getMessage()
                    }
                    try {
                        echo "CLASS_PRESENT: " + Class.forName('org.sonarsource.scanner.jenkins.pipeline.WithSonarQubeEnv')
                    } catch (Throwable t) {
                        echo "CLASS_ABSENT: " + t.getClass().getName()
                    }
                    def cfg = hudson.plugins.sonar.SonarGlobalConfiguration.get()
                    (cfg.getInstallations() ?: []).each { i ->
                        echo "INSTALLATION name=" + i.name + " url=" + i.serverUrl +
                             " credentialsId='" + i.credentialsId + "' mojoVersion='" + i.mojoVersion + "'" +
                             " additionalProperties='" + i.additionalProperties + "'" +
                             " webhookSecretId='" + i.webhookSecretId + "'"
                    }
                    def store = com.cloudbees.plugins.credentials.SystemCredentialsProvider.getInstance()
                            .getStore(com.cloudbees.plugins.credentials.domains.Domain.global())
                    store.getCredentials(com.cloudbees.plugins.credentials.domains.Domain.global()).each { c ->
                        echo "CREDENTIAL id=" + c.id + " type=" + c.getClass().getSimpleName()
                    }
                }
            }
        }
    }
}
'''

job.setDefinition(new CpsFlowDefinition(script, true))
job.save()
def f = job.scheduleBuild2(0)
println "[probe] Build programado: " + f
