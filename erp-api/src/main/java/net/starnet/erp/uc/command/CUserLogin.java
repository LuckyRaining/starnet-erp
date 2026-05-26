package net.starnet.erp.uc.command;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.java.Log;
import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.exception.BizException;
import net.starnet.erp.rc.service.LogService;
import net.starnet.erp.uc.model.User;
import net.starnet.erp.uc.service.UserService;
import net.starnet.erp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 用户登录
 */
@Log
@Command
public class CUserLogin extends BaseCommand {

    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private LogService logService;

    /**
     * 登录名
     */
    private @Param(required = true) String loginName;
    /**
     * 密码
     */
    private @Param(required = true) String password;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        User user = userService.findByLoginName(loginName);
        Assert.notNull(user, "登录名为【" + loginName + "】的用户不存在！");

        if (!encoder.matches(password, user.getPassword())) {
            throw new BizException("密码不正确！");
        }

        // 生成令牌
        JSONArray roles = new JSONArray();
        roles.add("admin");
        String token = jwtUtil.createJwt(user.getId(), user.getUsername(), roles.toJSONString());

        log.info("登录成功！");

        // 记录登录日志。loginStyle = 0 表示用户名登录，1 表示手机号登录，2 其他方式登录
        if (userService.findByUsername(loginName) != null) {
            logService.logUserLogin(Long.parseLong(user.getId()), user.getUsername(), 0);
        } else if (userService.findByMobile(loginName) != null) {
            logService.logUserLogin(Long.parseLong(user.getId()), user.getUsername(), 1);
        } else {
            logService.logUserLogin(Long.parseLong(user.getId()), user.getUsername(), 2);
        }

        // 返回 token 信息
        data.put("token", token);


        // JSONObject loginUser = new JSONObject();
        // loginUser.put("id", user.getId());
        // loginUser.put("username", user.getUsername());
        // loginUser.put("name", user.getName());
        // loginUser.put("mobile", user.getMobile());
        User loginUser = new User();
        loginUser.setId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setName(user.getName());
        loginUser.setMobile(user.getMobile());
        // 返回 当前登录用户 的信息
        data.put("user", loginUser);
    }
}
