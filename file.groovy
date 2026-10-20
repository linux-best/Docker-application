pipeline {
    agent any
    
    environment {
        CMD = sh(script:"date '+%F %T'",returnStdout: true).trim()
        VAR_LOG_TXT = "file_output_${env.CMD}.txt"
    }
    
    stages {
        stage('Build') {
            steps {
                echo "Building in File: ${env.VAR_LOG_TXT} ....!"
                sh """
                echo "doing build stuff.." > ${env.VAR_LOG_TXT}
                """
            }
        }
        stage('Test') {
            steps {
                echo "Testing..."
                sh """
                echo "doing test stuff.." >> ${env.VAR_LOG_TXT}
                """
            }
        }
        stage('Deliver') {
            steps {
                echo "Delivering Application ....!"
                sh """
                echo "doing delivery stuff.." >> ${env.VAR_LOG_TXT}
                """
            }
        }
    post {
        success {
            script {
                emailext(
                    attachmentsPattern: '*.txt',
                    subject: "Build ${env.BUILD_NUMBER} of ${env.JOB_NAME} was successful",
                    body: """
                    Check the details at ${env.BUILD_URL}""",
                    to: "amirmahdifhp@gmail.com"
                )
            }
        }
        failure {
            script {
                emailext(
                    attachmentsPattern: '*.txt',
                    subject: "Build ${env.BUILD_NUMBER} of ${env.JOB_NAME} was failed",
                    body: """
                    Check the details at ${env.BUILD_URL}""",
                    to: "amirmahdifhp@gmail.com"
                )
            }
        }
    }
}pipeline {
    agent any
    
    environment {
        CMD = sh(script:"date '+%F %T'",returnStdout: true).trim()
        VAR_LOG_TXT = "file_output_${env.CMD}.txt"
    }
    
    stages {
        stage('Build') {
            steps {
                echo "Building in File: ${env.VAR_LOG_TXT} ....!"
                sh """
                echo "doing build stuff.." > ${env.VAR_LOG_TXT}
                """
            }
        }
        stage('Test') {
            steps {
                echo "Testing..."
                sh """
                echo "doing test stuff.." >> ${env.VAR_LOG_TXT}
                """
            }
        }
        stage('Deliver') {
            steps {
                echo "Delivering Application ....!"
                sh """
                echo "doing delivery stuff.." >> ${env.VAR_LOG_TXT}
                """
            }
        }
        stage('LOG') {
            steps {
                sh """curl \
                -u javande:am12243648607080 \
                -i -H 'Accept:application/json' \
                --output log_output.txt \
                ${JENKINS_URL}/job/${JOB_NAME}/lastBuild/consoleText
                """
            }
        }
    }
    post {
        //always {
        //    steps {
        //        sh """curl \
        //        -u javande:am12243648607080 \
        //        -i -H 'Accept:application/json' \
        //        --output log_output.txt \
        //        ${JENKINS_URL}/job/${JOB_NAME}/lastBuild/consoleText
        //        """
        //    }            
        //}
        success {
            script {
                emailext(
                    attachmentsPattern: '*.txt',
                    subject: "Build ${env.BUILD_NUMBER} of ${env.JOB_NAME} was successful",
                    body: """
                    Check the details at ${env.BUILD_URL}""",
                    to: "amirmahdifhp@gmail.com"
                )
            }
        }
        failure {
            script {
                emailext(
                    attachmentsPattern: '*.txt',
                    subject: "Build ${env.BUILD_NUMBER} of ${env.JOB_NAME} was failed",
                    body: """
                    Check the details at ${env.BUILD_URL}""",
                    to: "amirmahdifhp@gmail.com"
                )
            }
        }
    }
}
