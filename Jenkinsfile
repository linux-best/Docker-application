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
                sh "docker image ls"
            }
        }

        stage("Process => Test_Container") {
            steps {
                script {
                    def run_container = sh(script: 'docker run --rm -it --name py_app -p 5000:5000 linuxbest531/python-application:${env.BUILD_NUMBER}',
                    returnStatus: true)

                    if (run_container == 0) {
                        echo "Running_Container passed !"
                    } else {
                        error('Failed to Run the container !!')
                    }
                }
            }
        }
        stage("Process => Deploy_Application") {
            steps {
                echo script {
                    docker.withRegistry('https://index.docker.io/v1/', 'Dockerpass') {
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