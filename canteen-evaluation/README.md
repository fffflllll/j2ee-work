# Canteen Evaluation (食堂菜品评价系统)

一个基于 **Spring Boot 3 + MyBatis + JWT** 的简易食堂菜品管理与评价系统，包含前后端（前端为静态页面 + 原生 JS / Vue2 + Axios）。实现了用户注册 / 登录、基于角色的权限控制（管理员 / 普通用户）、菜品管理、菜品评价、评价删除等核心功能。

---
## 功能概览
| 模块 | 普通用户 | 管理员 |
|------|----------|--------|
| 注册登录 | 注册 / 登录 | 登录（管理员账号） |
| 菜品查看 | 可查看列表与详情 | 可查看 |
| 菜品新增 | 否 | 是（添加菜品、上传图片） |
| 菜品删除 | 否 | 是 |
| 菜品修改 | （当前未实现，可扩展） | （可扩展） |
| 评价提交 | 是 | 是（以普通身份） |
| 评价删除 | 否 | 是（删除任意评价） |
| 角色区分 | 仅 user | admin |

---
## 技术栈
- 后端：Spring Boot 3.5.6
- Web：Spring MVC
- 数据访问：MyBatis 3 + MyBatis Spring Boot Starter
- 数据库：MySQL (需自行准备 schema)
- 安全鉴权：JWT (jjwt-api / jjwt-impl / jjwt-jackson 0.11.5)
- 依赖管理：Maven
- 构建 / 运行：JDK 17
- 前端：静态 HTML + Vue2 + Axios + Bootstrap (CDN)

---
## 目录结构（核心部分）
```
canteen-evaluation/
  pom.xml
  src/main/java/my_project/
    CanteenEvaluationApplication.java
    config/WebConfig.java                # 静态资源 & 上传目录映射
    interceptor/JwtInterceptor.java      # 解析 JWT，注入 Claims
    controller/
      UserController.java                # 注册 / 登录 / 退出
      DishController.java                # 菜品 CRUD（新增/改/删需 admin）
      EvaluationController.java          # 菜品评价（提交需登录，删除需 admin）
      UploadController.java              # 图片上传
    entity/{User,Dish,Evaluation}.java
    mapper/* (接口) + resources/my_project/mapper/*.xml
    service/* + impl/*
    util/JwtUtil.java
  src/main/resources/static/
    index.html          # 菜品列表（登录后可差异化展示）
    user-login.html     # 用户登录/注册（二合一）
    admin-login.html    # 管理员登录
    add-dish.html       # 管理员添加菜品
    dish.html           # 菜品详情 + 评价
```

---
## 运行环境要求
| 依赖 | 版本 |
|------|------|
| JDK  | 17+  |
| MySQL| 8.x  |
| Maven| 3.8+ |

确保本地 MySQL 可用，并在 `application.properties` 中配置正确的数据库连接。

