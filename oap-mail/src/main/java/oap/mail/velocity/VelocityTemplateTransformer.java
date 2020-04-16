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
package oap.mail.velocity;

import lombok.extern.slf4j.Slf4j;
import oap.mail.MailException;
import oap.mail.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static org.apache.velocity.runtime.RuntimeConstants.RESOURCE_LOADER;
import static org.apache.velocity.runtime.RuntimeConstants.RUNTIME_LOG_LOGSYSTEM;
import static org.apache.velocity.runtime.RuntimeConstants.UBERSPECT_CLASSNAME;

@Slf4j
public class VelocityTemplateTransformer {
    private final VelocityEngine engine = new VelocityEngine();

    public VelocityTemplateTransformer() {
        try {
            engine.addProperty( "userdirective", XPathDirective.class.getName() );
            engine.addProperty( "userdirective", FormatDateDirective.class.getName() );
            engine.addProperty( UBERSPECT_CLASSNAME, Uberspector.class.getName() );
            engine.setProperty( RUNTIME_LOG_LOGSYSTEM, new Logger() );
            engine.setProperty( RESOURCE_LOADER, "classpath" );
            engine.setProperty( "classpath.resource.loader.class", ClasspathResourceLoader.class.getName() );

            engine.init();
        } catch( Exception e ) {
            throw new MailException( e );
        }
    }

    public synchronized String transform( Template template ) {
        try {
            VelocityContext context = new VelocityContext();
            Map<String, Object> parameters = template.getParameters();
            for( String key : parameters.keySet() )
                context.put( key, parameters.get( key ) );
            StringWriter writer = new StringWriter();
            engine.evaluate( context, writer, "mail", template.getContent() );
            writer.close();
            return writer.toString();
        } catch( ParseErrorException | MethodInvocationException | ResourceNotFoundException | IOException e ) {
            throw new MailException( e );
        }
    }

    private static class Logger implements LogChute {

        @Override
        public void init( RuntimeServices runtimeServices ) {

        }

        @Override
        public void log( int level, String message ) {
            switch( level ) {
                case TRACE_ID -> log.trace( message );
                case DEBUG_ID -> log.debug( message );
                case INFO_ID -> log.info( message );
                case WARN_ID -> log.warn( message );
                case ERROR_ID -> log.error( message );
            }
        }

        @Override
        public void log( int level, String message, Throwable throwable ) {
            switch( level ) {
                case TRACE_ID -> log.trace( message, throwable );
                case DEBUG_ID -> log.debug( message, throwable );
                case INFO_ID -> log.info( message, throwable );
                case WARN_ID -> log.warn( message, throwable );
                case ERROR_ID -> log.error( message, throwable );
            }
        }

        @Override
        public boolean isLevelEnabled( int level ) {
            return switch( level ) {
                case TRACE_ID -> log.isTraceEnabled();
                case DEBUG_ID -> log.isDebugEnabled();
                case INFO_ID -> log.isInfoEnabled();
                case WARN_ID -> log.isWarnEnabled();
                case ERROR_ID -> log.isErrorEnabled();
                default -> true;
            };
        }
    }
}
