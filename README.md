# mhttpd
### 关于
小型http服务器，参照Tomcat实现

### 前置条件
运行mhttpd需要 `JDK 11` 或者以上版本

### 编译和运行
    mvn install
在target/dist可以找到编译好的项目

    cd target/dist/bin
    java -jar ./mhttpd-*.jar
### 工程结构
    dist
    └── mhttpd
        ├── bin (二进制文件)
        │   ├── apiguardian-api-1.0.0.jar
        │   ├── asm-3.3.1.jar
        │   ├── cglib-2.2.2.jar
        │   ├── commons-beanutils-1.8.3.jar
        │   ├── commons-digester3-3.2.jar
        │   ├── commons-io-2.1.jar
        │   ├── commons-logging-1.0.4.jar
        │   ├── hamcrest-core-1.3.jar
        │   ├── jmimemagic-0.1.5.jar
        │   ├── junit-4.12.jar
        │   ├── junit-jupiter-api-5.4.0.jar
        │   ├── junit-jupiter-engine-5.4.0.jar
        │   ├── junit-platform-commons-1.4.0.jar
        │   ├── junit-platform-engine-1.4.0.jar
        │   ├── junit-platform-launcher-1.4.2.jar
        │   ├── junit-vintage-engine-5.4.0.jar
        │   ├── log4j-1.2.8.jar
        │   ├── log4j-api-2.11.2.jar
        │   ├── log4j-core-2.11.2.jar
        │   ├── lombok-1.18.8.jar
        │   ├── mhttpd-1.0-SNAPSHOT.jar  (主要程序文件)
        │   └── opentest4j-1.1.1.jar
        ├── conf  (配置文件默认目录)
        │   └── server.xml (默认配置文件)
        └── www  (默认的web内容目录)
            ├── cgi-bin  (默认cgi脚本目录)
            │   ├── envtest.py
            │   ├── hello
            │   ├── index.html
            │   └── showenv
            └── html (默认的静态页面目录)
                ├── 201810271911
                │   ├── index.html
                │   └── net.mooctest
                │       ├── CalendarUnit.java.html
                │       ├── Date.java.html
                │       ├── Day.java.html
                │       ├── index.html
                │       ├── Month.java.html
                │       ├── Nextday.java.html
                │       └── Year.java.html
                ├── 404.html 
                ├── favicon.ico
                ├── form.html
                ├── index.html
                ├── rfc2616.txt
                └── web
                    ├── 1.jpg
                    ├── 2.png
                    ├── index.htm
                    └── main.css

    
