// =============================================================================
// Registro automatico de la integracion SonarQube dentro de Jenkins
// Se ejecuta en cada arranque (Jenkins -> init.groovy.d)
//
// Registra el servidor SonarQube en Manage Jenkins > System > SonarQube
// servers con el nombre `sonarServer` y la URL interna de la red de compose:
// http://sonarqube:9000
//
// El token NUNCA viaja por el .env ni por este archivo: la credencial
// `sonarqube-token` (Secret text) se crea desde la interfaz de Jenkins
// (Manage Jenkins > Credentials > Global > Add Credentials) pegando el token
// generado en SonarQube (My Account > Security > Generate Token).
//
// Este script solo deja el servidor apuntando a esa credencial. Si el usuario
// edita el servidor desde la UI, la configuracion se re-normaliza en el
// siguiente arranque (idempotente).
// =============================================================================

import hudson.plugins.sonar.SonarGlobalConfiguration
import hudson.plugins.sonar.SonarInstallation

def log = { msg -> println "[init-sonarqube] ${msg}" }

def serverName = 'sonarServer'          // debe coincidir con withSonarQubeEnv()
def serverUrl = 'http://sonarqube:9000'  // nombre de servicio en devsecops-net
def credentialId = 'sonarqube-token'     // credencial creada desde la UI
def mojoVersion = '5.5.0.6356'          // sonar-maven-plugin usado en el pom.xml
def legacyName = 'sonarqube-local'       // nombre de una version anterior del script

// ---------------------------------------------------------------------------
// Servidor SonarQube en la configuracion global
//
// OJO con el orden de los argumentos del constructor de SonarInstallation
// (plugin `sonar`, no `sonarqube-scanner`):
//   (name, serverUrl, credentialsId, credentialsSecret, webhookSecretId,
//    mojoVersion, additionalProperties, additionalAnalysisProperties, triggers)
// Si se confunde el orden, Jenkins guarda la config corrida (credentialsId
// vacio, mojoVersion dentro de additionalProperties...) y el analisis falla.
// ---------------------------------------------------------------------------
def globalConfig = SonarGlobalConfiguration.get()
def current = (globalConfig.getInstallations() ?: []) as List

// Se descartan las instalaciones con el nombre antiguo y con el actual:
// siempre se recrea la del nombre canonico para que el mapeo sea correcto.
def kept = current.findAll { it.name != serverName && it.name != legacyName }

def installation = new SonarInstallation(
        serverName,      // name
        serverUrl,       // serverUrl
        credentialId,    // credentialsId
        null,            // credentialsSecret (obsoleto, se usa credentialsId)
        '',              // webhookSecretId
        mojoVersion,     // mojoVersion
        '',              // additionalProperties
        '',              // additionalAnalysisProperties
        null)            // triggers

kept << installation
// setInstallations es varargs: hay que desplegar la lista
globalConfig.setInstallations(*kept)
globalConfig.save()

log("Servidor SonarQube '${serverName}' -> ${serverUrl} (credencial '${credentialId}', mojo ${mojoVersion})")
log("Credencial '${credentialId}': debe existir en Manage Jenkins > Credentials > Global (Secret text)")
