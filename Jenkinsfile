pipeline {
    agent any

    environment {
        REPO_PROJECT = 'https://github.com/linux-best/Docker-application'
        APP_WORKSPACE = "python-application-1/"  
    }

    stages {
        stage("Process => Checkout-SCM") {
            steps {
                dir("App-1") {
                    git branch: 'main', url: "${env.REPO_PROJECT}"
                }
            }
        }
        stage("Process => Build_Image") {
            steps {
                dir('App-1') {
                    // sh "cd ${env.APP_WORKSPACE} && ls -l && sudo cat Dockerfile"
                    sh """cd ${env.APP_WORKSPACE} && pwd
                    pwd
                    """
                    sh "pwd"
                    script {
                    dockerImage = docker.build("linuxbest531/python-application:${env.BUILD_NUMBER}")
                }
                sh "sudo docker image ls"
                }
            }
        }
        stage("Process => Test_Container") {
            steps {
                dir('App-1') {
                    sh """
                    pwd
                    sudo docker ps -a
                    cd python-application-1/ && sudo docker run --rm linuxbest531/python-application:${env.BUILD_NUMBER} 
                    """
                }
            }
        }
        stage("Process => Deploy_Application") {
            steps {
                echo "Deploying ..........."
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-credentials') {
                        dockerImage.push()
                    }
                }
            }
        }
    }
    post {
        success {echo "Done !"}
        failure {echo "Fuck !!"}
    }
}

// sudo docker build -f Dockerfile -t linuxbest531/python-application .
