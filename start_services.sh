#!/bin/bash

# $# checks for commandline arguments
# No arguments => start all services
# Example arguments => ./start_services.sh service-discovery auth-service
# $! gives the pid of the last command
# nohup runs a jar in the background

# set env vars

echo "Setting env vars..."

chmod 777 ./set_env_vars_local.sh
source ./set_env_vars_local.sh # sets the env variables in the current terminal session

if [ "$#" -eq 0 ]; then
    echo "Starting all services...";
    services=(service-discovery api-gateway auth-service note-service)
    for i in "${services[@]}"
    do 
        cd ./"$i"
        gradle build -x test
        if [ "$i" = "service-discovery" ]; then
            gradle bootRun > ../logs/"${i}.log" 2>&1 &  # write into the log/pid file starting from first micro-service
            echo $! $i > ../pid.file
        else
            gradle bootRun > ../logs/"${i}.log" 2>&1 & # append to the log/pid file
            echo $! $i >> ../pid.file
        fi
        cd ..
    done
    
else 
    for service in "$@" 
    do
        echo "Starting $service...";
        cd ./"$service"
        gradle build -x test
        gradle bootRun > ../logs/"$service.log" 2>&1 &
        echo $! "$service" >> ../pid.file
        cd ..
    done
fi