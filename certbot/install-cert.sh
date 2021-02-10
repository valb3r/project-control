#!/bin/bash
# Add the staging option (--staging) to certbot-auto if you wish to validate the procedure
KEYSTORE_PATH=$1
DOMAIN=$2
EMAIL=$3

echo "Usage: install-cert.sh KEYSTORE_PATH DOMAIN EMAIL"

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

if [ -z "$EMAIL" ]
then
    echo "Please provide email"
    exit 1
fi

echo -n "Keystore password: "
read -sr password

# apt-get install certbot
certbot certonly --debug --non-interactive --email "${EMAIL}" --agree-tos --standalone -d "${DOMAIN}" --keep-until-expiring
openssl pkcs12 -export -in /etc/letsencrypt/live/"${DOMAIN}"/cert.pem -inkey /etc/letsencrypt/live/"${DOMAIN}"/privkey.pem -out "${KEYSTORE_PATH}" \
       -name tomcat -CAfile /etc/letsencrypt/live/"${DOMAIN}"/chain.pem -caname root -passout pass:"$password"