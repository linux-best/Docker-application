pipeline {
    agent any

    environment {
        CONTAINER_ID = "1"
        REPO_PROJECT = 'https://github.com/linux-best/Docker-application'
    }

    stages {
        stage("Process => Checkout-SCM") {
            steps {
                git branch: 'main', url: "${env.REPO_PROJECT}"
            }
        }
        stage("Process => Build_Image") {
            steps {
                sh "sudo docker image ls"
            }
        }
        stage("Process => Test_Container") {
            steps {
                sh """
                sudo docker ps -a
                sudo docker ps
                """
            }
        }
        stage("Process => Deploy_Application") {
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
