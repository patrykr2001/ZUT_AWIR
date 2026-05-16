package edu.zut.awir.awir10.integration;

import edu.zut.awir.awir10.model.User;
import edu.zut.awir.awir10.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;

@Configuration
@RequiredArgsConstructor
public class UserIntegrationFlow {
    private final UserService userService;

    @Bean
    public IntegrationFlow userHttpGatewayFlow() {
        return IntegrationFlow
                .from(Http.inboundGateway("/si/users-sync")
                        .requestMapping(m -> m
                                .methods(HttpMethod.POST)
                                .consumes("application/json")
                        )
                        .requestPayloadType(User.class)
                )
                .handle(User.class, (user, headers) -> userService.save(user))
                .get();
    }
}
