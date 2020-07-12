FROM jupyter/datascience-notebook:latest
# 1386e2046833

ARG PHP_VERSION=7.4

USER root
RUN apt-get update  && apt-get upgrade -y \
 && apt-get install -y pass gnupg libdbus-glib-1-dev php${PHP_VERSION} php${PHP_VERSION}-dev libzmq3-dev php${PHP_VERSION}-mysql \
 && php -r "copy('https://getcomposer.org/installer', 'composer-setup.php');" \
 && php composer-setup.php \
 && php -r "unlink('composer-setup.php');" \
 && mv composer.phar /usr/local/bin/composer \
 && git clone git://github.com/mkoppanen/php-zmq.git php-zmq \
 && cd php-zmq \
 && phpize \
 && ./configure \
 && make \
 && make install \
 && echo "extension=zmq.so" > /etc/php/${PHP_VERSION}/mods-available/zmq.ini \
 && ln -sf /etc/php/7.4/mods-available/zmq.ini /etc/php/${PHP_VERSION}/cli/conf.d/20-zmq.ini
RUN cd .. \
 && rm -rf php-zmq
RUN conda update -n base conda
RUN conda config --add channels conda-forge \
 && conda install -y sqlalchemy psycopg2 pymysql boto3 elasticsearch qgrid
RUN pip install npm nodejs postgres_kernel git+https://github.com/shemic/jupyter-mysql-kernel
RUN python3 -m pip install --no-cache \
        ipython \
        jupyterhub \
        jupyterlab \
        pycrypto \
        pycryptodome \
        pgcontents \
        dash \
        dash-daq \
        chart_studio \
        oauth2client \
        oauthenticator \
        boto3 \
        jupyters3 \
        sshtunnel \
        keyring \
        keyrings.cryptfile \
        ipython-secrets \
        jupyterlab_latex \
        jupyterlab_github \
 && jupyter serverextension enable --py jupyterlab
RUN wget https://litipk.github.io/Jupyter-PHP-Installer/dist/jupyter-php-installer.phar \
 && chmod +x ./jupyter-php-installer.phar \
 && ./jupyter-php-installer.phar -vvv install \
 && rm -f ./jupyter-php-installer.phar
RUN jupyter nbextension enable --py --sys-prefix widgetsnbextension \
 && jupyter labextension install @jupyter-widgets/jupyterlab-manager@2.0
# && jupyter labextension install @jupyterlab/github \
# && jupyter labextension install @jupyterlab/google-drive \
# && jupyter labextension install @jupyterlab/latex \
# && jupyter labextension install jupyterlab-spreadsheet
RUN jupyter lab build
USER jovyan

COPY jupyter_notebook_config.py /etc/jupyter/
