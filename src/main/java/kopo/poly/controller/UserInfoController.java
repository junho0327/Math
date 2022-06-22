package kopo.poly.controller;

import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * UserInfo는 사용자의 정보를 관리하며, 사용자 정보를 담당하는 페이지입니다.
 */
@Slf4j
@Controller
public class UserInfoController {

    /**
     *
     * 비즈니스 로직(중요 로직을 수행하기 위해 사용되는 서비스를 메모리에 적재(싱글톤패턴 적용됨)
     */
    @Resource(name = "UserInfoService")
    private IUserInfoService userInfoService;

    // MongoDB 컬렉션 이름
    private String colNm = "UserInfoCollection";

    /**
     * GetMapping은 GET방식을 통해 접속되는 URL 호출에 대해 실행되는 함수로 설정함을 의미함
     * PostMapping은 POST방식을 통해 접속되는 URL 호출에 대해 실행되는 함수로 설정함을 의미함
     * GetMapping(value = "index") =>  GET방식을 통해 접속되는 URL이 index인 경우 아래 함수를 실행함
     */

    /**
     * 회원가입 화면으로 이동
     */
    @GetMapping(value = "math/register")
    public String userRegForm() {

        log.info(this.getClass().getName() + ".math/register ok!");

        return "math/register";
    }

    /**
     * 회원가입 로직 처리
     * */
    @RequestMapping(value="user/insertUserInfo")
    public String insertUserInfo(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".insertUserInfo start!");

        //회원가입 결과에 대한 메시지를 전달할 변수
        String msg = "";

        //웹(회원정보 입력화면)에서 받는 정보를 저장할 변수
        UserInfoDTO pDTO = null;

        try{

            /*
             *        웹(회원정보 입력화면)에서 받는 정보를 String 변수에 저장 시작!!
             */
            String user_id = CmmUtil.nvl(request.getParameter("user_id")); //아이디
            String user_pw = CmmUtil.nvl(request.getParameter("user_pw")); //비밀번호
            String email = CmmUtil.nvl(request.getParameter("email")); //이메일
            String auth_radio = CmmUtil.nvl(request.getParameter("flexRadioDefault")); //권한

            String user_auth = auth_radio.equals("on") ? "student" : "teacher";

            /*
             *     반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함
             */
            log.info("user_id : " + user_id);
            log.info("password : " + user_pw);
            log.info("email : " + email);
            log.info("user_auth : " + user_auth);
            log.info("user_auth : " + auth_radio);

            /*
             *        웹(회원정보 입력화면)에서 받는 정보를 DTO에 저장하기 시작!!
             */

            //웹(회원정보 입력화면)에서 받는 정보를 저장할 변수를 메모리에 올리기
            pDTO = new UserInfoDTO();

            pDTO.setUser_id(user_id);

            //비밀번호는 절대로 복호화되지 않도록 해시 알고리즘으로 암호화함
            pDTO.setUser_pw(EncryptUtil.encHashSHA256(user_pw));

            //민감 정보인 이메일은 AES128-CBC로 암호화함
            pDTO.setEmail(EncryptUtil.encAES128CBC(email));

            pDTO.setUser_auth(user_auth);

            // 회원가입
            int res = userInfoService.insertUserInfo(pDTO, colNm);

            log.info("회원가입 결과(res) : "+ res);

            if (res==1) {

                model.addAttribute("msg", "회원정보 입력완료");
                String login_url = user_auth.equals("student") ? "slogin" : "tlogin"; // 학생은 학생로그인, 선생은 선생 로그인 이동
                model.addAttribute("url", "/math/" + login_url);   //임시로 로그인 창으로 이동

            } else if (res==2) {

                model.addAttribute("msg", "중복되는 값입니다.");
                model.addAttribute("url", "/math/register");   // 회원가입창으로 다시 이동

            } else {
                model.addAttribute("msg", "회원가입에 실패");
                model.addAttribute("url", "/math/register"); //회원가입창으로 다시 이동

            }

        }catch(Exception e){
            //저장이 실패되면 사용자에게 보여줄 메시지
            msg = "실패하였습니다. : " + e.toString();
            log.info(e.toString());
            e.printStackTrace();

        }finally{
            log.info(this.getClass().getName() + ".insertUserInfo end!");


            //변수 초기화(메모리 효율화 시키기 위해 사용함)
            pDTO = null;

        }

