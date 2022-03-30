*** Settings ***
Library    SSHLibrary
Test Setup    Open connection and log in    ${HOST}    ${USERNAME}    ${PASSWORD}
Test Teardown    Close All Connections

*** Variable ***
${HOST}    192.168.1.58
${USERNAME}    pierre
${PASSWORD}    0db9182b6b
${write_read_timeout}    10 seconds

*** Test Cases ***
Execute Command And Verify Output
    [Documentation]    Execute Command can be used to ran commands on the remote machine.
    ...                The keyword returns the standard output by default.
    Execute Command    touch /home/pierre/Desktop/test2
    ${output}=    Execute Command    ls /home/pierre/Desktop
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
