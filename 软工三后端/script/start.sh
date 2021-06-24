cd /nioCoin/backend/product || exit 1
nohup java -jar -Dserver.port="$1" production.jar >log 2>&1 &
