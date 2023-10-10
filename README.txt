================= MAS-345 Data Acquisition (2023) =================
The *.exe installer package is out of date.  Changes were made in Java8 that
require me to rebuild the program.  Uploading the source for demonstrative purposes.

================= MAS-345 Data Acquisition (2013) =================

This software was developed by Kristofer Weisshaupt.  You can find more 
information on this project and others at:  
http://nooleanbot.blogspot.com/

This software is provided free of charge "as-is," without 
any express or implied warranty.  In no event shall the 
author be held liable for any damages arising from the
use of this software.

Please contact me before redistributing or reusing any of my code or
its output. A citation in the following format would be greatly appreciated:

"___<Component Used>___ courtesy of Kristofer Weisshaupt 
http://nooleanbot.blogspot.com/"


================== Troubleshooting Updating RXTX ===================

In Windows 7 RXTX should work just by being on the installed classpath.

1.  Copy RXTXcomm.jar to MAS-345 DAQ\lib
2.  Copy appropriate rxtxSerial library to MAS-345 DAQ\

Builds and installation instructions for several operating systems can 
be found at: http://mfizz.com/oss/rxtx-for-java

Choose your binary build - x64 or x86 (based on which version of
the JVM you are installing to)

NOTE: Your Java JRE and RXTX must match your system architecture!  
Will not work if you're running 32 bit Java on a 64 bit system.


============================= Credits =============================
RXTX binaries provided by Mfizz Inc. (http://mfizz.com)
Digital-7 font provided by Style-7 (http://www.styleseven.com)
Installer Created using Inno Setup (http://www.jrsoftware.org/isinfo.php)