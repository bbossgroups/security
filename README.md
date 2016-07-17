# bboss group website:
http://www.bbossgroups.com

# bboss group project blog:
http://yin-bp.iteye.com/

# bboss 会话共享源码
github托管地址： 

https://github.com/bbossgroups/security 

svn下载地址 

https://github.com/bbossgroups/security/trunk 

# session操作使用样列(遵循servlet标准规范)：

```
HttpSession session = request.getSession();//request.getSession(true)

session.setMaxInactiveInterval(180000);//修改session有效期

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

  bboss会话共享和令牌管理核心工程,独立ant构建指令：bboss-security/run.bat,发布的jar和依赖资源文件存放在bboss-security/distrib目录下,构建完毕后不会同步更新其他工程下的依赖包

## |--bboss-security-web和bboss-security-web-inf

bboss会话共享监控服务和令牌服务工程

为了方便应用系统集成bboss会话共享功能，准备了两个会话共享demo工程： 

## |--session 

如果只需要session共享功能，则整合这个工程中的配置文件和jar包到实际项目中即可 

## |--sessionmonitor

如果需要session共享以及session监控管理功能，则整合这个工程中的配置文件和jar包到实际项目中即可 

两个会话共享demo工程整合方法，可以参考文档：

http://yin-bp.iteye.com/blog/2087308

## 构建和发布工程：

编译构建所有插件模块：

cd security

gradle install

构建成功后，先启用gretty插件（注意：第一次构建工程，需要关闭gretty插件，默认关闭）

修改/security/gradle.properties中属性为true，即可启用插件：

enable_gretty=true

然后运行以下指令,启动tomcat和demo应用

cd security

gradle :session:tomcatStart

gradle :sessionmonitor:tomcatStart

运行前请修改session和sessionmonitor两个工程中的mongodb.xml或者redis.xml（根据实际情况二者选其一）

resources/mongodb.xml

resources/redis.xml


## bboss项目特色特点介绍文档：
http://yin-bp.iteye.com/blog/1080824

## License

The BBoss Framework is released under version 2.0 of the [Apache License][].

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0