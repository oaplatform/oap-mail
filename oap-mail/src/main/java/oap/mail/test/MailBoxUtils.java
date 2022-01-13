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

package oap.mail.test;

import lombok.SneakyThrows;
import oap.mail.MailAddress;
import oap.mail.Message;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FlagTerm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class MailBoxUtils {
    public static List<Message> getMessagesFromBox( Folder inbox ) {
        List<Message> messages = new ArrayList<>();
        try {
            javax.mail.Message[] inboxMessages = inbox.search(
                new FlagTerm( new Flags( Flags.Flag.SEEN ), false ) );

            // Sort messages from recent to oldest
            Arrays.sort( inboxMessages, ( m1, m2 ) -> {
                try {
                    return m2.getSentDate().compareTo( m1.getSentDate() );
                } catch( MessagingException e ) {
                    throw new RuntimeException( e );
                }
            } );

            for( javax.mail.Message m : inboxMessages ) {
                messages.add( convertMessage( m ) );
            }
            return messages;
        } catch( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public static Message getMessageFromBox( Folder inbox ) {
        try {
            // get last sent message
            javax.mail.Message inboxMessage = inbox.getMessage( inbox.getMessageCount() );
            return convertMessage( inboxMessage );
        } catch( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    @SneakyThrows
    public static Message convertMessage( javax.mail.Message source ) {
        Message target = new Message( source.getSubject(), source.getContent().toString().trim(), null );
        target.from = MailAddress.of( ( InternetAddress ) source.getFrom()[0] );
        target.to.addAll( MailAddress.of( ( InternetAddress[] ) source.getRecipients( javax.mail.Message.RecipientType.TO ) ) );
        target.cc.addAll( MailAddress.of( ( InternetAddress[] ) source.getRecipients( javax.mail.Message.RecipientType.CC ) ) );
        target.bcc.addAll( MailAddress.of( ( InternetAddress[] ) source.getRecipients( javax.mail.Message.RecipientType.BCC ) ) );
        return target;
    }


    public static Folder connectToInbox( String mail, String password ) {
        Properties properties = new Properties();

        properties.put( "mail.imap.port", "993" );
        properties.put( "mail.imap.host", "imap.gmail.com" );
        properties.put( "mail.imap.ssl.trust", "imap.gmail.com" );
        properties.put( "mail.imap.ssl.protocols", "TLSv1.2" );
        properties.put( "mail.imap.starttls.enable", "true" );
        properties.put( "mail.imap.starttls.required", "true" );

        Session emailSession = Session.getDefaultInstance( properties );
        Folder inbox = null;

        try {
            // create the imap store object and connect to the imap server
            Store store = emailSession.getStore( "imaps" );

            store.connect( "imap.gmail.com", mail, password );

            // create the inbox object and open it
            inbox = store.getFolder( "Inbox" );
            inbox.open( Folder.READ_WRITE );
            return inbox;
        } catch( MessagingException e ) {
            throw new RuntimeException( e );
        }
    }
}
