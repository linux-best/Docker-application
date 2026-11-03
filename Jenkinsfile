pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
    }

    environment { // [health-check , log-file , dovcker-var , git-repo] *
        VAR_LOG      = "Reports"
        REPO_ANSIBLE = 'https://github.com/linux-best/ansible'
        APP_REPO     = 'linuxbest531/python-application'
        APP_NAME     = "python-app-${env.BUILD_NUMBER}"
        PORT         = '5000'
        GATE_STATUS  = false 
    }

    stages {
        stage("Node Info") {
            steps {
                cleanWs()
                echo "Node name $NODE_NAME and the labels: $NODE_LABELS"
            }
        }
        stage('Test_figure') {
            environment {
                // putting some endpoits that are critical for us to have connection with them always . 
                IP_ADD_1 = '8.8.8.8'
                IP_ADD_2 = '140.82.121.4'
            }
            steps {
                script {
                    def lst_map = [:]
                    lst_map[env.IP_ADD_1] = false
                    lst_map[env.IP_ADD_2] = false
                    try {
                        def results = lst_map.collect { ip,status ->
                            Test_Connection(ip)
                        }
                        env.Gate_Connection = results.every { it }
                        print(env.Gate_Connection.getClass())
                        sh '''
                        curl --connect-timeout 5 \
                            --silent --show-error \
                            --output /dev/null \
                            http://github.com/
                        '''
                        println('moving on to stage => Checkout-SCM')
                    } catch(Exception ex) {
                        println('An error CORRUPTED the pipeline')
                        echo "Exception : ${ex.getMessage()}"
                        sh 'resolvectl flush-caches' // => clearing DNS-cach
                        error('Build has FAILED')
                    } finally {
                        echo 'Exception Checking DONE !'
                    }
                }
            }
        }   
        stage('Checkout-SCM') {
            when {
                expression {
                    (env.Gate_Connection.toBoolean()) == true
                }
            }
            steps {
                dir('Ansible'){
                    git branch: 'main', url: "${env.REPO_ANSIBLE}"
                    sh "mkdir -p ${env.VAR_LOG}"
                }
                dir('Docker') {
                    git branch: 'main', url: 'https://github.com/linux-best/Docker-application'
                    sh "mkdir -p ${env.VAR_LOG}"
                }
                echo "stage Checkout-SCM passed ...!"
            }
        }
        stage('Trivy-Repository'){
            stages {
                stage('Initialize SCAN') {
                    steps {
                        sh "cp -r $JENKINS_HOME/trivy-db/ ./trivy-db"
                        stash(includes: 'trivy-db/**', name: 'trivy-db')
                        echo "TRIVY => [vulnerability scanning] - REPOSITORY\n"
                        echo "==S==C==A==N= ${env.REPO_ANSIBLE} =R==E==P==O==!=="
                    }
                }
                stage('Configuration_Scan') {
                    steps {
                        dir('Ansible'){
                            sh "ls -l"
                            script {
                                env.Scan_ansible = sh (script:"trivy config \
                                                                    --misconfig-scanners ansible \
                                                                    --format json \
                                                                    --exit-code 1 \
                                                                    --output \"${env.VAR_LOG}/Configuration_Scan.json\" \
                                                                    ." ,returnStatus: true)
                            }
                        }
                        echo "Stage Configuration_Scan passed ! [condition_PASSED] => Scanning"
                    }
                }
                stage('Scanning-REPO') {
                    steps {
                        dir('Ansible'){
                            script {
                                env.resultStatus = [:]
                                unstash 'trivy-db'
                                def Scan_1 = sh (script:"trivy repo \
                                                        --cache-dir ./trivy-db/db \
                                                        --skip-db-update \
                                                        --format json \
                                                        --output \"${env.VAR_LOG}/REPO.json\" \
                                                        ${env.REPO_ANSIBLE}",returnStatus: true)
                                env.resultStatus['REPO'] = (Scan_1 == 0 && env.Scan_ansible?.toBoolean())
                            }
                        }
                    }
                }
                stage('DOCKERFILE') {
                    steps {
                        dir('Docker'){
                            script {
                                unstash 'trivy-db'
                                def Scan_2 = sh (script:"trivy fs \
                                                --cache-dir ./trivy-db/db \
                                                --skip-db-update \
                                                --severity HIGH,CRITICAL \
                                                --scanners vuln,secret,misconfig \
                                                --format json \
                                                --exit-code 1 \
                                                --output \"${env.VAR_LOG}/DOCKERFILE.json\" \
                                                . ",returnStatus: true) // *******
                                env.resultStatus['DOCKERFILE'] = (Scan_2 == 0)
                            }                
                        }
                    }
                }
                stage('Conclusion_Gate') {
                    steps {
                        script {
                            env.GATE_STATUS = resultStatus.values().every { it }
                            if(!env.GATE_STATUS?.toBoolean()) {
                                echo "SEC_GATE FAILED => gate_status : ${env.GATE_STATUS}"
                            } else {
                                echo "SEC_GATE PASSED => gate_status : ${env.GATE_STATUS}"
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
                    (env.GATE_STATUS?.toBoolean()) == true
                    // env.GATE_STATUS?.toBoolean() == true  (if pre-condition fails)
                }
            }
            stages {
                stage('Initialize SCAN') {
                    steps {
                        echo "TRIVY => [image/reciepe scanning] - REPOSITORY\n"
                        echo "==S==C==A==N= ${env.REPO_ANSIBLE} =I==M==A==G==E=="
                    }
                }
                stage('Image_Scan') {
                    steps {
                        dir('Docker') {
                            script {
                                unstash 'trivy-db'
                                def Scan_3 = sh (script:"trivy image \
                                                        --cache-dir ./trivy-db/db \
                                                        --skip-db-update \
                                                        --severity HIGH,CRITICAL \
                                                        --format json \
                                                        --exit-code 1 \
                                                        --scanners vuln,secret,misconfig \
                                                        --output \"../${env.VAR_LOG}/Image_Scan.json\" \
                                                        ${env.APP_REPO}:${env.BUILD_NUMBER}", returnStatus:true)
                                env.resultStatus.pop()
                                assert env.resultStatus == [:]
                                env.resultStatus['Image_Scan'] = (Scan_3 == 0)     // bool stay bool in local variable 
                                env.GATE_STATUS = resultStatus.each { it.value() } // but in env one you need to use "?"
                                if(!GATE_STATUS) {
                                    error("SEC_GATE FAILED => gate_status : ${env.GATE_STATUS}")
                                } else {
                                    echo "SEC_GATE PASSED => gate_status : ${env.GATE_STATUS}"
                                }
                            }
                        }
                        echo "stage trivy image passed"
                    }
                }
            }
        }
        stage('Test') { // curl --fail-with-body http://localhost:${env.PORT}/ 
                        // && 
                        // echo "App is running fine !"          <= BACKUP-COMMAND = it would provide the same test service (HTTP-reachability)
                        // || 
                        // echo "App isn't running fine !!"
            steps {
                withEnv(["PORT=${env.PORT}",
                "APP_NAME=${env.APP_NAME}",
                "APP_REPO=${APP_REPO}",
                "BUILD_NUMBER=${env.BUILD_NUMBER}"]) {

                    sh '''#!/bin/bash
                        docker run -d \
                        -p "$PORT:$PORT" \
                        --name "$APP_NAME" \
                        "$APP_REPO:$BUILD_NUMBER"
                        sleep 5

                        trap "docker container stop "$APP_NAME" && docker container rm -f "$APP_NAME"" EXIT
                        index=1
                        counter=3

                        while [ $index -le $counter ]
                        do
                                if curl -sf --connect-timeout 1 \
                                http://localhost:"$PORT"/ --output /dev/null
                                then
                                        echo "App is running fine"
                                        sleep 10
                                        exit 0
                                else
                                        echo "CHECKING FAILED   !" && sleep 2
                                        index=$((index+1))
                                fi
                        done
                        echo "App isn't running fine :(" && exit 1
                    '''
                }    
                echo "stage Test passed"
            }
        }
        stage('Deployment') {
            steps {
                withCredentials([string(
                    credentialsId : 'Dockerpass' ,
                    variable : 'PASS_KEY'    
                )]) {
                    sh "echo '$PASS_KEY' | docker login -u 'linuxbest531' --password-stdin"
                }       
                script {
                    docker.withRegistry('https://index.docker.io/v1/', "Docker_Auth_Keys") {
                        dockerImage.push()
                    }
                }
                echo "stage Deployment passed"
            }
        }
    }
    post {
        always { 
            // Credentials managment (SECURITY) & Capturing the LATEST Report
            withCredentials([usernamePassword(
                credentialsId : 'JenkinsPass' ,
                usernameVariable : 'username' ,
                passwordVariable : 'password'
            )]) {
                sh """
                curl \
                -u '$username:$password' \
                -i -H 'Accept:application/json' \
                --output log_output.txt \
                ${JENKINS_URL}/job/${JOB_NAME}/lastBuild/consoleText
                """
            }
            // Cleaning the WORKSPACE
            cleanWs(
                cleanWhenNotBuilt: false,
                deleteDirs: true,
                disableDeferredWipeout: true,
                notFailBuild: true,
                patterns : [[pattern:'**/*.json',type:'EXCLUDE'],
                            [pattern: 'log_output.txt',type:'EXCLUDE'],
                            [pattern: './trivy-db',type:'INCLUDE']]
            )
            echo "Task Done ."
        }
        success {
            echo "Task was Successful !!"
            script {
                Cleanup()
                emailext(
                    attachmentsPattern: 'log_output.txt',            
                    subject: "Build ${env.BUILD_NUMBER} of ${env.JOB_NAME} was successful",
                    body: """
                    Check the details at ${env.BUILD_URL}""",
                    to: 'amirmahdifhp@gmail.com'
                )
            }
        }
        failure {
            echo "Task was failed !!!"
            script {
                Cleanup()
                emailext(
                    attachmentsPattern: 'log_output.txt',
                    subject: "Build ${env.BUILD_NUMBER} of ${env.JOB_NAME} was failed",
                    body: """
                    Check the details at ${env.BUILD_URL}""",
                    to: 'amirmahdifhp@gmail.com'
                )
            }
        }
    }
}
def Cleanup() {
    sh "docker rmi -f ${env.APP_REPO}:${env.BUILD_NUMBER}"
    // sudo docker rmi -f $(sudo docker image ls | grep python-application | awk {'print $1'}) 2> /dev/null 
    // sudo docker rmi -f $(docker image ls -q '*/python-application') 2> /dev/null
    // sh "docker system prune -af" // clearing the docker-workspace [for important just testing workspace(capacity-matters)]    

}
def Test_Connection(String IP){
    def Test = sh (script:"ping -c 4 $IP", returnStatus: true).toInteger()
    Boolean Result = (Test == 0) ? true : false
    echo "connection -> $IP - Health_Checked : ${Result}"
    return Result
}

// ====================================================================================
// function in grrovy file for logging and attaching ut to email-notification .
// multi-json output files .
// echo commands -> log-msg on script fail part 
// trivy dashboard
// create /reports directory
// ====================================================================================
