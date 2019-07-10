package oap.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class PasswordAuthenticator extends Authenticator {
    public final String username;
    public final String password;

    public PasswordAuthenticator( String username, String password ) {
        this.username = username;
        this.password = password;
    }

    public PasswordAuthenticator( URL config ) {
        try( InputStream stream = config.openStream() ) {
            Properties properties = new Properties();
            properties.load( stream );
            this.username = properties.getProperty( "username" );
            this.password = properties.getProperty( "password" );
        } catch( IOException e ) {
            throw new IllegalArgumentException( e );
        }
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication( username, password );
    }
}
