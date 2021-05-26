package com.example.drg;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(DrgApplication.class);
    }

    /**
     * 톰캣에 옵션 추가.
     * Invalid character found in the request target. The valid characters are defined in RFC 7230 and RFC 3986
     */
    @Configuration
    static class TomcatWebServerCustomizer
            implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

        @Override
        public void customize(TomcatServletWebServerFactory factory) {
            factory.addConnectorCustomizers((TomcatConnectorCustomizer)
                    connector -> connector.setProperty("relaxedQueryChars", "<>[\\]^`{|}")); //인자값에 다음 특수문자 허용
        }
    }

}
