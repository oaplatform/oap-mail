package oap.mail.test;

import oap.mail.Message;
import oap.util.Lists;
import oap.util.Stream;
import org.assertj.core.api.AbstractIterableAssert;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.fail;

public final class MessagesAssertion extends AbstractIterableAssert<MessagesAssertion, Iterable<? extends Message>, Message, MessageAssertion> {

    private MessagesAssertion( Iterable<? extends Message> messages ) {
        super( messages, MessagesAssertion.class );
    }

    public static MessagesAssertion assertMessages( Iterable<? extends Message> messages ) {
        return new MessagesAssertion( messages );
    }

    public MessagesAssertion sentTo( String to, Consumer<Message> assertion ) {
        return by( m -> Lists.contains( m.to, ma -> ma.mail.equals( to ) ),
            assertion, "can't find message sent to " + to );
    }

    public MessagesAssertion by( Predicate<Message> predicate, Consumer<Message> assertion ) {
        return by( predicate, assertion, "can't find message" );
    }

    public MessagesAssertion by( Predicate<Message> predicate, Consumer<Message> assertion, String failureMessage ) {
        Stream.of( this.actual.iterator() )
            .filter( predicate )
            .findAny()
            .ifPresentOrElse( assertion, () -> fail( failureMessage ) );
        return this;
    }

    @Override
    protected MessageAssertion toAssert( Message message, String s ) {
        return MessageAssertion.assertMessage( message );
    }

    @Override
    protected MessagesAssertion newAbstractIterableAssert( Iterable<? extends Message> iterable ) {
        return assertMessages( iterable );
    }
}
