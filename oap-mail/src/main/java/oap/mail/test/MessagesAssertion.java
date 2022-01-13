package oap.mail.test;

import oap.mail.Message;
import oap.util.Lists;
import oap.util.Stream;
import org.assertj.core.api.AbstractIterableAssert;

import javax.annotation.Nonnull;
import javax.mail.Folder;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.fail;

public final class MessagesAssertion extends AbstractIterableAssert<MessagesAssertion, Iterable<? extends Message>, Message, MessageAssertion> {

    private MessagesAssertion( @Nonnull Iterable<? extends Message> messages ) {
        super( messages, MessagesAssertion.class );
    }

    @Nonnull
    public static MessagesAssertion assertMessages( @Nonnull Iterable<? extends Message> messages ) {
        return new MessagesAssertion( messages );
    }

    @Nonnull
    public static MessagesAssertion assertMessagesSentInTheBox( String user, String password ) {
        Folder inbox = MailBoxUtils.connectToInbox( user, password );
        List<Message> messages = MailBoxUtils.getMessagesFromBox( inbox );
        return new MessagesAssertion( messages );
    }

    @Nonnull
    public MessagesAssertion sentTo( @Nonnull String to, @Nonnull Consumer<Message> assertion ) {
        return by( m -> Lists.contains( m.to, ma -> ma.mail.equals( to ) ),
            assertion, "can't find message sent to " + to );
    }

    @Nonnull
    public MessagesAssertion bySubject( @Nonnull String subject, @Nonnull Consumer<Message> assertion ) {
        return by( m -> subject.equals( m.subject ),
            assertion, "can't find message with subject " + subject );
    }

    @Nonnull
    public MessagesAssertion by( @Nonnull Predicate<Message> predicate, @Nonnull Consumer<Message> assertion ) {
        return by( predicate, assertion, "can't find message" );
    }

    @Nonnull
    public MessagesAssertion by( @Nonnull Predicate<Message> predicate, @Nonnull Consumer<Message> assertion, @Nonnull String failureMessage ) {
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
