pipeline {
    agent any
       tools {
           maven 'maven'
           jdk 'openjdk-11'
       }    
    environment {
        branch = 'master'
        scmUrl = 'https://dev-0707@github.com/dev-0707/order-service.git'
        serverPort = '8080'
        developmentServer = 'localhost'
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
                    sh "mvn sonar:sonar -Dsonar.login=7502b1568b23953461c7d245a714163125ac713b"
                }
            }
        }
        stage("Quality gate") {
            steps {
                waitForQualityGate abortPipeline: true
            }
        }        
/*
        stage('deploy development'){
            steps {
                deploy(developmentServer, serverPort)
            }
        }

        stage('deploy staging'){
            steps {
                deploy(stagingServer, serverPort)
            }
        }

        stage('deploy production'){
            steps {
                deploy(productionServer, serverPort)
            }
        }*/
    }
/*    post {
        failure {
            mail to: 'team@example.com', subject: 'Pipeline failed', body: "${env.BUILD_URL}"
        }
    }*/
}