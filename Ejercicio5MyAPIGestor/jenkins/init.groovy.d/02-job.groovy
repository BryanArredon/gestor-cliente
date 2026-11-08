// =============================================================================
// Crea (o actualiza) los 3 pipelines requeridos por la Practica 3:
//   1) frontend-cliente  -> Ejercicio5Cliente/Jenkinsfile
//   2) backend-gestor    -> Ejercicio5MyAPIGestor/Jenkinsfile
//   3) database-ci       -> database/Jenkinsfile
//   +  api-gestor        -> Ejercicio5MyAPIGestor/Jenkinsfile (compatibilidad)
// =============================================================================

import com.cloudbees.jenkins.GitHubPushTrigger
import hudson.plugins.git.BranchSpec
import hudson.plugins.git.GitSCM
import hudson.plugins.git.UserRemoteConfig
import hudson.triggers.SCMTrigger
import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob

def jenkins = Jenkins.get()
def repoUrl = 'https://github.com/BryanArredon/gestor-cliente'
def branch = '*/main'

def jobsConfig = [
    [
        name: 'frontend-cliente',
        desc: 'Pipeline 1 Frontend (Ejercicio5Cliente): build, unit tests, JaCoCo, OWASP, SonarQube, Semgrep, package',
        script: 'Ejercicio5Cliente/Jenkinsfile'
    ],
    [
        name: 'backend-gestor',
        desc: 'Pipeline 2 Backend (Ejercicio5MyAPIGestor): build, unit tests, JaCoCo, OWASP, SonarQube, package',
        script: 'Ejercicio5MyAPIGestor/Jenkinsfile'
    ],
    [
        name: 'database-ci',
        desc: 'Pipeline 3 Base de Datos (Database CI): linting SQL, SAST, migracion, integridad referencial y reporte',
        script: 'database/Jenkinsfile'
    ],
    [
        name: 'api-gestor',
        desc: 'Pipeline DevSecOps Ejercicio5MyAPIGestor (alias)',
        script: 'Ejercicio5MyAPIGestor/Jenkinsfile'
    ]
]

jobsConfig.each { cfg ->
    def job = jenkins.getItem(cfg.name)
    if (job == null) {
        job = jenkins.createProject(WorkflowJob.class, cfg.name)
    }
    job.setDescription(cfg.desc)

    def scm = new GitSCM(
            [new UserRemoteConfig(repoUrl, null, null, null)],
            [new BranchSpec(branch)],
            null,
            null,
            []
    )

    job.setDefinition(new CpsScmFlowDefinition(scm, cfg.script))

    def triggers = []
    triggers << new GitHubPushTrigger()
    triggers << new SCMTrigger('H/2 * * * *')
    job.setTriggers(triggers)

    job.save()
    println "[init-job] Job '${cfg.name}' configurado -> Script: ${cfg.script}"
}
