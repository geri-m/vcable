#!/usr/bin/env bash
docker stop openvpn || true && docker rm openvpn || true
docker run --name openvpn -v $OVPN_DATA:/etc/openvpn -p 1194:1194/udp -p 7505:7505 --cap-add=NET_ADMIN vcable/openvpn