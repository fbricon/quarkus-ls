cd microprofile.jdt && ./mvnw clean verify \
&& cd ../microprofile.ls/com.redhat.microprofile.ls && ./mvnw clean install \
&& cd ../../quarkus.ls.ext/com.redhat.quarkus.ls && ./mvnw clean verify \
&& cd ../..