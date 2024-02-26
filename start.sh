echo '═══════════════════'
echo '# CONSTRUINDO JAR #'
echo '═══════════════════'
mvn clean install -U

echo '═══════════════════════'
echo '# REMOVENDO CONTAINER #'
echo '═══════════════════════'
docker stop rabbitmq || true && docker rm rabbitmq || true
docker stop postgresql || true && docker rm postgresql || true
docker stop backend-cooperativa || true && docker rm backend-cooperativa || true

echo '════════════════════'
echo '# REMOVENDO VOLUMES #'
echo '════════════════════'
docker volume prune -f

echo '═══════════════════════════════════════════════════'
echo '# INICIANDO TODOS OS SERVIÇOS COM DOCKER-COMPOSER #'
echo '═══════════════════════════════════════════════════'
cd ./docker && docker-compose -f docker-compose.yml up
