package edu.zut.awir.awir7.web.soap;

import edu.zut.awir.awir7.service.UserService;
import edu.zut.awir.awir7.web.soap.gen.GetUserRequest;
import edu.zut.awir.awir7.web.soap.gen.GetUserResponse;
import edu.zut.awir.awir7.web.soap.gen.GetUsersRequest;
import edu.zut.awir.awir7.web.soap.gen.GetUsersResponse;
import edu.zut.awir.awir7.web.soap.gen.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@RequiredArgsConstructor
public class UserEndpoint {
    private static final String NAMESPACE = "http://zut.edu/awir/users";

    private final UserService userService;

    @PayloadRoot(namespace = NAMESPACE, localPart = "getUserRequest")
    @ResponsePayload
    public GetUserResponse getUser(@RequestPayload GetUserRequest request) {
        GetUserResponse resp = new GetUserResponse();
        edu.zut.awir.awir7.model.User user = userService.findById(request.getId());
        if (user != null) {
            resp.setUser(toSoapUser(user));
        }
        return resp;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "getUsersRequest")
    @ResponsePayload
    public GetUsersResponse getUsers(@RequestPayload GetUsersRequest request) {
        GetUsersResponse resp = new GetUsersResponse();
        userService.findAll().forEach(u -> resp.getUsers().add(toSoapUser(u)));
        return resp;
    }

    private User toSoapUser(edu.zut.awir.awir7.model.User user) {
        User soapUser = new User();
        soapUser.setId(user.getId());
        soapUser.setName(user.getName());
        soapUser.setEmail(user.getEmail());
        return soapUser;
    }
}
