package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    private String user_id;
    private String email;
    private String password;
    private String type;
    private String reg_id;
    private String reg_dt;
    private String chg_id;
    private String chg_dt;

}
