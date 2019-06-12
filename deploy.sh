cf create-service p-circuit-breaker-dashboard trial demo-circuit-breaker
pushd Circuit-Breaker-To-Read-List
./mvnw clean package 
cf push
popd 
pushd Circuit-Breaker-Book-List 
./mvnw clean package 
cf push
popd
