// Declarative Jenkins pipeline for the Parabank Selenium+Cucumber project
// - Parameters to control branch and headless mode
// - Builds with Maven, archives test artifacts, screenshots and logs
// - Publishes HTML report when available

pipeline {
  agent any

  options {
    timestamps()
    //ansiColor('xterm')
    // keep 10 builds
    buildDiscarder(logRotator(numToKeepStr: '10'))
  }

  parameters {
    string(name: 'BRANCH', defaultValue: 'main', description: 'Git branch to build')
    booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run browser in headless mode')
    booleanParam(name: 'CLEAN', defaultValue: true, description: 'Run mvn clean before build')
    string(name: 'MAVEN_GOALS', defaultValue: 'test', description: 'Maven goals to run (appended to mvn)')
  }

  environment {
    // Allow overriding mvn if needed on the node; otherwise assume 'mvn' on PATH
    MVN = 'mvn'
    // Extra mvn options
    MAVEN_OPTS = '-Xmx1024m'
  }

  stages {
    stage('Checkout') {
      steps {
        script {
          if (params.BRANCH?.trim()) {
            echo "Checking out branch: ${params.BRANCH}"
            checkout scm: [$class: 'GitSCM', branches: [[name: params.BRANCH]], userRemoteConfigs: scm.userRemoteConfigs]
          } else {
            echo 'Checking out default SCM configuration'
            checkout scm
          }
        }
      }
    }

    stage('Build & Test') {
      steps {
        script {
          // Build command
          def mvnCmd = env.MVN
          def goals = []
          if (params.CLEAN) { goals.add('clean') }
          goals.add(params.MAVEN_GOALS ?: 'test')
          // Pass headless flag into Maven properties so tests can toggle behavior
          def headlessProp = "-Dheadless=${params.HEADLESS}"
          def cmd = "${mvnCmd} ${goals.join(' ')} -U ${headlessProp} -Dclose=true"

          if (isUnix()) {
            sh "${cmd}"
          } else {
            bat "${cmd}"
          }
        }
      }
    }

    stage('Archive Results') {
      steps {
        script {
          // JUnit XML reports (surefire/failsafe)
          def junitGlobs = ['**/target/surefire-reports/*.xml', '**/target/failsafe-reports/*.xml']
          try {
            junit keepLongStackTraces: true, testResults: junitGlobs.join(',')
          } catch (Exception e) {
            echo "No JUnit XML found or junit step failed: ${e.message}"
          }

          // Archive artifacts: cucumber HTML, cucumber JSON, screenshots and logs
          def artifacts = []
          artifacts.add('cucumber.json')
          artifacts.add('target/cucumber-reports.html')
          artifacts.add('target/screenshots/**')
          artifacts.add('target/logs/**')
          artifacts.add('target/*.log')
          archiveArtifacts artifacts: artifacts.join(','), allowEmptyArchive: true, fingerprint: true
        }
      }
    }

    stage('Publish HTML Reports') {
      steps {
        script {
          // If cucumber HTML report exists, publish it via the HTML Publisher plugin
          def htmlReport = fileExists('target/cucumber-reports.html')
          if (htmlReport) {
            publishHTML([
              allowMissing: false,
              alwaysLinkToLastBuild: true,
              keepAll: true,
              reportDir: 'target',
              reportFiles: 'cucumber-reports.html',
              reportName: 'Cucumber Report'
            ])
          } else {
            echo 'No HTML report found at target/cucumber-reports.html'
          }
        }
      }
    }
  }

  post {
    always {
      script {
        // Ensure logs/screenshots are archived even on failure
        archiveArtifacts artifacts: 'target/logs/**,target/screenshots/**,cucumber.json', allowEmptyArchive: true
      }
      cleanWs()
    }

    failure {
      script {
        // Print a hint where to find logs
        echo 'Build failed — check archived artifacts (screenshots, logs, cucumber.json) for failure details.'
      }
    }
  }
}
