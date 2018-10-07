# VCable - Virtual Cable

Virtual Cable is a IoT platform for bridging the Ethernet Port of Raspberry PIs to create a virtual network link between two device.

For bridging we are using [OpenVPN](https://www.openvpn.org) and 
[Bridge Control](https://openvpn.net/index.php/open-source/documentation/miscellaneous/76-ethernet-bridging.html).

# Testing


1) Virtual Box
For Testing you can use Ubuntu Minimal in a Virtual Box Environment, if you don't run on Linux. 
- https://help.ubuntu.com/community/Installation/MinimalCD

2) Add Packages 
On Ubuntu install the OpenSSH Server and Python (as we will use Ansible in a second step.)
- apt-get install python
- apt-get install openssh-server

3) Setup Port forwarding
Setup access of the Guest OS using Nat and Port-Forwarding.
- https://stackoverflow.com/questions/5906441/how-to-ssh-to-a-virtualbox-guest-externally-through-a-host

4) Setup the Host using Ansible

```
ansible-playbook -i inventory.yml --extra-vars "ansible_user=<SSH_USER> ansible_password=<SSH_PWD> ansible_become_pass=<ROOT_PWD>" ci.yml -vvvv
```

If you are on OSX you will require ```sshpass``` which is not part of the brew universe. You can find the sources here. 
- https://gist.github.com/arunoda/7790979


# Setting up the OpenVPN Containers for Testing

OpenVPN as Container Image with enabled Management Console. 

For Test Purpose we use this [docker image](https://hub.docker.com/r/kylemanna/openvpn/) ([git](https://github.com/kylemanna/docker-openvpn)).

In order to run the container with the enabled management interface, we add a custom config, namely the management interface config. Using
no password for the Server CA is okay for testing purpose.

The ```vars``` file enables us to use batch mode, which does not require interaction during setup (as long as there are no previous) certs. 

```
docker run -v $OVPN_DATA:/etc/openvpn --rm kylemanna/openvpn ovpn_genconfig -u udp://localhost -e 'management 0.0.0.0 7505'
docker run -v $OVPN_DATA:/etc/openvpn -v /tmp/vars:/usr/local/bin/vars --rm -it kylemanna/openvpn ovpn_initpki nopass
```

Run the server container as daemon

```
docker run -d -v $OVPN_DATA:/etc/openvpn -p 1194:1194/udp -p 7505:7505 --cap-add=NET_ADMIN kylemanna/openvpn
```

Gen Keys for a client

```
docker run -v $OVPN_DATA:/etc/openvpn --rm -it kylemanna/openvpn easyrsa build-client-full client_1 nopass
```

Generate the Config for a client. 

```
docker run -v $OVPN_DATA:/etc/openvpn --rm kylemanna/openvpn ovpn_getclient client_1 > $OVPN_DATA/client_1.ovpn
```

Run the Client with this Config in a Container

```
docker run -v $OVPN_DATA:/etc/openvpn -v /dev/net/tun:/dev/net/tun --network=host --rm --cap-add=NET_ADMIN kylemanna/openvpn openvpn --config /etc/openvpn/client_1.ovpn
```
