## Configuration for application.conf

    oap-mail-password-authenticator.parameters.username = login
    oap-mail-password-authenticator.parameters.password = password
    oap-mail-smtp-transport.parameters.host = smtp.gmail.com
    oap-mail-smtp-transport.parameters.port = 587
    oap-mail-smtp-transport.parameters.tls = true
    
## Mocking

To disable message sending just turn off scheduling

    oap-mail-mailman.supervision.schedule = false
    
## Persistence

    oap-mail-queue.parameters.location = /where/to/store
   
    