        return "/math/Msg";
    }

    /**
     * 로그인 처리 및 결과 알려주는 화면으로 이동
     * */
    @PostMapping(value="user/getUserLoginCheck")
    public String getUserLoginCheck(HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".getUserLoginCheck start!");

        //로그인 처리 결과를 저장할 변수 (로그인 성공 : 1, 아이디, 비밀번호 불일치로인한 실패 : 0, 시스템 에러 : 2)
        int res = 0;

        //웹(회원정보 입력화면)에서 받는 정보를 저장할 변수
        UserInfoDTO pDTO = null;

        try{

            // 웹에서 받는 정보를 String 변수로 저장
            String user_id = CmmUtil.nvl(request.getParameter("user_id")); //아이디
            String user_pw = CmmUtil.nvl(request.getParameter("user_pw")); //비밀번호

            // 로그 테스트
            log.info("user_id : "+ user_id);
            log.info("user_pw : "+ user_pw);

            //웹(회원정보 입력화면)에서 받는 정보를 저장할 변수를 메모리에 올리기
            pDTO = new UserInfoDTO();

            pDTO.setUser_id(user_id);

            //비밀번호는 절대로 복호화되지 않도록 해시 알고리즘으로 암호화함
            pDTO.setUser_pw(EncryptUtil.encHashSHA256(user_pw));


            // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기 위한 userInfoService 호출하기
            res = userInfoService.getUserLoginCheck(pDTO, colNm);

            log.info("res : "+ res);

            // 로그인 성공 시, 세션 값 집어넣기
            if (res == 1) {

                /*
                 * 세션에 회원아이디 저장하기, 추후 로그인여부를 체크하기 위해 세션에 값이 존재하는지 체크한다.
                 * 일반적으로 세션에 저장되는 키는 대문자로 입력하며, 앞에 SS를 붙인다.
                 *
                 * Session 단어에서 SS를 가져온 것이다.
                 */
                session.setAttribute("SS_USER_ID", user_id);
                session.setAttribute("SS_USER_AUTH", userInfoService.getUserInfo(pDTO, colNm).getUser_auth());

                String user_auth = userInfoService.getUserInfo(pDTO, colNm).getUser_auth();

                model.addAttribute("msg", user_id + "님이 로그인 되었습니다.");
                String main_url = user_auth.equals("student") ? "student" : "teacher"; // 학생은 학생메인, 선생은 선생님메인 이동
                model.addAttribute("url", "/math/" + main_url);

            } else {

                model.addAttribute("msg", user_id + "아이디, 비밀번호가 일치하지 않습니다.");
                model.addAttribute("url", "/math/main");

            }

        }catch(Exception e){

            //저장이 실패되면 사용자에게 보여줄 메시지
            res = 2;
            log.info(e.toString());
            e.printStackTrace();

            model.addAttribute("msg", "시스템에 문제가 발생하였습니다. 잠시 후 다시 시도하여 주시길 바랍니다.");
            model.addAttribute("url", "/math/main");

        }finally{
            log.info(this.getClass().getName() + ".insertUserInfo end!");

            /* 로그인 처리 결과를 jsp에 전달하기 위해 변수 사용
             * 숫자 유형의 데이터 타입은 값을 전달하고 받는데 불편함이  있어
             * 문자 유형(String)으로 강제 형변환하여 jsp에 전달한다.
             * */
            model.addAttribute("res", String.valueOf(res));

            //변수 초기화(메모리 효율화 시키기 위해 사용함)
            pDTO = null;

        }

        return "/math/userInfoLoginResult";
    }

}