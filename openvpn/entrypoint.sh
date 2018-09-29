#!/usr/bin/env bash
# We need to overwrite the openvpn.conf on startup.
set -e

cat /etc/openvpn.new > /etc/openvpn/openvpn.conf

echo "$@"

exec "$@"