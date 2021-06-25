def call(Map pipelineParams) {
pipeline {
    agent any
       tools {
           maven 'maven'
           jdk 'openjdk-11'
       }    
    environment {
        branch = "${pipelineParams.branch}"
        scmUrl = "${pipelineParams.scmUrl}"
        serverPort = "${pipelineParams.serverPort}"
        developmentServer = "${pipelineParams.developmentServer}"
        //stagingServer = 'staging-myproject.mycompany.com'
        //productionServer = 'production-myproject.mycompany.com'
    }
    stages {
        stage('checkout git') {
            steps {
                git branch: branch, credentialsId: 'GitCredentials', url: scmUrl
            }
        }

        stage('build') {
            steps {
                sh 'mvn clean package -DskipTests=true'
            }
        }
        stage ('test') {
            steps {
                parallel (
                    "unit tests": { sh 'mvn test' },
                    //"integration tests": { sh 'mvn integration-test' }
                )
            }
        }
        stage('SonarQube analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh "mvn sonar:sonar"
                }
            }
        }
        
        stage("Quality Gate") {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }
}
}