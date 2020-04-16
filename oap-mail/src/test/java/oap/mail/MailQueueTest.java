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
