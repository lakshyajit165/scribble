#!/bin/bash
if [ "$#" -eq 0 ]; then
    echo "Restarting all services...";
    ./stop_services.sh
    ./start_services.sh 
else
    declare -a services=()
    for param in "$@"; 
    do
        services+=("$param")
    done
    ./stop_services.sh ${services[@]}
    ./start_services.sh ${services[@]}
fi