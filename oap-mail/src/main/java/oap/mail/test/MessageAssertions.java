package oap.mail.test;

import oap.mail.Message;
import oap.util.Lists;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static oap.util.Lists.contains;
import static org.assertj.core.api.Assertions.fail;

public final class MessageAssertions {
    private final Collection<Message> actual;

    private MessageAssertions( Collection<Message> messages ) {
        this.actual = messages;
    }

    public MessageAssertions sentTo( String to, Consumer<Message> assertion ) {
        return by( m -> contains( m.to, ma -> ma.mail.equals( to ) ),
            assertion, "can't find message sent to " + to );
    }

    public MessageAssertions by( Predicate<Message> predicate, Consumer<Message> assertion ) {
        return by( predicate, assertion, "can't find message" );
    }

    public MessageAssertions by( Predicate<Message> predicate, Consumer<Message> assertion, String failureMessage ) {
        Lists.find( this.actual, predicate )
            .ifPresentOrElse( assertion, fail( failureMessage ) );
        return this;
    }
}
