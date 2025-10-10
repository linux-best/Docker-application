    pipeline {
    agent any

    environment { 
        APP_REPO = 'linuxbest531/python-application'
        APP_NAME = "python-app-${env.BUILD_NUMBER}"
        PORT = '5000'
    }

    stages {
        stage("stage => Checkout-SCM") {
            steps {
                git branch: 'main', url: 'https://github.com/linux-best/Docker-application'
            }
        }
        stage('Process => Build_Image') {
            steps {
                script {
                    dockerImage = docker.build("${env.APP_REPO}:${env.BUILD_NUMBER}")
                }
                sh "docker image ls"
            }
        }
        stage("Process => Test_Container,Cleaning_workspace") {
            stages {
                stage('Test_Container') {
                    steps {
                        sh """
                        docker run --rm -p ${env.PORT}:${env.PORT} --name ${env.APP_NAME} ${env.APP_REPO}:${env.BUILD_NUMBER}
                        curl http://localhost:${env.PORT}/ && echo "App is running fine !" || echo "App isn't running fine !!"
                        sleep 20
                        """
                    }       
                }
                stage('Cleaning_workspace') {
                    steps {
                        sh """
                        sudo systemctl restart docker.socket docker.service 
                        sudo systemctl status docker.socket docker.service
                        sudo kill -9 $(sudo lsof -t -i :${env.PORT})
                        docker ps -a && docker image ls
                        """
                    }
                }
            }
        }
        stage("Process => Deploy_Application") {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'Dockerpass_1') {
                        dockerImage.push()
                    }
                }
            }
        }
    }
    post {
        success {
            echo "Done !"
            sh "docker system prune -af" // clearing the docker-workspace    
            script {
                emailext(
                    subject: "Build ${env.BUILD_NUMBER} of ${env.JOB_NAME} was failed",
                    body: """
                    Check the details at ${env.BUILD_URL}""",
                    to: "amirmahdifhp@gmail.com"
                )
            }

        } // email
        failure {
            echo "Fuck !!"
            script {
                emailext(
                    subject: "Build ${env.BUILD_NUMBER} of ${env.JOB_NAME} was failed",
                    body: """
                    Check the details at ${env.BUILD_URL}""",
                    to: "amirmahdifhp@gmail.com"
                )
            }
        
        } // email
    }
}

