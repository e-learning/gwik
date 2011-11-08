#!/bin/bash

mvn -f ../../pom.xml exec:java \
-Dexec.mainClass="net.sf.xfresh.util.Starter" \
-Dserver \
-Dexec.args="beans.xml"