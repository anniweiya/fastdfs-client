FastDFS连接池
-------------
使用common-pool2  
直持spring  
通过nginx进行http访问  
支持防盗链  


```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

  <bean id="fastDFSFactory" class="com.anniweiya.fastdfs.FastDFSTemplateFactory" init-method="init">
    <!--连接超时的时限，单位为秒-->
    <property name="g_connect_timeout" value="60"/>
    <!--网络超时的时限，单位为秒-->
    <property name="g_network_timeout" value="80"/>
    <!--防盗链配置-->
    <property name="g_anti_steal_token" value="true"/>
    <property name="g_secret_key" value="FastDFS1234567890"/>
    <property name="poolConfig">
      <bean class="com.anniweiya.fastdfs.pool.PoolConfig">
        <!--池的大小-->
        <property name="maxTotal" value="100"/>
        <!--连接池中最大空闲的连接数-->
        <property name="maxIdle" value="10"/>
      </bean>
    </property>
    <!--tracker的配置 ","逗号分隔-->
    <property name="tracker_servers" value="127.0.0.1:22122"/>
    <!--HTTP访问服务的端口号-->
    <property name="g_tracker_http_port" value="8080"/>
    <!--nginx的对外访问地址，如果没有端口号，将取g_tracker_http_port配置的端口号 ","逗号分隔-->
    <property name="nginx_address" value="127.0.0.1:8080"/>
  </bean>

  <!--注入模板类-->
  <bean id="fastDFSTemplate" class="com.anniweiya.fastdfs.FastDFSTemplate">
    <constructor-arg ref="fastDFSFactory"/>
  </bean>

</beans>
```