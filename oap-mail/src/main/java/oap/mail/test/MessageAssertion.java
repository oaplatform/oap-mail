package oap.mail.test;

import lombok.SneakyThrows;
import oap.mail.MailAddress;
import oap.mail.Message;
import org.assertj.core.api.AbstractAssert;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import java.util.Map;
import java.util.Properties;

import static oap.testng.Asserts.assertString;
import static oap.testng.Asserts.contentOfTestResource;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public final class MessageAssertion extends AbstractAssert<MessageAssertion, Message> {
    private MessageAssertion( Message message ) {
        super( message, MessageAssertion.class );
    }

    private static Message getMessageFromBox( Folder inbox ) {
        Message message;
        try {
            javax.mail.Message inboxMessage = inbox.getMessage( inbox.getMessageCount() );
            message = new Message( inboxMessage.getSubject(), inboxMessage.getContent().toString().trim(), null );
            message.from = MailAddress.of( ( InternetAddress ) inboxMessage.getFrom()[0] );
            message.to.addAll( MailAddress.of( ( InternetAddress[] ) inboxMessage.getRecipients( javax.mail.Message.RecipientType.TO ) ) );
            message.cc.addAll( MailAddress.of( ( InternetAddress[] ) inboxMessage.getRecipients( javax.mail.Message.RecipientType.CC ) ) );
            message.bcc.addAll( MailAddress.of( ( InternetAddress[] ) inboxMessage.getRecipients( javax.mail.Message.RecipientType.BCC ) ) );
        } catch( Exception e ) {
            throw new RuntimeException( e );
        }
        return message;
    }

    private static Folder connectToInbox( String mail, String password ) {
        Properties properties = new Properties();

        properties.put( "mail.imap.port", "993" );
        properties.put( "mail.imap.host", "imap.gmail.com" );
        properties.put( "mail.imap.ssl.trust", "imap.gmail.com" );
        properties.put( "mail.imap.ssl.protocols", "TLSv1.2" );
        properties.put( "mail.imap.starttls.enable", "true" );
        properties.put( "mail.imap.starttls.required", "true" );

        Session emailSession = Session.getDefaultInstance( properties );
        Folder inbox = null;

        try {
            // create the imap store object and connect to the imap server
            Store store = emailSession.getStore( "imaps" );

            store.connect( "imap.gmail.com", mail, password );

            // create the inbox object and open it
            inbox = store.getFolder( "Inbox" );
            inbox.open( Folder.READ_WRITE );
            return inbox;
        } catch( MessagingException e ) {
            throw new RuntimeException( e );
        }
    }

    public static MessageAssertion assertMessage( Message message ) {
        return new MessageAssertion( message );
    }

    @SneakyThrows
    public static MessageAssertion assertThatInboxHasMessage( String mail, String password ) {
        Folder inbox = connectToInbox( mail, password );
        Message message = getMessageFromBox( inbox );
        inbox.close();
        inbox.getStore().close();

        return new MessageAssertion( message );
    }

    public MessageAssertion isFrom( String email ) {
        assertString( this.actual.from.mail ).isEqualTo( email );
        return this;
    }

    public MessageAssertion isSentTo( String... emails ) {
        assertThat( this.actual.to )
            .extracting( ma -> ma.mail )
            .contains( emails );
        return this;
    }

    public MessageAssertion isCopiedTo( String... emails ) {
        assertThat( this.actual.cc )
            .extracting( ma -> ma.mail )
            .contains( emails );
        return this;
    }

    public MessageAssertion isBlindlyCopiedTo( String... emails ) {
        assertThat( this.actual.bcc )
            .extracting( ma -> ma.mail )
            .contains( emails );
        return this;
    }

    public MessageAssertion hasSubject( String subject ) {
        assertString( this.actual.subject ).isEqualTo( subject );
        return this;
    }

    public MessageAssertion hasContentType( String contentType ) {
        assertThat( this.actual.contentType ).isEqualTo( contentType );
        return this;
    }

    public MessageAssertion hasBody( String body ) {
        assertString( this.actual.body ).isEqualTo( body );
        return this;
    }

    public MessageAssertion hasBody( Class<?> contextClass, String resource ) {
        return hasBody( contextClass, resource, Map.of() );
    }

    public MessageAssertion hasBody( Class<?> contextClass, String resource, Map<String, Object> substitutions ) {
        return hasBody( contentOfTestResource( contextClass, resource, substitutions ) );
    }
}
