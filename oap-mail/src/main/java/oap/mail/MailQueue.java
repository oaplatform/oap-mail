package oap.mail;

import oap.json.Binder;
import oap.reflect.TypeRef;
import oap.storage.Metadata;
import oap.util.Lists;
import oap.util.Stream;

import javax.mail.MessageAware;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

public class MailQueue {

    private ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();
    private Path location;

    public MailQueue( Path location ) {
        if( location != null ) {
            this.location = location.resolve( "mail.gz" );
        }
        load();
    }

    public MailQueue() {
        this( null );
    }

    public void add( Message message ) {
        queue.add( message );
    }

    public void processing( Predicate<Message> processor ) {
        queue.removeIf( processor::test );
        persist();
    }

    private void persist() {
        if( location != null ) Binder.json.marshal( location, this.queue );
    }

    private void load() {
        if( location != null ) queue.addAll( Binder.json.unmarshal( new TypeRef<List<Message>>() {}, location ) );
    }

    public List<Message> messages() {
        return Stream.of( queue.stream() ).toList();
    }
}
