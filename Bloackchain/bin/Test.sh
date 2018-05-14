#!/bin/bash

##
# Florian GUILLEMEAU, Sylvain LANGENBRONN
##

rmiregistry 3045 &
rmiregistry 3046 &
rmiregistry 3047 &
rmiregistry 3048 &

##
# Lancement des serveurs
# La topologie est la suivante
#
#  S4
#  |
# S1----S2
#  |     |
#  -----S3

java Serveur 0 localhost 3045 & > serveur3045.txt # S1
java Serveur 1 localhost 3046 localhost 3045 & > serveur3046.txt # S2
java Serveur 2 localhost 3047 localhost 3045 localhost 3046 & > serveur3047.txt # S3
java Serveur 3 localhost 3048 localhost 3045 & > serveur3048.txt # S4


# Dors longtemps
sleep 60*10

#Tue les processus
pkill java
pkill rmiregistry