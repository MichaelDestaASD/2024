
package com.eshop.usermanagement.shared;

import lombok.Data;

/**
 *
 * @author Michael
 */

@Data
public class UserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
}
