# VCable - Virtual Cable

Virtual Cable is a IoT platform for bridging the Ethernet Port of Raspberry PIs to create a virtual network link between two device.

For bridging we are using [OpenVPN](https://www.openvpn.org) and 
[Bridge Control](https://openvpn.net/index.php/open-source/documentation/miscellaneous/76-ethernet-bridging.html).

# Testing


1) Virtualbox
For Testing you can use Ubuntu Minimal in a Virtual Box Environment, if you don't run on Linux. 
- https://help.ubuntu.com/community/Installation/MinimalCD

2) Add Packages 
On Ubuntu install the OpenSSH Server and Python (as we will use Ansible in a second step.)
- apt-get install python
- apt-get install openssh-server

3) Setup Portforwarding
Setup access of the Guest OS using Nat and Port-Forwarding.
- https://stackoverflow.com/questions/5906441/how-to-ssh-to-a-virtualbox-guest-externally-through-a-host

4) Setup the Host using Ansible

```
ansible-playbook -i inventory.yml --extra-vars "ansible_user=<SSH_USER> ansible_password=<SSH_PWD> ansible_become_pass=<ROOT_PWD>" ci.yml -vvvv
```

If you are on OSX you will require ```sshpass``` which is not part of the brew universe. You can find the sources here. 
- https://gist.github.com/arunoda/7790979