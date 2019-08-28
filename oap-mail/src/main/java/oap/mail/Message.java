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

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

@ToString
public class Message {
    private String subject;
    private String body;
    private ArrayList<Attachment> attachments = new ArrayList<Attachment>();
    private MailAddress from;
    private MailAddress[] to = new MailAddress[0];
    private MailAddress[] cc = new MailAddress[0];
    private MailAddress[] bcc = new MailAddress[0];
    private String contentType = "text/plain";

    @JsonCreator
    public Message( String subject, String body, List<Attachment> attachments ) {
        this.body = body;
        this.subject = subject;
        if( attachments != null ) this.attachments.addAll( attachments );
    }

    public Message() {
        this( null, null, emptyList() );
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject( String subject ) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody( String body ) {
        this.body = body;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public MailAddress getFrom() {
        return from;
    }

    public void setFrom( MailAddress from ) {
        this.from = from;
    }

    public MailAddress[] getCc() {
        return cc;
    }

    public void setCc( MailAddress... cc ) {
        this.cc = cc;
    }

    public MailAddress[] getBcc() {
        return bcc;
    }

    public void setBcc( MailAddress... bcc ) {
        this.bcc = bcc;
    }

    public MailAddress[] getTo() {
        return to;
    }

    public void setTo( MailAddress... to ) {
        this.to = to;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType( String contentType ) {
        this.contentType = contentType;
    }

    public void addAttachment( Attachment attachment ) {
        attachments.add( attachment );
    }

}
