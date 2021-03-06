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
        // Informations nécessaire pour la connection a la VM
        string(defaultValue: '10.120.177.77', name: 'HOST', description: 'Host sur lequel effectuer la qualification')
        password(name: 'PASSWORD', description: "mot de passe pour se connecter à la VM", defaultValue:"EdfP@ssw0rd2018")
        // Récupération du playbooks
        // TODO : modifier cette ligne pour pouvoir donner le chemin du playbook directement pour pouvoir lancer 
        // n'importe quel playbook sur n'importe quel VM
        string(defaultValue: 'svr_test', name: 'OINAME', description: "nom de l'OI en cours de qualification")
        // string(defaultValue: 'master', name: 'BRANCH', description: "branche dans laquelle Jenkins doit aller chercher le playbook a effectuer\n
        // par defaut : - master pour Linux\n
        //              - dev pour Windows\n
        //              - Si l'on a créer une autre branche pour pouvoir tester AIX par exemple")
        string(defaultValue: 'prerequis.yml', name: 'PLAYBOOK_NAME', description: "nom du playbook a lancer")
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

        stage ('Playbook launch'){
            steps {
                ansiblePlaybook (
                    playbook: "/var/tmp/oi_test/playbooks/$OINAME/$PLAYBOOK_NAME",
                    inventory: "/var/tmp/oi_test/playbooks/hosts",
                    colorized: true,
                )
            }
        }   
    }
}