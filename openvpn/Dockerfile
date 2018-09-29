FROM kylemanna/openvpn
COPY entrypoint.sh /usr/local/bin
RUN chmod a+x /usr/local/bin/entrypoint.sh

COPY ./openvpn.conf /etc/openvpn.new

# Expose the management UI Port
EXPOSE 7505

# Overwrite the existing config with config provided
ENTRYPOINT ["entrypoint.sh"]

CMD ["ovpn_run"]