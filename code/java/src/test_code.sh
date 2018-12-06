#!/bin/sh

source ../../postgresql/./startPostgreSQL.sh
pg_ctl status
source ../../postgresql/./createPostgreDB.sh
source .././compile.sh
source .././run.sh msant035_DB 9998 msant035

