#!/bin/bash
kubectl apply -f ./deploy/helm-chart/namespace.yaml

helm upgrade --install -f ./deploy/helm-chart/our-map-secrets/values-test.yaml our-map-secrets ./deploy/helm-chart/our-map-secrets
helm upgrade --install -f ./deploy/helm-chart/our-map-server/values-test.yaml our-map-server ./deploy/helm-chart/our-map-server

helm upgrade --install -f ./deploy/helm-chart/our-map-secrets/values-prod.yaml our-map-secrets ./deploy/helm-chart/our-map-secrets
helm upgrade --install -f ./deploy/helm-chart/our-map-server/values-prod.yaml our-map-server ./deploy/helm-chart/our-map-server
helm upgrade --install -f ./deploy/helm-chart/our-map-server-admin/values.yaml our-map-server-admin ./deploy/helm-chart/our-map-server-admin
helm upgrade --install -f ./deploy/helm-chart/our-map-frontend-admin/values.yaml our-map-frontend-admin ./deploy/helm-chart/our-map-frontend-admin