### 示例 `application.properties`
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/canteen?serverTimezone=Asia/Shanghai&useSSL=false&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=你的密码
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
mybatis.mapper-locations=classpath:my_project/mapper/*.xml
server.port=8080
```

---
## 数据库表结构参考
### 1. 用户表 `user`
```sql
CREATE TABLE user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(200) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'user', -- 'user' 或 'admin'
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 2. 菜品表 `dish`
```sql
CREATE TABLE dish (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  ingredients VARCHAR(255),
  price DECIMAL(10,2) NOT NULL DEFAULT 0,
  description VARCHAR(500),
  photo_url VARCHAR(500),
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3. 评价表 `evaluation`
```sql
CREATE TABLE evaluation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  dish_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  score INT NOT NULL,            -- 1 ~ 5
  comment VARCHAR(500),
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_eval_dish FOREIGN KEY (dish_id) REFERENCES dish(id) ON DELETE CASCADE,
  CONSTRAINT fk_eval_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

> 如需 *首个注册用户自动成为管理员*，可自行在 `register` 逻辑加入 countAdmins 判定（当前版本已去除：所有注册用户默认为 `user`）。

---
## 权限与 JWT 说明
- 登录成功后后端返回：`{ token, role, success, message }`
- 前端将 `token` 存入 `localStorage`，请求时通过 Axios 拦截器自动添加：
  `Authorization: Bearer <token>`
- `JwtInterceptor` 验证 token，成功后将 `Claims` 放入 `request`：
  - `userId` / `username` / `role`
- 管理员判断：`claims.get("role") == "admin"`
- 需要登录的接口：评价提交、菜品新增 / 修改 / 删除、评价删除

---
## 主要 REST 接口
### 认证模块 `/api/auth`
| 方法 | 路径 | 描述 | 认证 | 备注 |
|------|------|------|------|------|
| POST | /api/auth/register | 注册用户 | 否 | 返回 token & role（role= user） |
| POST | /api/auth/login | 登录 | 否 | 返回 token & role |

### 菜品模块 `/api/dishes`
| 方法 | 路径 | 描述 | 认证 | 备注 |
|------|------|------|------|------|
| GET  | /api/dishes | 查询全部菜品 | 否 | 列表页使用 |
| GET  | /api/dishes/{id} | 查询单个菜品 | 否 | 详情页使用 |
| POST | /api/dishes | 新增菜品 | 需要 admin | JSON Body |
| PUT  | /api/dishes | 修改菜品 | 需要 admin | （当前前端未实现） |
| DELETE | /api/dishes/{id} | 删除菜品 | 需要 admin | |

### 评价模块 `/api/evaluations`
| 方法 | 路径 | 描述 | 认证 | 备注 |
|------|------|------|------|------|
| GET  | /api/evaluations/dish/{dishId} | 某菜品评价列表 | 否 | 匿名可看 |
| POST | /api/evaluations | 添加评价 | 登录 | Body: {dishId, score, comment} |
| DELETE | /api/evaluations/{id} | 删除评价 | 需要 admin | 前端 dish.html 管理员可见按钮 |

### 上传模块 `/api/upload`
| 方法 | 路径 | 描述 | 认证 | 备注 |
|------|------|------|------|------|
| POST | /api/upload | 上传图片 | 登录（建议限制 admin） | 返回图片 URL |

---
## cURL 示例
```bash
# 1. 注册用户
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"user1","password":"123456"}'

# 2. 登录获取 token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'username=user1&password=123456' | jq -r '.token')

# 3. 获取菜品列表
curl http://localhost:8080/api/dishes

# 4. 管理员新增菜品（需 admin token）
curl -X POST http://localhost:8080/api/dishes \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name":"红烧肉","price":25.5,"ingredients":"五花肉","description":"经典家常菜"}'

# 5. 提交评价（普通登录用户）
curl -X POST http://localhost:8080/api/evaluations \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"dishId":1,"score":5,"comment":"很好吃"}'
```
> 说明：如果需要 jq，请提前安装；也可以手动复制 token。

---
## 构建与运行
### 克隆与编译
```bash
mvn clean package
```

### 启动
```bash
java -jar target/canteen-evaluation-0.0.1-SNAPSHOT.jar
```
访问：
- 首页：http://localhost:8080/index.html
- 用户登录/注册：http://localhost:8080/user-login.html
- 管理员登录：http://localhost:8080/admin-login.html

---
## 前端说明
| 页面 | 说明 |
|------|------|
| user-login.html | 登录 + 注册（注册即普通用户，自动登录） |
| admin-login.html | 管理员专用登录入口 |
| index.html | 菜品列表（管理员可见“添加”“删除”） |
| add-dish.html | 管理员添加菜品、上传图片 |
| dish.html | 菜品详情与评价，管理员可删除评价 |

- 所有请求通过 Axios 拦截器自动塞入 `Authorization` 头。
- JWT 解析采用前端简单 Base64 解码（仅 UI 显示，不依赖其做安全决策）。

---
## 常见问题 (FAQ)
| 问题 | 可能原因 / 解决方案 |
|------|----------------------|
| 登录后访问需要权限接口仍 401 | 确认请求头中是否包含 `Authorization: Bearer <token>` |
| 新增菜品返回 false | 当前用户不是管理员；确认 token 中 `role=admin` |
| 出现 `DefaultJwtBuilder` 类缺失 | 确保 pom 中包含 `jjwt-impl` 与 `jjwt-jackson` 且未加 scope=runtime |
| 数据库字段不匹配报错 | 确认 evaluation 表是否使用 `user_id` 而不是 `user_name` |
| 密码明文存储 | 已使用 BCrypt；如果仍是明文，可能注册在依赖添加前执行，重新注册确认 |

---
## 可扩展方向
- 分页与条件查询（菜品 / 评价）
- 评价编辑 / 防重复评价（同一用户对同一菜品限制一条）
- 统计（平均评分、热门菜品）
- 管理后台（单独的管理页面聚合：用户管理 / 评价管理）
- 图片存储改用对象存储（OSS / MinIO）
- 使用 Spring Security 统一管理认证与授权
- Refresh Token 机制 / Token 黑名单

---
## 开发提示
1. 修改 Mapper XML 后若无效，可先 `mvn clean` 再启动。
2. 避���在前端信任 JWT 里的 role 进行关键安全操作；后端已再次校验。
3. 上传功能建议进一步限制文件大小、类型与路径。
4. 跨域（如后期分离前后端）可在 `WebConfig` 里添加 CORS 配置。

---
## 贡献 & 维护
当前为示例/练习项目，可自由扩展。建议新增功能时：
1. 先写接口与数据库字段变更设计。
2. 添加或更新 README 对应章节。
3. 补充最小化测试（如 Service 层单元测试）。

---
## License
本项目作为学习示例使用，未设置开源协议；如需对外发布请补充 LICENSE 文件。

---
如果你需要：添加管理员提升接口、平均评分统计、分页、或改造为前后端分离，请继续提出需求。祝开发顺利！

