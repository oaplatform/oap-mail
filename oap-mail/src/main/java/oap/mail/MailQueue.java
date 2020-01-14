/*
 * The MIT License (MIT)
 *
 * Copyright (c) Open Application Platform Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package oap.mail;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;
import oap.json.Binder;
import oap.reflect.TypeRef;
import oap.util.Lists;
import oap.util.Stream;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

@Slf4j
public class MailQueue {
    private final ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();
    private Path location;

    public MailQueue( Path location ) {
        if( location != null ) {
            this.location = location.resolve( "mail.gz" );
            load();
        }

        Metrics.gaugeCollectionSize( "mail_queue", Tags.empty(), queue );
    }

    public MailQueue() {
        this( null );
    }

    public void add( Message message ) {
        queue.add( message );
    }

    public void processing( Predicate<Message> processor ) {
        queue.removeIf( processor );
        persist();
    }

    private void persist() {
        if( location != null ) Binder.json.marshal( location, this.queue );
    }

    private void load() {
        log.debug( "loading queue..." );
        queue.addAll( Binder.json.unmarshal( new TypeRef<List<Message>>() {}, location ).orElse( Lists.empty() ) );
        log.debug( "{} messages loaded", size() );
    }

    public List<Message> messages() {
        return Stream.of( queue.stream() ).toList();
    }

    public int size() {
        return queue.size();
    }
}
