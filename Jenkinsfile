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
        stage('Process => Build_Image') {
            steps {
                script {
                    dockerImage = docker.build("linuxbest531/python-application:${env.BUILD_NUMBER}")
                }
            }
        }

        stage("Process => Test_Container") {
            steps {
                sh """
                docker run --rm -i -p 5150:5150 linuxbest531/python-application:${env.BUILD_NUMBER}
                sleep 5
                docker ps
                docker ps - a
                """
            }
        }
        stage("Process => Deploy_Application") {
            steps {
                echo script {
                    docker.withRegistry('https://index.docker.io/v1/', "${env.DOCKER_AUTH}") {
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
        }
        failure {echo "Fuck !!"}
    }
}