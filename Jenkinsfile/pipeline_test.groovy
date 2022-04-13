pipeline{
    //definition des agents : je sais pas encore trop a quoi ça sert
    agent any
    //définition des options de la pipeline
    options {
        buildDiscarder(logRotator(numToKeepStr:'15')) // on concerve que les résultats des 15 derniers builds
        timeout(time: 120, unit: 'MINUTES') // la pipeline fail après deux heures sans résultats
    }
    // définition des parametres necessaires pour realiser le lancement d'un playbook quelconque
    parameters {
        string(defaultValue: 'svr_test', name: 'OINAME', description: "nom de l'OI en cours de qualification")
    }
    stages {
        // Premiere etape : faire un update du git pour pouvoir récuperer le playbook
        // TODO : faire en sorte de pouvoir pull sur la branche choisit dans les parametres
        stage ('Update Git'){
            steps {
                sh '''
                    cd /var/tmp/oi_test;
                    git pull;
                    '''
            }
        }
        stage ('Defining steps'){
            steps {
                def steps = readJSON file : '/var/tmp/oi_test/steps/${OINAME}/steps.json'
                println steps
            }
        }
    }
}