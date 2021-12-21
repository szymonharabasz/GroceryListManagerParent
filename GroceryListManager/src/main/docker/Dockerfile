FROM zulu8-jdk.arch64

ENV HOME_DIR=/opt/payara
ENV PAYARA_DIR=${HOME_DIR}/appserver \
    SCRIPT_DIR=${HOME_DIR}/scripts \
    CONFIG_DIR=${HOME_DIR}/config \
    DEPLOY_DIR=${HOME_DIR}/deployments \
    PASSWORD_FILE=${HOME_DIR}/passwordFile \
    ADMIN_USER=admin \
    ADMIN_PASSWORD=admin \
    JVM_ARGS="" \
    MEM_MAX_RAM_PERCENTAGE="70.0" \
    MEM_XSS="512k"
ENV PATH="${PATH}:${PAYARA_DIR}/bin"

RUN true \
    && apt-get update \
    && apt-get install -y gpg wget \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir -p ${HOME_DIR} \
    && addgroup --gid 1000 payara \
    && adduser --system --uid 1000 --no-create-home --shell /bin/bash --home "${HOME_DIR}" --gecos "" --ingroup payara payara \
    && echo payara:payara | chpasswd \
    && mkdir -p ${DEPLOY_DIR} \
    && mkdir -p ${CONFIG_DIR} \
    && mkdir -p ${SCRIPT_DIR} \
    && chown -R payara:payara ${HOME_DIR} \
    && true

USER payara
WORKDIR ${HOME_DIR}

# Default payara ports to expose
# 4848: admin console
# 9009: debug port (JPDA)
# 8080: http
# 8181: https
EXPOSE 4848 9009 8080 8181

ENV DOMAIN_NAME=domain1 \
    PAYARA_ARGS="" \
    PREBOOT_COMMANDS=${CONFIG_DIR}/pre-boot-commands.asadmin \
    PREBOOT_COMMANDS_FINAL=${CONFIG_DIR}/pre-boot-commands-final.asadmin \
    POSTBOOT_COMMANDS=${CONFIG_DIR}/post-boot-commands.asadmin \
    POSTBOOT_COMMANDS_FINAL=${CONFIG_DIR}/post-boot-commands-final.asadmin \
    DEPLOY_PROPS=""

COPY --chown=payara:payara --chmod=755 bin/* ${SCRIPT_DIR}/
ARG PAYARA_VERSION=5.2021.8
ARG PAYARA_PKG=https://search.maven.org/remotecontent?filepath=fish/payara/distributions/payara/${PAYARA_VERSION}/payara-${PAYARA_VERSION}.zip
ARG PAYARA_SHA1=7c2280c765e03bd9d459ac5b8f3059927507683f

# Download and unzip the Payara distribution
RUN wget --no-verbose -O payara.zip ${PAYARA_PKG}
RUN echo "${PAYARA_SHA1} *payara.zip" | sha1sum -c -
RUN unzip -qq payara.zip -d ./
RUN mv payara*/ ${PAYARA_DIR}

RUN ls -lhtr ${PAYARA_DIR}
RUN ls -lhtr ${PAYARA_DIR}/bin
RUN ls -lhtr ${PAYARA_DIR}/bin/asadmin

COPY --chown=payara:payara --chmod=777 system-properties ${WORKDIR}
RUN true \
    && echo "AS_ADMIN_PASSWORD=\nAS_ADMIN_NEWPASSWORD=${ADMIN_PASSWORD}" > /tmp/password-change-file.txt \
    && echo "AS_ADMIN_PASSWORD=${ADMIN_PASSWORD}" >> ${PASSWORD_FILE} \
    && ${PAYARA_DIR}/bin/asadmin --user ${ADMIN_USER} --passwordfile=/tmp/password-change-file.txt \
    change-admin-password --domain_name=${DOMAIN_NAME} \
    && ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} start-domain ${DOMAIN_NAME} \
    && ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} enable-secure-admin \
    && for MEMORY_JVM_OPTION in \
       $(${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} list-jvm-options | \grep "Xm[sx]\|Xss"); \
       do \
         ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} delete-jvm-options $MEMORY_JVM_OPTION; \
       done \
    && ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jvm-options \
      '-XX\:+UseContainerSupport:-XX\:MaxRAMPercentage=${ENV=MEM_MAX_RAM_PERCENTAGE}:-Xss${ENV=MEM_XSS}' \
    && ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} \
      set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.logtoFile=false \
    && ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-system-properties `cat system-properties` \
    && ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} \
      set configs.config.server-config.network-config.protocols.protocol.http-listener-1.http.xpowered-by=false \
    && ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} \
      set configs.config.server-config.network-config.protocols.protocol.http-listener-2.http.xpowered-by=false \
    && ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} \
      set configs.config.server-config.network-config.protocols.protocol.http-listener-1.http.server-header=false \
    && ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} \
      set configs.config.server-config.network-config.protocols.protocol.http-listener-2.http.server-header=false \
    && ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} stop-domain ${DOMAIN_NAME}

RUN rm -rf \
        ${WORKDIR}/system-properties \
        /tmp/password-change-file.txt \
        ${PAYARA_DIR}/glassfish/domains/${DOMAIN_NAME}/osgi-cache \
        ${PAYARA_DIR}/glassfish/domains/${DOMAIN_NAME}/logs
RUN true

COPY tini-static /

ENTRYPOINT ["/tini-static", "--"]
CMD "${SCRIPT_DIR}/entrypoint.sh"
