监控新增功能：
1.应用定制查询属性：在sessionconf.xml文件中可以配置需要在监控管理查询的session属性
2.如果“账号”框不填值，并且选中查询账号为空的记录复选框，则查询所有没有设置账号或者账号为""的session记录
3.自定义的账号属性也会在列表中出现
4.可以查看选中的应用的session共享配置，应用加载session共享框架组件时，会将本地的session管理配置推送到session监控中心，进行统一监控管理：可以在线查看应用有没有开启失效session销毁进程等配置
5.可以在线查看应用配置的可查询session属性信息，应用可以配置多个session属性，
如果没有配置则不需要在线查看，可查询属性在sessionconf.xml中进行配置，采用json格式配置，例如：

			[
				{"name":"userAccount","cname":"账号","type":"String","like":true,"enableEmptyValue":true},				
				{"name":"worknumber","cname":"工号","type":"String","like":false,"enableEmptyValue":true}
				
6.在线查看跨域跨站session共享配置：
根域名
共享属性
应用私有session属性命名空间

7.采用json数组进行配置可查询session属性，配置的属性包含以下信息：
name:属性名称
cname:属性中文名称
type：属性数据对应的java类型
like:是否采用模糊查询，查询输入的条件串开始的session对象
enableEmptyValue:是否可以查询属性值为null或者为""串的session对象
useIndex:是否对指定的session属性值建立索引(保留属性，目前未启用)

需要查询的属性通过以下java语句在程序中设置属性值：
session.setAttribute("userAccount", this.getUserAccount());
		session.setAttribute("worknumber", this.getUserAttribute("userWorknumber"));



如果启用了跨域跨站的session共享，则只能配置应用间共享的session属性，
不能配置应用私有的session属性
				
准备了两个工程
session工程 ----如果只需要session共享功能，则整合这个工程中的配置文件和jar包即可
sessionmonitor工程----如果需要session共享以及监控功能，则整合这个工程中的配置文件和jar包即可

一、session工程使用指南
session工程包含session功能验证sessiontest.jsp文件，会话共享需要的所有配置文件和最小依赖jar包，
工程的结构说明：
/session/resources/org/frameworkset/soa/serialconf.xml --序列化插件配置文件，类别名配置
/session/resources/mongodb.xml   --mongodb配置文件
/session/resources/sessionconf.xml --session配置文件
/session/session.xml    --session工程web部署文件
/session/WebRoot/sessiontest.jsp  --session功能验证文件
/session/WebRoot/WEB-INF/web.xml   --配置session共享拦截过滤器
/session/WebRoot/WEB-INF/lib      --会话共享最小依赖jar文件存放目录
工程搭建和部署步骤如下：
1.下载工程并导入eclipse
2.参考以下文档配置/resources/mongodb.xml中的mongodb连接
http://yin-bp.iteye.com/blog/2064662
参考其中的章节【6.mongodb客户端配置 】
2.部署，将工程根目录下的部署文件session.xml拷贝到tomcat目录下：
F:\environment\apache-tomcat-7.0.30\conf\Catalina\localhost
根据实际情况修改session.xml中的应用路径docBase：
<?xml version="1.0" encoding="UTF-8"?>
<Context   path="/session"   docBase="F:\workspace\bbossgroups-3.5\bestpractice\session\WebRoot"   debug="0"/>
3.启动tomcat，
启动tomcat后，在浏览器中输入以下地址，验证session共享功能，同时在监控工程sessionmonitor的监控页面查看session的变化情况，
http://localhost:8080/session/sessiontest.jsp

二、sessionmonitor工程使用指南
sessionmonitor工程包含session功能验证jsp页面和会话共享监控功能，
工程的结构说明：
/sessionmonitor/resources/org/frameworkset/soa/serialconf.xml --序列化插件配置文件，类别名配置
/sessionmonitor/resources/mongodb.xml   --mongodb配置文件
/sessionmonitor/resources/sessionconf.xml --session配置文件
/sessionmonitor/sessionmonitor.xml    --sessionmonitor工程web部署文件
/sessionmonitor/WebRoot/sessiontest.jsp  --session功能验证文件
/sessionmonitor/WebRoot/WEB-INF/web.xml   --配置session共享拦截过滤器
/sessionmonitor/WebRoot/WEB-INF/lib      --会话共享及监控最小依赖jar文件存放目录
/sessionmonitor/WebRoot/session/sessionManager/sessionManager.jsp          --session监控jsp页面入口地址
/sessionmonitor/WebRoot/session/sessionManager/sessionList.jsp  --session监控session 列表页面
/sessionmonitor/WebRoot/session/sessionManager/viewSessionInfo.jsp  --sesion详细信息查看页面
工程搭建和部署步骤如下：
1.下载工程并导入eclipse
2.参考以下文档配置/resources/mongodb.xml中的mongodb连接
http://yin-bp.iteye.com/blog/2064662
参考其中的章节【6.mongodb客户端配置 】
2.部署，将工程根目录下的部署文件sessionmonitor.xml拷贝到tomcat目录下：
F:\environment\apache-tomcat-7.0.30\conf\Catalina\localhost
根据实际情况修改sessionmonitor.xml中的应用路径docBase：
<?xml version="1.0" encoding="UTF-8"?>
<Context   path="/sessionmonitor"   docBase="F:\workspace\bbossgroups-3.5\bestpractice\sessionmonitor\WebRoot"   debug="0"/>
3.启动tomcat，
启动tomcat后，在浏览器中访问以下地址，查看所有应用session数据
http://localhost:8080/sessionmonitor/session/sessionManager/sessionManager.jsp
在浏览器中输入以下地址，验证session共享功能，同时在监控页面查看session的变化情况，
http://localhost:8080/sessionmonitor/sessiontest.jsp