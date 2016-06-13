@@ -0,0 +1,48 @@
# Data Vault installation guide (Debian/Ubuntu)

## Before you start
```
# Make sure your packages are up to date
sudo apt-get update
```

## Prerequisites

### Java (1.7)
```
sudo apt-get install openjdk-7-jdk
```

### Maven
```
sudo apt-get install maven
```

### Git
```
sudo apt-get install git
```

### MySQL
```
# Install package
sudo apt-get install mysql-server

# Set a root password when prompted by the installer.
```

### Configure MySQL database
```
# Start MySQL shell session
mysql -i -u root -p

# Create a new database user and database
# In this example the password is 'datavault' and MySQL is running on the same machine as the broker
CREATE USER 'datavault'@'localhost' IDENTIFIED BY 'datavault';
GRANT ALL PRIVILEGES ON *.* TO 'datavault'@'localhost';
CREATE DATABASE datavault;
EXIT;
```

### RabbitMQ
```
# Add the RabbitMQ repository and signing key to the package manager
# See: https://www.rabbitmq.com/install-debian.html
echo 'deb http://www.rabbitmq.com/debian/ testing main' | sudo tee /etc/apt/sources.list.d/rabbitmq.list
wget -O- https://www.rabbitmq.com/rabbitmq-release-signing-key.asc | sudo apt-key add -
sudo apt-get update

# Install packages
sudo apt-get install rabbitmq-server

# Configure RabbitMQ users
# In this example the password is 'datavault'
sudo rabbitmqctl add_user datavault datavault
sudo rabbitmqctl set_user_tags datavault administrator
sudo rabbitmqctl set_permissions -p / datavault ".*" ".*" ".*"
```

### Apache & Tomcat
```
# Install packages
sudo apt-get install tomcat7-admin
sudo apt-get install tomcat7

# Tomcat will start automatically - you can also start it using the following command:
sudo /etc/init.d/tomcat7 start

# Ensure that port 8080 is open your firewall.
# You should now be able to navigate to http://my-server:8080 and see the default tomcat welcome page.
```

## Data Vault

### Download and build the Data Vault code
```
git clone https://github.com/DataVault/datavault.git
cd datavault
export MAVEN_OPTS="-Xmx1024m"
mvn package
```

### Configure the Data Vault home directory
```
# Copy the generated datavault home directory
cd ~/datavault
sudo cp -R datavault-assembly/target/datavault-assembly-1.0-SNAPSHOT-assembly/datavault-home /opt/datavault

# Change ownership of the home directory
sudo chown -R www-data /opt/datavault

# Set permissions on the logging directory
sudo chmod 777 /opt/datavault/logs

# Set the $DATAVAULT_HOME environment variable (for the current shell user)
nano ~/.profile
# Add the following line to your ~/.profile
# export DATAVAULT_HOME=/opt/datavault

# Reload the modified .profile file and check the new environment variable
source ~/.profile
echo $DATAVAULT_HOME

# Set the $DATAVAULT_HOME environment variable (for tomcat)
sudo nano /usr/share/tomcat7/bin/setenv.sh
# Add the following line to /usr/share/tomcat7/bin/setenv.sh
# export DATAVAULT_HOME=/opt/datavault

# If you created /usr/share/tomcat7/bin/setenv.sh make sure tomcat has read/execute permissions on the new file
sudo chmod 755 /usr/share/tomcat7/bin/setenv.sh
```

### Configuration (datavault.properties)
```
# Edit the $DATAVAULT_HOME/config/datavault.properties file
sudo nano $DATAVAULT_HOME/config/datavault.properties
```

