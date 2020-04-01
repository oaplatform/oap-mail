package oap.mail.test;

import oap.mail.Message;
import oap.util.Lists;
import oap.util.Stream;
import org.assertj.core.api.AbstractIterableAssert;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.fail;

public final class MessageAssertions extends AbstractIterableAssert<MessageAssertions, Iterable<? extends Message>, Message, MessageAssertion> {

    private MessageAssertions( Iterable<? extends Message> messages ) {
        super( messages, MessageAssertions.class );
    }

    public static MessageAssertions assertMessages( Iterable<? extends Message> messages ) {
        return new MessageAssertions( messages );
    }

    public MessageAssertions sentTo( String to, Consumer<Message> assertion ) {
        return by( m -> Lists.contains( m.to, ma -> ma.mail.equals( to ) ),
            assertion, "can't find message sent to " + to );
    }

    public MessageAssertions by( Predicate<Message> predicate, Consumer<Message> assertion ) {
        return by( predicate, assertion, "can't find message" );
    }

    public MessageAssertions by( Predicate<Message> predicate, Consumer<Message> assertion, String failureMessage ) {
        Stream.of( this.actual.iterator() )
            .filter( predicate )
            .findAny()
            .ifPresentOrElse( assertion, fail( failureMessage ) );
        return this;
    }

    @Override
    protected MessageAssertion toAssert( Message message, String s ) {
        return MessageAssertion.assertMessage( message );
    }

    @Override
    protected MessageAssertions newAbstractIterableAssert( Iterable<? extends Message> iterable ) {
        return assertMessages( iterable );
    }
}
