# SDK Atos

# Prerequisites

File **docker-compose.env** containing all properties.

Variable COMPOSE_PROJECT_NAME is really important, as it will be used to name 
the docker volumes.

# Extend

Here you can customize the Alfresco stack by create a **docker-compose.extended.yml** file.

See https://docs.docker.com/compose/extends/ to understand how to extend a Compose configuration.

# LDAP

**users.ldif** contains the groups and users used by the project when starting Active Directory

It can be generated with Atos SDK by using script **ldap/gen_ldif.sh**