<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
$host        = "host=127.0.0.1";
$port        = "port=5433";
$dbname      = "dbname=gisgraphy";
$credentials = "user=postgres password=neon04$";
$db_connection = pg_connect( "$host $port $dbname $credentials"  );
?>