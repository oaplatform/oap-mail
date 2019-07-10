## Configuration for application.conf

    oap-mail-password-authenticator.parameters.username = login
    oap-mail-password-authenticator.parameters.password = password
    oap-mail-smtp-transport.host = smtp.gmail.com
    oap-mail-smtp-transport.port = 587
    oap-mail-smtp-transport.tls = true
    
## Mocking

To disable message sending just turn of scheduling

    oap-mail-mailman.supervision.schedule = false
    
## Persistence

    oap-mail-queue.parameters.location = /where/to/store
   
    
