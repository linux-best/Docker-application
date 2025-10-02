pipeline {
    agent any

    environment {
        CONTAINER_ID = "1"
        REPO_PROJECT = 'https://github.com/linux-best/Docker-application'
    }

    stages {
        stage("stage => Checkout-SCM") {
            steps {
                git branch: 'main', url: ${env.REPO_PROJECT}
            }
        }
        stage("stage => Build_Image") {
            steps {
                sh "sudo docker image ls"
            }
        }
        stage("stage => Test_Container") {
            steps {
                sh """
                sudo docker ps -a
                sudo docker ps
                """
            }
        }
        stage("stage => Deploy_Application") {
            steps {
                echo "Deploying .........."   
            }
        }
    }
    post {
        success {echo "Done !"}
        failure {echo "Fuck !!"}
    }
}
