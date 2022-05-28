#!/bin/bash
if [ "$#" -eq 0 ]; then
    echo "Restarting all services...";
    sh stop_services.sh
    sh start_services.sh 
else
    declare -a services=()
    for param in "$@"; 
    do
        services+=("$param")
    done
    sh stop_services.sh ${services[@]}
    sh start_services.sh ${services[@]}
fi