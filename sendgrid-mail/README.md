## OAP SendGrid
Add module sendgrid-mail to your dependsOn section in
 
oap-module.yaml.

Enable sendgrid profile in profiles = [] section at
the top of application.conf.

Go to https://app.sendgrid.com/settings/api_keys and click
"Create API key".

Place `ap-mail-smtp-transport.parameters.sendGridKey = [NEW KEY]`
to the application.conf.

