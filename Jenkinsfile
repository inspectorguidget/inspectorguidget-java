def githubStatusCheck(String state, String description){
    def commitHash = checkout(scm).GIT_COMMIT
    githubNotify account: 'inspectorguidget',sha: "${commitHash}", status: state, description: description, credentialsId: 'github-token', repo: 'inspectorguidget-java'
}

pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'jdk11'
    }

    stages {
        stage ('Tools Info') {
            steps {
                sh '''
                    java -version
                    mvn -v
                '''
            }
        }

        stage ('Git') {
            steps {
                //going to build on the branch master
                git branch: 'master', url: "https://github.com/inspectorguidget/inspectorguidget-java"
            }
        }

        stage ('Artifactory configuration') {
            steps {
                rtServer (
                    id: "InriaArtifactoryServer",
                    url: 'http://maven.irisa.fr/artifactory',
                    credentialsId: 'credRepoInria'                                  // add credentials in Jenkins
                )

                rtMavenDeployer (
                    id: "MAVEN_DEPLOYER",
                    serverId: "InriaArtifactoryServer",
                    releaseRepo: "malai-public-release",
                    snapshotRepo: "malai-public-snapshot"
                )
            }
        }

        stage ('Build') {
            steps {
                rtMavenRun (
                    pom: 'pom.xml',
                    goals: 'clean install',
                    deployerId: 'MAVEN_DEPLOYER'
                )
            }
        }

        stage ('Publish build info') {
            steps {
                rtPublishBuildInfo (
                    serverId: "InriaArtifactoryServer"
                )
            }
        }

    }
    post{
        success {
            githubStatusCheck("SUCCESS", "Build succeeded");
        }
        failure {
            githubStatusCheck("FAILURE", "Build failed");
        }
    }
}