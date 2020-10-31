package org.openchat.api;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import org.openchat.domain.users.RegistrationData;
import org.openchat.domain.users.User;
import org.openchat.domain.users.UserService;
import org.openchat.domain.users.UsernameAlreadyInUseException;
import spark.Request;
import spark.Response;

import java.util.List;

import static org.eclipse.jetty.http.HttpStatus.*;
import static org.openchat.infrastructure.json.UserJson.jsonFor;

public class UsersAPI {

    private UserService userService;

    public UsersAPI(UserService userService) {
        this.userService = userService;
    }

    public String createUser(Request request, Response response) {
        RegistrationData registration = registrationDataFrom(request);

        try {
            User user = userService.createUser(registration);
            response.status(CREATED_201);
            response.type("application/json");
            return jsonFor(user);
        } catch (UsernameAlreadyInUseException e) {
            response.status(BAD_REQUEST_400);
            return "Username already in use.";
        }
    }

    public String allUsers(Request request, Response response) {
        List<User> users = userService.allUsers();
        response.status(OK_200);
        response.type("application/json");
        return jsonFor(users);
    }

    private RegistrationData registrationDataFrom(Request request) {
        JsonObject json = Json.parse(request.body()).asObject();
        return new RegistrationData(
                            json.getString("username", ""),
                            json.getString("password", ""),
                            json.getString("about", ""),
                            json.getString("url",""));
    }
}
