*** Settings ***
Library    SSHLibrary
Test Setup    Open connection and log in    ${HOST}    ${USERNAME}    ${PASSWORD}
Test Teardown    Close All Connections

*** Variable ***
${write_read_timeout}    10 seconds

*** Test Cases ***
Execute Command And Verify Output
    [Documentation]    Execute Command can be used to ran commands on the remote machine.
    ...                The keyword returns the standard output by default.
    Execute Command    touch /var/tmp/test
    ${output}=    Execute Command    ls /var/tmp/
    log   ${output}
    Close Connection

*** Keywords ***
Open connection and log in
    [Arguments]    ${host}    ${login}    ${password}
    SSHLibrary.Open Connection    ${host}
    Set Client Configuration    timeout=${write_read_timeout}    prompt=$
    SSHLibrary.login    ${login}    ${password}
    Write    whoami
    ${whoami}=    Read Until Prompt
