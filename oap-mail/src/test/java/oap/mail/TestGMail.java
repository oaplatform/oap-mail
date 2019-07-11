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

import oap.io.Resources;

public class TestGMail {

    /**
     * put gmailauth.conf in test/resources. Dont worry it's in .gitignore:
     *
     * username=aaa@gmail.com
     * password=whatever
     *
     */
    @SuppressWarnings( "OptionalGetWithoutIsPresent" )
    public static void main( String[] args ) throws MailException {
        Resources.readProperties( TestGMail.class, "/gmailauth.conf" );
        Resources.url( TestGMail.class, "/gmailauth.conf" ).ifPresentOrElse( auth -> {
            PasswordAuthenticator authenticator = new PasswordAuthenticator( auth );
            SmtpTransport transport = new SmtpTransport( "smtp.gmail.com", 587, true, authenticator );
            Mailman mailman = new Mailman( transport, new MailQueue() );
            Template template = Template.of( "/xjapanese" ).get();
            template.bind( "logo",
                "https://assets.coingecko.com/coins/images/4552/small/0xcert.png?1547039841" );
            Message message = template.buildMessage();
            message.setFrom( MailAddress.of( "Україна", "vladimir.kirichenko@gmail.com" ) );
            message.setTo( MailAddress.of( "Little Green Mail", "vk@xenoss.io" ) );
            mailman.send( message );
            mailman.run();
        }, () -> {
            throw new RuntimeException( "see javadoc!" );
        } );
    }
}
