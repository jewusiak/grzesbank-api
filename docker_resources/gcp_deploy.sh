#!/bin/zsh

docker build -t us-west1-docker.pkg.dev/grzesbank-pamiw/gb24-repo/grzesbank-api:latest .
docker push us-west1-docker.pkg.dev/grzesbank-pamiw/gb24-repo/grzesbank-api:latest
gcloud run deploy grzesbank-api --image us-west1-docker.pkg.dev/grzesbank-pamiw/gb24-repo/grzesbank-api:latest --region us-west1 --allow-unauthenticated