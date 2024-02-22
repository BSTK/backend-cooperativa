echo '════════════════════════════════════════════'
echo '# CONSTRUINDO JAR                          #'
echo '════════════════════════════════════════════'
mvn clean install -U

echo ''
echo '═══════════════════════════════════════════════════'
echo '# INICIANDO TODOS OS SERVIÇOS COM DOCKER-COMPOSER #'
echo '═══════════════════════════════════════════════════'
cd ./docker && docker-compose -f docker-compose.yml up
