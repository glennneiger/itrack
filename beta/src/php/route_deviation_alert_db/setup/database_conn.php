<?php
	$DBASE = "iespl_vts_beta";
	$USER = "bailoo";
        //$USER = "root";
	$PASSWD = 'neon04$VTS';
	$HOST = "itrackdb.c4pqfsdaiccz.us-east-1.rds.amazonaws.com";
        $HOST='localhost';
	$DbConnection = mysql_connect($HOST,$USER,$PASSWD) or die("could not connect to DB test");
	mysql_select_db ($DBASE, $DbConnection) or die("could not find DB");
	$common_date=date("Y-m-d H:i:s");
?>
