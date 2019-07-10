package oap.mail;

import oap.json.Binder;
import oap.reflect.TypeRef;
import oap.util.Stream;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

public class MailQueue {

    private ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();
    private final Path location;

    public MailQueue( Path location ) {
        this.location = location.resolve( "mail.gz" );
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
        queue.addAll( Binder.json.unmarshal( location, new TypeRef<>() {} ) );
    }

    public List<Message> messages() {
        return Stream.of( queue.stream() ).toList();
    }
}
