#!/usr/bin/env bash
docker rmi -f vcable/openvpn:latest
docker build -t vcable/openvpn:latest .