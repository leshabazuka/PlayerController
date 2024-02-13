package entities;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Player {
    public Integer id;
    public String screenName;
    public String gender;
    public Integer age;
    public String login;
    public String password;
    public String role;
}
