/*
 * Copyright (c) Vieworks
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 */

package oap.mail;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;

import javax.mail.Part;
import java.util.Arrays;

@Slf4j
public class SendGridTransport implements Transport {
    private final String sendGridKey;

    public SendGridTransport( String sendGridKey ) {
        this.sendGridKey = sendGridKey;
    }

    @Override
    public void send( Message message ) {
        Email from = new Email( message.getFrom().toString() );
        Content content = new Content( "text/html", message.getBody() );
        SendGrid sendGrid = new SendGrid( sendGridKey );
        Request request = new Request();
        request.setMethod( Method.POST );
        request.setEndpoint( "mail/send" );
        Arrays.stream( message.getTo() )
            .forEach( address -> {
                Email to = new Email( address.toString() );
                Mail mail = new Mail( from, message.getSubject(), to, content );
                message.getAttachments().stream().map( this::createAttachments )
                    .forEach( mail::addAttachments );
                try {
                    request.setBody( mail.build() );
                    sendGrid.api( request );
                } catch( Exception e ) {
                    log.error( String.format( "failed to send '%s'", message.getSubject() ), e );
                }
            } );
    }

    private Attachments createAttachments( oap.mail.Attachment oapAttachment ) {
        Attachments attachments = new Attachments();
        attachments.setContent( oapAttachment.getContent() );
        attachments.setContentId( oapAttachment.getContentId() );
        attachments.setDisposition( Part.ATTACHMENT );
        attachments.setFilename( oapAttachment.getFile() );
        attachments.setType( oapAttachment.getContentType() );

        return attachments;
    }
}
