pipeline {
    agent any

    environment { // [health-check , log-file , dovcker-var , git-repo] *
        CMD = sh(script:"date '+%F %T'",returnStdout: true).trim()
        VAR_LOG = "Reports_${env.CMD}"
        REPO_ANSIBLE = 'https://github.com/repo/ansible'
        APP_REPO = 'linuxbest531/python-application'
        APP_NAME = "python-app-${env.BUILD_NUMBER}"
        PORT = '5000'
        GATE_STATUS = false
    }

    stages {
        stage('Test_figure') {
            stages {
                stage('LOG Loading .......') {
                    steps {
                        script {
                            script_1 = load 'project/project_file.groovy'
                            def log_1 = script_1.logmsg("info","pipeline started","stage =>(LOG_Loading)")
                        }
                    }
                }
                stage('') {
                    steps {
                        script {
                            Test_1 = sh (script:'ping -c 4 8.8.8.8', returnStdout: true).trim()
                            echo "connection result : "{Test_1}
                            Test_2 = sh (script:'ping -c 4 140.82.121.4', returnStatus: true).trim()
                            echo "connection result : "{Test_2}
                            Test_3 = sh(script:'curl --connection-timeout 5 \
                                                --silent --show-error \
                                                --output /dev/null \
                                                http://github.com/ ',returnStatus:true)
                        }
                    }
                }
            }
        }
        stage('Checkout-SCM') {
            when {
                expression {
                        env.Test_1 == 0 && env.Test_2 == 0 // [env.Test_1.result == 0 && env.Test_2.result == 0]
                }
            }
            steps {
                script{
                    def log_1 = script_1.logmsg("info","pipeline started","stage =>(checkout_SCM)")
                }
                dir('Ansible'){
                    sh "mkdir -p ${env.VAR_LOG}"
                    git branch: 'main', url: "${env.REPO_ANSIBLE}"
                }
                dir('Script') {
                    sh "mkdir -p ${env.VAR_LOG}"
                    git branch: 'main', url: 'https://github.com/repo/Docker-application'
                }
                echo "stage Checkout-SCM passed ...!"
            }
        }
        stage('Trivy-Repository'){
            stages {
                stage('Initialize SCAN') {
                    steps {
                        echo "TRIVY => [vulerability scanning] - REPOSITORY\n"
                        echo "==S==C==A==N==${env.REPO_ANSIBLE}=R==E==P==O==!=="
                    }
                }
                stage('Configuration_Scan') {
                    steps {
                        dir('Ansible'){
                            script {
                                def Scan_ansible = sh (script:"trivy config \
                                                                    --misconfig-scanners ansible \
                                                                    --format json \
                                                                    --output ${env.VAR_LOG}/Configuration_Scan.json \
                                                                    ." ,returnStatus: true)
                                if(Scan_ansible == 0) {
                                    ${env.GATE_STATUS} = true
                                    echo "SEC_GATE PASSED => gate_status : ${env.GATE_STATUS}"
                                } else {
                                    ${env.GATE_STATUS} = false
                                    error("SEC_GATE FAILED => gate_status : ${env.GATE_STATUS}")
                                }
                            }
                        }
                        echo "Stage Configuration_Scan passed ! [condition_PASSED] => Scanning"
                    }
                }
                stage('Scanning') {
                    parallel {
                        stage('REPO') {
                            steps {
                                dir('Ansible'){
                                    script {
                                        def Scan_1 = sh (script:"trivy repo \
                                                                --db-repository ghcr.io/aquasecurity/trivy-db \
                                                                --format json \
                                                                --output ${env.VAR_LOG}/REPO.json \
                                                                ${env.REPO_ANSIBLE}" ,returnStatus: true)
                                        if(Scan_1 == 0) {
                                            ${env.GATE_STATUS} = true
                                            echo "SEC_GATE PASSED => gate_status : ${env.GATE_STATUS}"
                                        } else {
                                            ${env.GATE_STATUS} = false
                                            error("SEC_GATE FAILED => gate_status : ${env.GATE_STATUS}")
                                        }
                                    }
                                }
                            }
                        }
                        stage('DOCKERFILE') {
                            steps {
                                dir('Docker'){
                                    script {
                                        def Scan_2 = sh (script:"trivy config \
                                                        --severity HIGH,CRITICAL \
                                                        --scanners vuln,secret,misconfig \
                                                        --format json \
                                                        --exit-code 1 \
                                                        --output ${env.VAR_LOG}/DOCKERFILE.json \
                                                        . ",returnStatus: true) // *******
                                        if(Scan_2 == 0) {
                                            ${env.GATE_STATUS} = true
                                            echo "SEC_GATE PASSED => gate_status : ${env.GATE_STATUS}"
                                        } else {
                                            ${env.GATE_STATUS} = false
                                            error("SEC_GATE FAILED => gate_status : ${env.GATE_STATUS}")
                                        }
                                    }                
                                }
                            }
                        }
                    }
                }
            }
        }
        stage('Build') {
            steps {
                script {
                    dockerImage = docker.build("${env.APP_REPO}:${env.BUILD_NUMBER}")
                }
                sh "docker image ls" // sh "docker images"   
                echo "stage Build passed"
            }
        }
        stage('Trivy_Image') { // sec-gate
            when {
                expression {
                    ${env.GATE_STATUS} = true
                }
            }
            stages {
                stage('Initialize SCAN') {
                    steps {
                        echo "TRIVY => [image/reciepe scanning] - REPOSITORY\n"
                        echo "==S==C==A==N==${env.REPO_ANSIBLE}=I==M==A==G==E=="
                    }
                }
                stage('Image_Scan') {
                    steps {
                        dir('Docker') {
                            script {
                                def Scan_3 = sh (script:"trivy image \
                                                        --severity HIGH,CRITICAL \
                                                        --format json \
                                                        --exit-code 1 \
                                                        --scanners vuln,secret,misconfig \
                                                        --output ${env.VAR_LOG}/Image_Scan.json \
                                                        ${env.APP_REPO}:${env.BUILD_NUMBER}", returnStatus:true)
                                if(Scan_3 == 0) {
                                    ${env.GATE_STATUS} = true
                                    echo "SEC_GATE PASSED => gate_status : ${env.GATE_STATUS}"
                                } else {
                                    ${env.GATE_STATUS} = false
                                    error("SEC_GATE FAILED => gate_status : ${env.GATE_STATUS}")
                                }
                            }
                        }
                        echo "stage trivy image passed"
                    }
                }
            }
        }
        stage('Test') {
            steps {
                sh """
                docker run -d \
                -p ${env.PORT}:${env.PORT} \
                --name ${env.APP_NAME} \
                ${env.APP_REPO}:${env.BUILD_NUMBER}

                sleep 5

                curl --fail-with-body http://localhost:${env.PORT}/ && echo "App is running fine !" || echo "App isn't running fine !!"
                
                sleep 10
                docker container stop ${env.APP_NAME} && docker container rm -f ${env.APP_NAME} || false
                """
                echo "stage Test passed"
            }
        }
        stage('Deployment') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'Dockerpass_1') {
                        dockerImage.push()
                    }
                }
                echo "stage Deployment passed"
            }
        }
    }
    post {
        always {
            sh "curl \
                -u user:password \
                -i -H 'Accept:application/json' \
                --output log_output.txt \
                ${JENKINS_URL}/job/${JOB_NAME}/lastBuild/consoleText
                "
            echo "Task Done ."
        }
        success {
            echo "Task was Successful !!"
            sh "docker rmi -f ${env.APP_REPO}:${env.BUILD_NUMBER}"
            // sudo docker rmi -f $(sudo docker image ls | grep python-application | awk {'print $1'}) 2> /dev/null 
            // sudo docker rmi -f $(docker image ls -q '*/python-application') 2> /dev/null
            // sh "docker system prune -af" // clearing the docker-workspace [for important just testing workspace(capacity-matters)]    
            script {
                emailext(
                    attachmentsPattern: '*.txt',            
                    subject: "Build ${env.BUILD_NUMBER} of ${env.JOB_NAME} was successful",
                    body: """
                    Check the details at ${env.BUILD_URL}""",
                    to: "email@gmail.com"
                )
            }
        }
        failure {
            echo "Task was failed !!!"
            script {
                emailext(
                    attachmentsPattern: 'log.txt',
                    subject: "Build ${env.BUILD_NUMBER} of ${env.JOB_NAME} was failed",
                    body: """
                    Check the details at ${env.BUILD_URL}""",
                    to: "email@gmail.com"
                )
            }
        }
    }
}


// function in grrovy file for logging and attaching ut to email-notification .
// multi-json output files .
// echo commands -> log-msg
// trivy dashboard
// create /reports directory


