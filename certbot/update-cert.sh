#!/bin/bash
# Make sure to reboot the SpringBoot application
KEYSTORE_PATH=$1
DOMAIN=$2

echo "Usage: update-cert.sh KEYSTORE_PATH DOMAIN"

if [ -z "$KEYSTORE_PATH" ]
then
    echo "Please provide keystore path"
    exit 1
fi

if [ -z "$DOMAIN" ]
then
    echo "Please provide domain"
    exit 1
fi

echo -n "Keystore password: "
read -sr password

# apt-get install certbot
certbot renew
openssl pkcs12 -export -in /etc/letsencrypt/live/"${DOMAIN}"/cert.pem -inkey /etc/letsencrypt/live/"${DOMAIN}"/privkey.pem -out "${KEYSTORE_PATH}" \
        -name tomcat -CAfile /etc/letsencrypt/live/"${DOMAIN}"/chain.pem -caname root -passout pass:"$password"