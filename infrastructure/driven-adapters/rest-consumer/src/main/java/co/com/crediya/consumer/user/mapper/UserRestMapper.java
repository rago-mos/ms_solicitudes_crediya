package co.com.crediya.consumer.user.mapper;

import co.com.crediya.consumer.user.UserDocumentsResponse;
import co.com.crediya.model.user.UserApplication;
import org.springframework.stereotype.Component;

@Component
public class UserRestMapper {

    public UserApplication toUserApplication(UserDocumentsResponse response) {
        return UserApplication.builder()
                .firstName(response.getFirstName())
                .lastName(response.getLastName())
                .email(response.getEmail())
                .identityDocument(response.getIdentityDocument())
                .baseSalary(response.getBaseSalary())
                .build();
    }
}