| Name | Description |
| ------------- | ------------- |
| `brokerURL`  | The URL of the broker API for use by the default web application (localhost in this simple example). |
| `metadataURL` | The URL of the external metadata service (for example, a Pure CRIS API endpoint). Leave this blank to use a mock provider. <br> - **work in progress** |
| `dbURL` | The location of the MySQL database (localhost in this simple example). |
| `dbUsername` | The MySQL username - should match the created MySQL user. |
| `dbPassword` | The MySQL password - should match the created MySQL user. |
| `queueServer` | The location of the RabbitMQ server (localhost in this simple example). |
| `queueName` | The name of the primary RabbitMQ queue for communication between the broker and workers. |
| `eventQueueName` | The name of the RabbitMQ queue to use for notifying the broker of events. |
| `queueUser` | The RabbitMQ username - should match the created RabbitMQ user. |
| `queuePassword` | The RabbitMQ password - should match the created RabbitMQ user's password. |
| `numberOfWorkers` | The number of concurrent workers to start. |
| `activeDir` | A default directory for sample user data (if per-user storage is not configured). <br> - **for demonstration only** |
| `archiveDir` | Directory for archive data (if using 'local storage'). |
| `tempDir` | A temporary directory for workers to process files before storing in the archive. |
| `metaDir` | A directory for storing archive metadata. |
| `mail.administrator` | The email account of the system administrator. |
| `mail.host` | SMTP host for sending mail. |
| `mail.port` | SMTP port for sending mail. |
| `mail.username` | SMTP account name for sending mail. |
| `mail.password` | SMTP password for sending mail. |
| `webapp.welcome` | Welcome message (HTML) displayed by the (non-shibboleth) login page. |
| `broker.api.key` | API key for the default web client. <br> - **you should change this value** |
| `validateClient` | Debug setting to disable API authentication. |
| `describe.system` | Name displayed in the help page for the institutional CRIS or external metadata system. |
| `describe.link` | URL displayed in the help page for the institutional CRIS or external metadata system. |
| `retentioncheck.schedule` | How frequently the retention policy check job is run (in 'cron' format). |
| `host` | Default SFTP host. <br> - **work in progress** |
| `port` | Default SFTP port. <br> - **work in progress** |
| `rootPath` | Default SFTP path. <br> - **work in progress** |
| `passphrase` | Default SFTP key passphrase. <br> - **work in progress** |

### Deploy web applications
```
# Remove the default tomcat webapp
sudo rm -rf /var/lib/tomcat7/webapps/ROOT

# Deploy the generated war files to the tomcat directory
cd ~/datavault

# Deploy the broker (REST API)
# Deploying the broker application will cause Hibernate to construct the database tables and load some sample user data.
sudo cp datavault-broker/target/datavault-broker.war /var/lib/tomcat7/webapps

# Deploy the sample web application
sudo cp datavault-webapp/target/datavault-webapp.war /var/lib/tomcat7/webapps/ROOT.war

# Restart tomcat
sudo /etc/init.d/tomcat7 restart

# You should now be able to navigate to http://my-server:8080 and see the Data Vault login page.
# You can also to navigate to http://my-server:8080/datavault-broker/ and see the API documentation.
```

### Update web application API Key
```
# If you changed the api key for the default web application you'll also need to update it in the database.
# By default the broker API can only be accessed from the same machine as the broker.

# Start MySQL shell session
mysql -i -u root -p

# In this example the new API key is '123456'
USE datavault
UPDATE Clients SET apiKey = '123456' WHERE id = 'datavault-webapp';
EXIT;
```

### Start the worker processes
```
cd ~/datavault/datavault-worker/target
pkill -f datavault-worker
nohup java -cp datavault-worker-1.0-SNAPSHOT.jar:./* org.datavaultplatform.worker.WorkerManager > /dev/null 2>&1 &
```

### Troubleshooting

Check the various logfiles:
* $DATAVAULT_HOME/logs
* /var/log/tomcat7/catalina.out

Ensure that directories referenced in datavault.properties have been created and have the correct permissions:
* activeDir
* archiveDir
* tempDir
* metaDir

## Other considerations
* HTTPS (for both tomcat and RabbitMQ connections)
* Shibboleth authentication
* SFTP and storage configuration
* Customisation using spring configuration files
