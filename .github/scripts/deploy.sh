#!/bin/sh
set -eu

echo "Registering SSH keys..."
mkdir -p ~/.ssh

printf '%s\n' "$PRKEY" > ~/.ssh/id_rsa
chmod 600 ~/.ssh/id_rsa

printf '%s\n' "$PBKEY" > ~/.ssh/id_rsa.pub
chmod 600 ~/.ssh/id_rsa.pub

eval $(ssh-agent)

ssh-add ~/.ssh/id_rsa
echo "Add known hosts"
printf '%s %s\n' "95.163.242.79" "$PBKEY" > /etc/ssh/ssh_known_hosts

ssh deployer@95.163.242.79 "JBTAG=$REF docker compose -f /opt/staging/docker-compose.yaml up -d java-backend"