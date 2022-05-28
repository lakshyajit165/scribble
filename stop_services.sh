#!/bin/bash

# $# checks for commandline arguments
# No arguments => stop all services
# Example arguments => ./stop_services.sh service-discovery auth-service
if [ "$#" -eq 0 ]; then
    echo "Stopping all services!";
    kill $(awk '{ print $1 }' ./pid.file);
else
    for service in "$@" 
    do
        echo "Stopping $service!";
        kill $(cat ./pid.file | grep "$service" | awk '{ print $1 }')
        sed -i "" "/$service/d" pid.file 
    done
fi
