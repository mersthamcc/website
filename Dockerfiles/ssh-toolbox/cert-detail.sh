#!/usr/bin/env bash

jq -r -c -n \
  --arg subject "$(openssl s_client -showcerts -connect $1:${2:-443} -servername $1 < /dev/null 2>/dev/null | openssl x509 -inform pem -noout -subject -nameopt RFC2253 | sed 's/^subject=//g' )" \
  --arg issuer "$(openssl s_client -showcerts -connect $1:${2:-443} -servername $1 < /dev/null 2>/dev/null | openssl x509 -inform pem -noout -issuer -nameopt RFC2253 | sed 's/^issuer=//g')" \
  --arg serial "$(openssl s_client -showcerts -connect $1:${2:-443} -servername $1 < /dev/null 2>/dev/null | openssl x509 -inform pem -noout -serial | sed 's/^serial=//g')" \
  --arg startdate "$(openssl s_client -showcerts -connect $1:${2:-443} -servername $1 < /dev/null 2>/dev/null | openssl x509 -inform pem -noout -startdate | sed 's/^notBefore=//g')" \
  --arg enddate "$(openssl s_client -showcerts -connect $1:${2:-443} -servername $1 < /dev/null 2>/dev/null | openssl x509 -inform pem -noout -enddate | sed 's/^notAfter=//g')" \
  --arg status $(openssl s_client -showcerts -connect $1:${2:-443} -servername $1 < /dev/null 2>/dev/null | openssl x509 -inform pem -noout -checkend 604800 > /dev/null 2>&1 ; echo $?) \
  --arg timestamp "$(date -Iseconds)" \
  --arg site "$1" \
  '{ "@timestamp": $timestamp, "site": $site, "subject": $subject, "issuer": $issuer, "serial": $serial, "start_date": $startdate, "end_date": $enddate, "status": $status }'
