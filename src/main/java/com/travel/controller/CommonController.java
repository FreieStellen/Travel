package com.travel.controller;

import cn.hutool.core.util.StrUtil;
import com.travel.common.CommonHolder;
import com.travel.common.ResponseResult;
import com.travel.utils.RedisCache;
import com.travel.utils.RegexUtil;
import com.travel.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.travel.utils.RedisConstants.LOGIN_CODE_KEY;
import static com.travel.utils.RedisConstants.PHONE_CODE_TTL_MINUTES;

/*
 *@ClassName CommonController 公共控制层
 *@Author Freie  stellen
 *@Date 2024/5/7 23:52
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private RedisCache redisCache;

    @Value("${Travel.path}")
    private String basePath;

    /**
     * @Description: 发送手机验证码(已测试)
     * @param: phone
     * @date: 2024/3/29 14:21
     */
    @GetMapping("/sendmsg/{phone}")
    public ResponseResult<String> sendMsg(@PathVariable String phone) {

        log.info("拿到的电话为：{}", phone);
        //判断电话号码是否为空(如果传入的字符串为null、空字符串(“”)或仅包含空白字符（如空格、制表符、换行符等），则返回true；否则返回false。)
        if (StrUtil.isBlank(phone)) {

            //号码为空则返回
            return ResponseResult.error("电话不能为空！");
        }
        log.info("{}", RegexUtil.isPhoneInvalid(phone));

        //校验手机号是否有效
        if (!RegexUtil.isPhoneInvalid(phone)) {

            //如果无效就返回
            return ResponseResult.error("电话号码格式错误！");
        }
        //如果有效就生成随机的验证码
        String code = ValidateCodeUtils.generateValidateCode();

        log.info("生成的验证码为:{}", code);

        //将手机号和验证码存到redis中并设置过期时间,时间为1分钟
        redisCache.setCacheObject(phone, code, PHONE_CODE_TTL_MINUTES, TimeUnit.MINUTES);

        //返回发送成功消息
        return ResponseResult.success(code, "随机验证码生成！");

    }

    /**
     * @Description: 退出登录账户功能
     * @param:
     * @date: 2024/5/13 18:01
     */

    @GetMapping("/logout")
    public ResponseResult<String> logout() {
        //获取当前登录的用户
        String id = CommonHolder.getUser();

        log.info("当前线程用户：{}", id);

        //拼接redis的key
        String key = LOGIN_CODE_KEY + id;

        //从redis中移除用户
        boolean deleteObject = redisCache.deleteObject(key);

        if (!deleteObject) {
            return ResponseResult.error("登陆状态异常！");
        }
        return ResponseResult.success("注销账户成功！");
    }

    @PostMapping("/upload")
    public ResponseResult<String> upload(@RequestParam(name = "file") MultipartFile files) {

        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info("文件上传与下载：{}", files.toString());

        //使用原始文件名
        String originalFilename = files.getOriginalFilename();

        //截取文件名后缀l.jpg

        String suffix = null;
        if (originalFilename != null) {

            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID() + suffix;

        //创建一个目录对象
        File file = new File(basePath);

        //判断当前对象是否存在
        if (!file.exists()) {

            //不存在则创建目录
            file.mkdirs();
        }
        try {

            //将临时文件转存到指定位置
            files.transferTo(new File(basePath + fileName));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseResult.success(fileName);
    }

    /**
     * 文件的下载及回显
     *
     * @param: name, response
     * @date: 2023/11/27 16:37
     */

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {

        try {
            //输入流，通过输入流来读取文件内容
            FileInputStream fileInputStream = new FileInputStream(basePath + name);

            //输出流。通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            //设置下载文件类型
            response.setContentType("image/jpeg");

            int len;
            byte[] bytes = new byte[1024];

            while ((len = fileInputStream.read(bytes)) != -1) {

                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
