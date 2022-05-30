# Module Alfresco

Ce projet permet de compiler, tester et déployer des modules Alfresco (AMP).

Prérequis: utilisation du SDK Atos : https://gitlab.mtpl.bs.fr.atos.net/alfresco/commons/sdk-atos

Structure du projet:
- build : configurations d'intégration continue
- compose : fichiers d'environnement compose/docker-compose.env.
  Celui-ci contient les versions des composants (acs, share, solr...etc.) à utiliser.
  Ceux-ci doivent être en adéquation avec ceux renseignés dans le fichier pom.xml à la racine du projet,
  à savoir les variables <alfresco.platform.version> et <alfresco.share.version>
- docker : à ne pas modifier. Contient les instructions de récupération des dépendances de runtime qui
  seront injectés dans l'image docker générée.
- integration-tests : tests d'intégrations...
- platform: un AMP platform.
- share: un AMP share.

## Version d'Alfresco

La version d'Alfresco est renseignée dans les fichiers:
- compose/docker-compose.env
- pom.xml
Bien sur, la version doit correspondre

## Livraison

Le fichier **pom.xml** ne contient aucune information de distribution.
C'est à dire que la commande **mvn deploy** ne pourra pas être utilisée.
Les releases sont faites simplement en déposant le(s) livrable(s) AMP dans le 
dépôt Nexus, c'est un dépôt commun avec les artefacts ADF.

Le déploiement est fait dans le pipeline d'intégration continue **build/ci/Jenkinsfile.groovy**

```
stage('Publish maven artifacts on Nexus') {
    when { expression { currentBuild.result == null } }
    steps {
       withMaven(maven: 'maven-3.3.9') {
            sh "mvn install -DskipTests -DskipITs -Pamp ${OPT_VERSION}"
        }
        withCredentials([usernamePassword(credentialsId: 'ged_nexus', usernameVariable: 'LOGIN_USER', passwordVariable: 'LOGIN_PASSWORD')]) {
            sh "curl -v -u $LOGIN_USER:$LOGIN_PASSWORD --upload-file platform/target/platform-1.0-SNAPSHOT.amp ${NEXUS_REPO}/${ARTIFACT_NAME}"
        }
    }
}
```

