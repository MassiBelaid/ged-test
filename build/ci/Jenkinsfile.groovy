pipeline {
    agent {
        docker {
            image 'sdk:latest'
            args '-v /docker/volume/jenkins-slave/maven:/data/maven/ ' +
                 '-v /docker/volume/jenkins-slave/dind/bin:/slave-bin ' +
                 '--net=host'
        }
    }
    environment {
        OPT_VERSION="-Dmodule.branch=${GIT_BRANCH} -Dmodule.commit=${GIT_COMMIT}"
    }
    triggers {
        pollSCM('H/5 * * * *')
    }
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5', daysToKeepStr: '30'))
    }
    parameters {
        string(name: 'STARTUP_TIMEOUT_IN_SECONDS', defaultValue: '500', description: 'Défini la durée maximale (exprimée en secondes) d\'attente du démarrage du runner Alfresco.')
        string(name: 'SERVICES', defaultValue: 'acs', description: 'Liste des services à démarrer pour les tests')
        booleanParam(name: 'DOCKER', defaultValue: false, description: 'Tagger et pusher les images docker ?')
        string(name: 'DOCKER_TAG', defaultValue: 'master', description: 'Tag à utilsier pour les images docker ("master" si vide)')
        string(name: 'DOCKER_SERVICE', defaultValue: '', description: 'Liste des images docker à publier (toutes les images :development si vide)')
    }
    stages {
        stage('Clean, checkout project and SDK') {
            steps{
                deleteDir()
                checkout(changelog: false, scm: scm)
                sh "prj set PROJECT_ROOT_PATH $WORKSPACE"
            }
        }
        stage ('Build and start stack') {
            steps {
                sh "sdk clean package ${OPT_VERSION}"
                sh "sdk -noport start ${params.SERVICES}"
            }
        }
        stage('Tests') {
            steps {
                script {
                    try {
                        sh "sdk test"
                    } catch (e) {
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
            post{
                always {
                    sh "sdk stop"
                }
            }
        }
        stage('Sonar') {
            when { expression { currentBuild.result == null } }
            steps {
                sh "sdk extract-coverage"
                sh "sdk sonar"
            }
        }
        stage('Publish docker image on Nexus') {
            when {
                expression { currentBuild.result == null }
                expression { params.DOCKER }
            }
            steps {
                sh "sdk push_image -t ${params.DOCKER_TAG} ${params.DOCKER_SERVICE}"
            }
        }
        stage('Deploy artifacts') {
            when { expression { currentBuild.result == null } }
            steps {
                sh "sdk deploy -DskipTests -DskipITs -Pamp ${OPT_VERSION}"
            }
        }
    }
    post {
        always {
            sh "sdk logs > alfresco_all.log"
            sh "sdk stop purge"
            step([$class: 'JUnitResultArchiver', testResults: '**/target/*-reports/TEST-*.xml', allowEmptyResults: true])
            archiveArtifacts allowEmptyArchive: true, artifacts: '*.log', caseSensitive: false, defaultExcludes: false
            step([$class: 'Mailer', recipients: [emailextrecipients([[$class: 'CulpritsRecipientProvider'], [$class: 'RequesterRecipientProvider']])].join(' ')])
        }
    }
}
