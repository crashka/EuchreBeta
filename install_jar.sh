#!/usr/bin/env bash

scriptdir="$(dirname $(readlink -f $0))"

path_to_file="${scriptdir}"/EuchreBeta.jar
group_id=game
artifact_id=euchrebeta
version=0.9
packaging=jar

mvn install:install-file         \
   -Dfile="${path_to_file}"      \
   -DgroupId="${group_id}"       \
   -DartifactId="${artifact_id}" \
   -Dversion="${version}"        \
   -Dpackaging="${packaging}"    \
   -DgeneratePom=true
