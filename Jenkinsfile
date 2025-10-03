pipeline {
    agent any

    environment { 
   	    DOCKER_AUTH = credentials('Dockerpass') 
    }

    stages {
        stage("stage => Checkout-SCM") {
            steps {
                git branch: 'main', url: 'https://github.com/linux-best/Docker-application'
            }
        }
        stage("stage => Build_Image") {
            steps {
                sh "sudo docker image ls && ls -l && sudo cat Dockerfile"
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