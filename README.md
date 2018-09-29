# VCable - Virtual Cable

Virtual Cable is a IoT platform for bridging the Ethernet Port of Raspberry PIs to create a virtual network link between two device.

For bridging we are using [OpenVPN](https://www.openvpn.org) and 
[Bridge Control](https://openvpn.net/index.php/open-source/documentation/miscellaneous/76-ethernet-bridging.html).

The Source Code is from 2012 and will be refurbished to fit new standards. We are gradually adding more code to this repo.

Binaries are available on Maven Central

## Running OpenVPN

For Test Purpose we use this [docker image](https://hub.docker.com/r/kylemanna/openvpn/) ([git](https://github.com/kylemanna/docker-openvpn)).

In order to run the container with the enabled management interface, we add a custom config 

```
docker run -v $OVPN_DATA:/etc/openvpn --log-driver=none --rm vcable/openvpn ovpn_genconfig -u udp://VPN.VCABLE.ORG
docker run -v $OVPN_DATA:/etc/openvpn --log-driver=none --rm -it vcable/openvpn ovpn_initpki
```

