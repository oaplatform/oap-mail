package oap.mail.velocity;

import oap.mail.Message;
import oap.mail.Template;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import static oap.mail.Template.Type.TEXT;
import static oap.testng.Asserts.assertString;

public class FormatDateDirectiveTest {

    @Test
    public void render() {
        Bean bean = new Bean();
        Template template = new Template( TEXT, "--subject--\nsubj\n--body--\n#formatDate($o.d 'MMM dd, yyyy')\n" );
        template.bind( "o", bean );
        Message message = template.buildMessage();
        assertString( message.body ).isEqualTo( "Jan 01, 1970" );
    }

    public static class Bean {
        public DateTime d = new DateTime( 0 );
    }
}
