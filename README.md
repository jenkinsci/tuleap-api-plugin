# tuleap-api-plugin

This jenkins plugin provides a client for the Tuleap API. It is the result of the extraction of the client and server configuration of
[tuleap-gitbranch-source](https://github.com/jenkinsci/tuleap-git-branch-source-plugin/) for separation concerns and reuse in future Jenkins
plugins for Tuleap.

Works with any recent version of Jenkins >= 2.150.3 (latest LTS preferred).

## How to use

* Install the plugin
* In Jenkins global configuration you should reference your Tuleap instance (There is a limitation of 1 Tuleap server per Jenkins instance)

## Report issues

Issues must be reported in [Request tracker of the Tuleap project](https://tuleap.net/plugins/tracker/?report=1136) under the category "Jenkins Tuleap API plugin".

# Development

## On jenkins, connect to Tuleap

Configure Jenkins to accept a tuleap dev environment certificate

    echo -n | openssl s_client -connect tuleap-web.tuleap-aio-dev.docker:443 |    sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p'  >> /usr/local/share/ca-certificates/tuleap-web.tuleap-aio-dev.docker.crt
    keytool -keystore $JAVA_HOME/jre/lib/security/cacerts   -import -trustcacerts -storepass changeit -noprompt -alias tuleap-web-dev -file /usr/local/share/ca-certificates/tuleap-web.tuleap-aio-dev.docker.crt
    update-ca-certificates --fresh

## Build

### You have a local java / maven env

Tested with OpenJDK 8

    $> mvn clean install
    $> cp target/tuleap-branch-source.hpi onto jenkins

## Authors

* GOYOT Martin
* ROBINSON Clarck
