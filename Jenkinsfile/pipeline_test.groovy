def steps = "hola"
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

    }
}