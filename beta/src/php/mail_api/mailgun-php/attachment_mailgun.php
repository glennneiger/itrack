<?php
# Include the Autoloader (see "Libraries" for install instructions)
require 'vendor/autoload.php';
use Mailgun\Mailgun;

# Instantiate the client.
$mgClient = new Mailgun('key-88da0d744f1a79c0dc336210f561ca2c');
$domain = "sandbox9d2e30efb63e41f899cbb0499833c2f1.mailgun.org";

/*# Make the call to the client.
$result = $mgClient->sendMessage($domain, array(
    'from'    => 'Excited User <rizwan@iembsys.com>',
    'to'      => 'rizwan@iembsys.com',
    'cc'      => 'hourlyreport4@gmail.com',
   // 'bcc'     => 'astaseen83@gmail.com',
    'subject' => 'Hello',
    'text'    => 'Testing some Mailgun awesomness!',
    'html'    => '<html>HTML version of the body</html>'
), array(
    'attachment' => array('C:\Users\taseen\Desktop\SENDGRID\attachment_mailgun,php.txt', 'C:\Users\taseen\Desktop\SENDGRID\attachment_mailgun.php')	
));*/
?>