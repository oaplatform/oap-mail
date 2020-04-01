package oap.mail.test;

import oap.mail.Message;

import java.util.Map;

import static oap.testng.Asserts.assertString;
import static oap.testng.Asserts.contentOfTestResource;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public final class MessageAssertion {
    private final Message actual;

    private MessageAssertion( Message message ) {
        this.actual = message;
    }

    public static MessageAssertion assertMessage( Message message ) {
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
