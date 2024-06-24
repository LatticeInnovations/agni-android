#!/bin/sh

# Decrypt the file
# mkdir $HOME/secrets
# --batch to prevent interactive command
# --yes to assume "yes" for questions
gpg --quiet --batch --yes --decrypt --passphrase="$GOOGLE_SERVICES_PASSPHRASE" \
--output ${PWD}/app/google-services.json ${PWD}/app/google-services.json.gpg
