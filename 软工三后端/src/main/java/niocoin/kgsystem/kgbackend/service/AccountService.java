package niocoin.kgsystem.kgbackend.service;

import niocoin.kgsystem.kgbackend.dto.*;
import niocoin.kgsystem.kgbackend.mapper.AccountMapper;
import niocoin.kgsystem.kgbackend.po.User;
import niocoin.kgsystem.kgbackend.util.AESUtil;
import niocoin.kgsystem.kgbackend.util.Err;
import niocoin.kgsystem.kgbackend.util.MailUtil;
import niocoin.kgsystem.kgbackend.util.OssUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class AccountService {
    private final AccountMapper accountMapper;
    private final OssUtil ossUtil;
    private final MailUtil mailUtil;

    public AccountService(AccountMapper accountMapper, OssUtil ossUtil, MailUtil mailUtil) {
        this.accountMapper = accountMapper;
        this.ossUtil = ossUtil;
        this.mailUtil = mailUtil;
    }

    public void register(userDTO userDto) throws Err {
        AESUtil aesUtil = new AESUtil();
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        user.setPassword(aesUtil.encrypt(user.getPassword()));
        if (accountMapper.getAccountByUsername(user.getUsername()) != null) {
            throw new Err("用户名已存在");
        } else if (accountMapper.getAccountByEmail(user.getEmail()) != null) {
            throw new Err("邮箱已存在");
        }
        try {
            accountMapper.createNewAccount(user);
        } catch (Exception e) {
            throw new Err("注册出现异常");
        }
    }

    public User login(userDTO userDto) throws Err {
        AESUtil aesUtil = new AESUtil();
        User user = accountMapper.getAccountByUsername(userDto.getUsername());
        if (user == null) {
            user = accountMapper.getAccountByEmail(userDto.getUsername());
        }
        if (user == null || !aesUtil.decrypt(user.getPassword()).equals(userDto.getPassword())) {
            throw new Err("不正确的用户名或密码");
        }
        user.setPassword("");
        return user;
    }

    public boolean isInit() {
        User user = accountMapper.getAccountByUsername("123");
        return user == null;
    }

    public boolean changePass(passwordDTO passwordDTO) throws Err {
        AESUtil aesUtil = new AESUtil();
        User user = accountMapper.getAccountByUsername(passwordDTO.getUserName());
        if (!aesUtil.decrypt(user.getPassword()).equals(passwordDTO.getOldPass())) {
            return false;
        } else {
            accountMapper.updatePass(aesUtil.encrypt(passwordDTO.getNewPass()), passwordDTO.getUserName());
            return true;
        }
    }

    public boolean changeUsername(usernameDTO usernameDTO) {
        User user = accountMapper.getAccountByUsername(usernameDTO.getOldUsername());
        if (user == null)
            return false;
        else {
            accountMapper.updateUsername(usernameDTO.getNewUsername(), usernameDTO.getOldUsername());
            return true;
        }
    }

    public String updateUserImg(userImgDTO userImgDTO, InputStream input) throws Err {
        String url = ossUtil.upload("niocoin-181", userImgDTO.getName(), input);
        System.out.println(userImgDTO.getUserId());
        System.out.println(userImgDTO.getName());
        accountMapper.updateUserImg(url, userImgDTO.getUserId());
        return url;
    }

    public void verifyUserEmail(verifyUserEmailDTO verifyUserEmailDTO) throws Err {
        User user = accountMapper.getAccountByUsername(verifyUserEmailDTO.getUsername());
        if (user.getEmail().equals(verifyUserEmailDTO.getEmail())) {
            mailUtil.verifyCode(verifyUserEmailDTO.getVerCode(), verifyUserEmailDTO.getEmail());
        } else {
            throw new Err("用户名与邮箱不匹配");
        }
    }

    public void forgetPassword(forgetPasswordDTO forgetPasswordDTO) throws Err {
        AESUtil aesUtil = new AESUtil();
        accountMapper.updatePass(aesUtil.encrypt(forgetPasswordDTO.getPassword()), forgetPasswordDTO.getUsername());
    }
}
