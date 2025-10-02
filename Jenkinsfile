pipeline {
    agent any

    enviroment {
        CONTAINER_ID = "1"
        REPO_PROJECT = "http"
    }
    stages {
        stage("stage => Checkout-SCM") {
            steps {
                git branch: 'main', url: 'https://github.com/linux-best/Docker-application'
            }
        }
        stage("stage => Build_Image") {
            steps {
                sh "sudo docker image ls"
            }
        }
        stage("stage => Test_Container") {
            steps {
                """
                sudo docker ps -a
                sudo docker ps
                """
            }
        }
        stage("stage => Deploy_Application") {
            echo "Deploying .........."
        }
    }
    post {
        success {echo "Done !"}
        failure {echo "Fuck !!"}
    }
}
