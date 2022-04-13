def steps_qualif = "" // définition du receptacle des étapes
//note on passe du txt au json pour de futures utilisations plus cools 

pipeline{
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr:'15')) // on concerve que les résultats des 15 derniers builds
        timeout(time: 120, unit: 'MINUTES') // la pipeline fail après deux heures sans résultats
    }
    parameters {
        string(defaultValue: '10.120.177.77', name: 'HOST', description: "adresse IP de la machine sur laquelle effectuer le playbook")
        password(name: 'PASSWORD', description: "mot de passe pour se connecter à la VM", defaultValue:"EdfP@ssw0rd2018")
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
            }
        }
        stage ("PREREQUIS : création d'un snapshot"){
            when{
                expression { return ( steps_qualif['prerequis']['snapshot_init'] == "yes" )}
            }
            steps {
                echo "NON FONCTIONNEL ACTUELLEMENT : ABSENCE DE VSPHERE"
            }
        }
        stage ('PREREQUIS : guide de securité'){
            when{
                expression { return ( steps_qualif['prerequis']['guide_securite'] == "yes" )}
            }
            steps {
                echo "you have failed"
            }
        }
        stage ("PREREQUIS : execution de playbook prerequis.yml"){
            when{
                expression { return ( steps_qualif['prerequis']['playbook'] == "yes" )}
            }
            steps{
                ansiblePlaybook (
                    playbook: "/var/tmp/oi_test/playbooks/$OINAME/prerequis.yml",
                    inventory: "/var/tmp/oi_test/playbooks/hosts",
                    colorized: false,
                )
            }
        }
        stage ("PREREQUIS : execution des tests robotframeworks commons"){
            when{
                expression { return ( steps_qualif['prerequis']['common'] == "yes" )}
            }
            steps{
                echo "you have failed"
            }
        }
        stage ("PREREQUIS : execution des tests robotframework OI"){
            when{
                expression { return ( steps_qualif['prerequis']['robotframework'] == "yes" )}
            }
            steps{
                sh "robot -v HOST:$HOST -v USERNAME:admsrv -v PASSWORD:$PASSWORD --outputdir /var/tmp/robotoutput/$HOST /var/tmp/oi_test/robotframework/$OINAME/prerequis.robot"
            }
        }        
    }
    post{
        step('publish robotframework result'){
            echo "success"
        }
    }
}