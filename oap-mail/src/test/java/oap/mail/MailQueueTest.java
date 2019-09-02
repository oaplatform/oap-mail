package oap.mail;

import oap.testng.Env;
import oap.testng.Fixtures;
import oap.testng.TestDirectory;
import oap.util.Lists;
import org.testng.annotations.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class MailQueueTest extends Fixtures {
    {
        fixture( TestDirectory.FIXTURE );
    }

    @Test
    public void persist() {
        var location = Env.tmpPath( "queue" );
        MailQueue queue = prepareQueue( location );
        queue.processing( m -> false );
        assertThat( location.resolve( "mail.gz" ) ).exists();
        var queue2 = new MailQueue( location );
        assertThat( queue2.messages() ).hasSize( 3 );

        queue2.processing( m -> true );
        assertThat( queue2.messages() ).isEmpty();
        var queue3 = new MailQueue( location );
        assertThat( queue3.messages() ).isEmpty();
    }

    @Test
    public void persistWithNullLocation() {
        MailQueue queue = prepareQueue( null );
        queue.processing( m -> false );
    }

    private MailQueue prepareQueue( Path location ) {
        var queue = new MailQueue( location );
        queue.add( new Message( "subj1", "body", Lists.empty() ) );
        queue.add( new Message( "subj2", "body", Lists.empty() ) );
        queue.add( new Message( "subj3", "body", Lists.empty() ) );
        return queue;
    }

}