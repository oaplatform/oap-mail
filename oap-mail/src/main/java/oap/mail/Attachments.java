package oap.mail;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.QCodec;

import javax.activation.FileDataSource;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

@Slf4j
public class Attachments {
    static String makeMimeType( Attachment attachment ) throws MessagingException {
        String name = attachment.getName();
        if( name == null ) {
            // try to construct the name from filename
            name = attachment.getFile();
            if( name != null ) {
                int p = name.lastIndexOf( '/' );
                if( p < 0 ) p = name.lastIndexOf( '\\' );
                if( p >= 0 ) name = name.substring( p + 1 );
            }
        }
        try {
            MimeType mt = new MimeType( attachment.getContentType() );
            if( name != null ) {
                String encoded = name;
                try {
                    encoded = new QCodec().encode( name, "UTF-8" );
                    String sub = encoded.substring( 10, encoded.length() - 2 );
                    if( sub.equals( name ) ) encoded = name;
                } catch( EncoderException e ) {
                    log.warn( "encoging error for: " + name, e );
                }
                mt.setParameter( "name", encoded );
            }
            if( attachment.getFile() == null ) mt.setParameter( "charset", "UTF-8" );
            return mt.toString();
        } catch( MimeTypeParseException e ) {
            throw new MessagingException( "bad content type", e );
        }
    }

    @Slf4j
    static class HtmlMimeMultipart extends MimeMultipart {

        HtmlMimeMultipart() {
            try {
                this.setSubType( "related" );
            } catch( MessagingException e ) {
                log.warn( e.toString(), e );
            }
        }

        public String getContentType() {
            try {
                MimeType mt = new MimeType( super.getContentType() );
                mt.setParameter( "type", "text/html" );
                return mt.toString();
            } catch( MimeTypeParseException e ) {
                log.warn( e.toString(), e );
                return super.getContentType();
            }
        }
    }

    static class MimeFileDataSource extends FileDataSource {
        String mimeType;

        MimeFileDataSource( Attachment attachment ) throws MessagingException {
            super( attachment.getFile() );
            mimeType = makeMimeType( attachment );
        }

        public String getContentType() {
            return mimeType != null ? mimeType : super.getContentType();
        }
    }
}
