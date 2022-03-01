# bboss group website:
http://www.bbossgroups.com

# 版本构建方法

gradle clean publishToMavenLocal

需要通过gradle构建发布版本,gradle安装配置参考文档：

https://esdoc.bbossgroups.com/#/bboss-build

# bboss group project blog:
http://yin-bp.iteye.com/
# 技术交流
bboss session技术交流群：166471103

bboss session微信公众号：bbossgroups
![bbossgroups](https://static.oschina.net/uploads/space/2017/0617/094201_QhWs_94045.jpg)


# bboss 会话共享源码
github托管地址： 

https://github.com/bbossgroups/security 

svn下载地址 

https://github.com/bbossgroups/security/trunk 

# bboss session集成权威指南
https://my.oschina.net/bboss/blog/758871

# session操作使用样列(遵循servlet标准规范)：

```
HttpSession session = request.getSession();//request.getSession(true)

session.setMaxInactiveInterval(180);//修改session有效期,单位：秒

TestVO testVO = new TestVO();

testVO.setId("sessionmoitor testvoid");

TestVO1 testVO1 = new TestVO1();

testVO1.setName("hello,sessionmoitor test vo1");

testVO.setTestVO1(testVO1);

session.setAttribute("testVO", testVO);

testVO = (TestVO)session.getAttribute("testVO");

//修改testVO中属性的值

testVO.setId("testvoidaaaaa,sessionmonitor modifiy id");

//需要将修改后的对象重新设置到session中否则无法存储最新的testVO到mongodb中

session.setAttribute("testVO", testVO);

testVO = (TestVO)session.getAttribute("testVO");

```

更多使用方法参考文档：

http://yin-bp.iteye.com/category/327553

## |--bboss-security

  bboss会话共享核心工程
  
## |--bboss-ticket

  bboss令牌统一认证核心工程  

## |--bboss-security-web和bboss-security-web-inf

bboss会话共享监控服务和令牌服务工程

为了方便应用系统集成bboss会话共享功能，准备了两个会话共享demo工程： 
https://github.com/bbossgroups/sessiondemo
 
## |--session 

如果只需要session共享功能，则整合这个工程中的配置文件和jar包到实际项目中即可 

## |--sessionmonitor

具体使用参考demo使用文档：
http://yin-bp.iteye.com/blog/2087308


## bboss项目特色特点介绍文档：
http://yin-bp.iteye.com/blog/1080824

## License

The BBoss Framework is released under version 2.0 of the [Apache License][].

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0