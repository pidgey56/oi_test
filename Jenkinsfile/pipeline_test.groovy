def steps_qualif = "hola" // définition du receptacle des étapes
//note on passe du txt au json pour de futures utilisations plus cools 

pipeline{
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr:'15')) // on concerve que les résultats des 15 derniers builds
        timeout(time: 120, unit: 'MINUTES') // la pipeline fail après deux heures sans résultats
    }
    parameters {
        string(defaultValue: 'svr_test', name: 'OINAME', description: "nom de l'OI en cours de qualification")
    }
    stages {
        stage ('Update Git'){
            steps {
                sh '''
                    cd /var/tmp/oi_test;
                    git pull;
                    '''
            }
        }
        stage ('Defining the steps'){
            steps {
                step{
                    steps_qualif = readJSON file: '/var/tmp/steps/svr_test/steps.json'
                }
                step{
                    echo "contenue de la variable ${steps_qualif}"
                }
            }
        }
    }
}