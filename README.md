# itrack
Location Tracking for Everyone

CREATE TABLE full_data (
  imeih ascii,
  dtime timestamp,
  data ascii,
  PRIMARY KEY (imeih, dtime)
); 

//Scenario for Compressing data by modifying schema

1. Primary key only consists of 2 columns. The first one is sharding key/ row key (imeih). second one is column key (dtime).
imeih is combination of imei + date & hour. Sample data: '862170011627815@2015-01-29@00'. 
Dtime is just column with timestamp datatype

2. Change all text datatype to ascii since all characters in data will be in ascii format. Can you confirm Abhishek?

3. Data column contains the XML data. However, we compacted the xml to minimized the space that will be used in Cassandra.
Sample of Full Data XML:
<x a="NORMAL" b="v1.45C" c="1" d="26.25148N" e="79.86157E" f="0.06" g="2015-01-29 00:00:09" h="2015-01-29 00:00:08" i="2" j="5" k="3" l="5" m="6" n="6" o="3" p="5" q="0" r="12.88"/>

Sample of Compacted XML data and to be store in data column:
N;v1.45C;1;26.25148;79.86157;0.06;2015-01-29@00:00:09;2;5;3;5;6;6;3;5;0;12.88

Note the changes:
 - No opening and closing tags of x ('<x' nd '/>')
 - No parenthesis (")
 - No alphabet like a, b, c. to distinguish the data, the order must be maintained by Java listener and php
 - No device time in the data, only server time. This is becuse we already put as column key
 - Message type is Change from 'NORMAL' to just 'N'.
 - Letter 'N' and 'E' in lat and lon is removed. it is better just to write the coordinate.
 
By applying this trategy the size I read on cassandra is 215 Bytes. this is the data and also the overhead. 
I calculated the overhead is around 50 Bytes. So Roughly the data size is only 165 Bytes.

I believe there are still several steps to push the size downwards even smaller like for example:
- Change the version inside data '1.45C' into shorter version like for example 1. 
  Later we put reference table elsewhere to translate what is 1 means as a version in the xml data
- Change the server time in the data into the time difference (in seconds) between the device time and server time.

However, this approach will need the java listener and PHP to change the way they store and parse data from and 
to Cassandra.
