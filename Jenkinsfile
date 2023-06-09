def password = "${PASSWORD}"
def label = "docker-jenkins-${UUID.randomUUID().toString()}"
def home = "/home/jenkins"
def workspace = "${home}/workspace/build-docker-jenkins"
def workdir = "${workspace}/src/localhost/docker-jenkins/"

def repoName = "qa-docker-nexus.mtnsat.io/dockerrepo/java-repo-test:${BUILD_ID}"
def tag = "$repoName:latest"

podTemplate(yaml: '''
              apiVersion: v1
              kind: Pod
              spec:
                volumes:
                - name: docker-socket
                  emptyDir: {}
                containers:
                - name: maven
                  image: yannoff/maven:3-openjdk-19
                  command:
                  - sleep
                  args:
                  - 99d
                - name: node
                  image: node:latest
                  command:
                  - sleep
                  args:
                  - 99d
                - name: docker
                  image: docker:19.03.1
                  readinessProbe:
                    exec:
                      command: [sh, -c, "ls -S /var/run/docker.sock"]
                  command:
                  - sleep
                  args:
                  - 99d
                  volumeMounts:
                  - name: docker-socket
                    mountPath: /var/run
                - name: docker-daemon
                  image: docker:19.03.1-dind
                  securityContext:
                    privileged: true
                  volumeMounts:
                  - name: docker-socket
                    mountPath: /var/run
''') {  
  
  
  node(POD_LABEL) {

       stage('Git sCM Checkout') {
            git branch: 'main', credentialsId: 'gitssh-1', url: 'https://github.com/faisalbasha19/java-repo-test-1.git'
        }
    
        stage('SonarQube Analysis') {
          def mvn = tool 'sonarQubeScanner';
          withSonarQubeEnv() {
            container('maven') {
                  sh "mvn clean verify sonar:sonar -Dsonar.projectKey=java"
            }
          }
        }

        stage('maven build') {
          container('maven') {
                  sh 'mvn clean package -DSkipTests=true'
                   archive 'target/*.jar'
          }
        }
      
        stage('docker build') {
               container('docker'){
                  sh 'docker version && docker build -t qa-docker-nexus.mtnsat.io/dockerrepo/java-repo-test:${BUILD_ID} .'                   
               }
        }
    
        stage('docker login') {
                container('docker') {
                  sh 'docker login -u admin -p admin qa-docker-nexus.mtnsat.io'
                }
        }
        stage('docker push'){
               container('docker'){
                   sh 'docker push qa-docker-nexus.mtnsat.io/dockerrepo/java-repo-test:${BUILD_ID}'
               }
        }
  }
}
