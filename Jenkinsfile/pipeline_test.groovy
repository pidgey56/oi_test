def steps_qualif = "" // définition du receptacle des étapes
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
                script{
                    steps_qualif = readJSON file: "/var/tmp/oi_test/steps/${OINAME}/steps.json"
                }
                // sh echo "contenue de la variable ${steps_qualif}"
            }
        }
        stage ("PREREQUIS : création d'un snapshot"){
            when{
                expression { return ( steps_qualif['prerequis']['snapshot_init'] == "yes" )}
            }
            steps {
                sh echo "you have failed"
            }
        }
        stage ('PREREQUIS : guide de securité'){
            when{
                expression { return ( steps_qualif['prerequis']['guide_securite'] == "yes" )}
            }
            steps {
                sh echo "you have failed"
            }
        }
        stage ("PREREQUIS : execution de playbook prerequis.yml"){
            when{
                expression { return ( steps_qualif['prerequis']['playbook'] == "yes" )}
            }
            steps{
                sh echo "GREAT SUCCESS, VERY GOOD VERY NICE"
            }
        }
        stage ("PREREQUIS : execution des tests robotframeworks commons"){
            when{
                expression { return ( steps_qualif['prerequis']['common'] == "yes" )}
            }
            steps{
                sh echo "you have failed"
            }
        }
        stage ("PREREQUIS : execution des tests robotframework OI"){
            when{
                expression { return ( steps_qualif['prerequis']['robotframework'] == "yes" )}
            }
            steps{
                sh echo "ok"
            }
        }        
    }
}