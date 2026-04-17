# Mule 4.6.0 替代引擎方案（聚焦 Mule 自带算子，app XML 兼容输入）

## 1. 范围重定义（按最新要求）

本版方案按以下约束收敛：

- **暂不优先考虑 40+ 自研算子**（因其语法与 Mule 自带算子一致，后续可按同一机制扩展）
- **优先覆盖 Mule 自带核心能力**：
  - HTTP Listener / HTTP Request
  - Socket
  - JDBC
  - Scheduler
  - For Each / Choice / Flow-Ref / Logger / Variables / Script
- **以现有 app XML 作为输入参考**，实现可替换运行时
- DataWeave 使用少，可先做“轻表达式 + 脚本兜底”，不追求全量兼容

---

## 2. 可行性结论

**结论：可行，且在“先不处理自研算子”的约束下，落地风险明显降低。**

原因：

1. 现阶段只需兼容 Mule 核心流程语义和少量官方连接器。
2. app XML 已有稳定资产，可直接作为“兼容输入层”。
3. DataWeave 依赖低，避免了最难的语义兼容点。

建议目标：

- **第一目标（3~6 个月）**：让大部分核心 app 在新引擎“零改/微改”运行
- **第二目标（持续）**：逐步接入自研算子与高级语义

---

## 3. 总体技术路线：XML 兼容输入 + IR + 执行引擎

### 3.1 核心思路

采用“**解析 Mule app XML → 转换为统一 IR → 由新 Runtime 执行**”的结构：

1. XML Parser（兼容子集）
2. 语义校验器（Validator）
3. 执行计划生成器（Planner）
4. Runtime（事件模型 + 节点执行 + 错误处理）

该路线的优势是：

- 不要求业务团队重写 app
- 兼容能力边界清晰、可渐进扩展
- 后续可接入自研算子，只需补连接器适配层

### 3.2 引擎五层架构

1. **输入兼容层**：读取 Mule app XML（flows、sub-flows、configs）
2. **模型层**：统一 IR（节点、边、作用域、异常策略）
3. **执行层**：Processor Pipeline + Scheduler + 并发控制
4. **连接器层**：HTTP/JDBC/Socket 等官方能力适配
5. **治理层**：日志、指标、追踪、部署、回滚

---

## 4. 开源能力选型建议（Mule 相关能力替换）

目标不是“重造所有轮子”，而是组合成熟开源组件。

### 4.1 XML 与 DSL 处理

- XML 解析：Jakarta XML / JAXB / Woodstox
- XSD 校验：保留必要 schema 校验，避免非法配置进入运行时
- 自定义转换：Mule XML 子集 -> Engine IR

### 4.2 HTTP / 网络

- 服务端（替换 Listener）：Netty 或 Undertow
- 客户端（替换 Request）：Apache HttpClient 5 或 OkHttp
- Socket：Java NIO / Netty

### 4.3 JDBC / 事务

- 连接池：HikariCP
- JDBC 执行：Spring JDBC 或 jOOQ
- 事务：先支持本地事务；分布式事务后置

### 4.4 调度与并发

- Scheduler：Quartz（或轻量内置调度器）
- 线程模型：业务线程池 + IO 线程池隔离
- Backpressure：队列限流 + 拒绝策略

### 4.5 脚本与表达式

- 轻表达式：变量访问、常见函数、JSON Path
- 脚本兜底：Groovy（单一脚本引擎，避免多语言复杂度）

### 4.6 可观测性

- 指标：Micrometer + Prometheus
- 链路追踪：OpenTelemetry
- 日志：SLF4J + Logback + JSON layout

---

## 5. 兼容边界（必须先讲清）

### 5.1 首批必须兼容（Must）

- Flow / Sub-flow / Flow-ref
- HTTP Listener / Request
- Scheduler
- For Each / Choice
- Set Variable / Remove Variable
- Logger
- JDBC 基本查询与更新
- 标准错误传播与 continue/propagate

### 5.2 可延后兼容（Should）

- 更复杂的异常路由组合
- 高级脚本场景
- 批处理高级特性

### 5.3 暂不兼容（Won’t in Phase-1）

- 全量 DataWeave 语义
- 分布式事务
- 全部生态连接器

---

## 6. 运行时语义设计（保证“可替换”的关键）

### 6.1 事件模型

统一 `EventContext`：

- `payload`
- `attributes`
- `vars`
- `error`
- `meta`（traceId/app/flow/retryCount）

### 6.2 变量与作用域

- 流程级变量：默认可读写
- 子流调用：明确“引用传递”或“复制传递”策略（建议与 Mule 行为对齐）
- 并发节点：变量写冲突需检测并告警

### 6.3 错误模型

定义 `EngineError`：

- `type`（HTTP:TIMEOUT、DB:DEADLOCK 等）
- `retriable`
- `cause`
- `ext`

并映射：on-error-continue / on-error-propagate 语义。

---

## 7. 实施路径（不含自研算子优先接入）

### Phase A（2~3 周）：资产扫描 + 兼容画像

输出：

- 所有 app XML 的算子分布统计
- Must/Should/Won’t 清单
- Top N 高频模板（优先支持）

### Phase B（6~8 周）：MVP 引擎

覆盖：

- XML 解析与 IR
- HTTP Listener/Request、JDBC、Scheduler、变量、日志、循环
- 错误传播与基本重试

验收：

- 10~20 个核心 app 可跑
- 回归通过率 >95%

### Phase C（8~12 周）：规模化替换

覆盖：

- 完善异常语义和并发控制
- 观测能力、灰度发布、回滚
- 双跑比对平台（Mule vs 新引擎）

验收：

- 核心业务 app 覆盖率 >90%
- 核心链路性能不低于现网

### Phase D（持续）：再接入自研算子

- 因语法一致，按统一节点/连接器 SPI 扩展即可
- 按业务优先级逐批接入，不阻塞主替换计划

---

## 8. “开源 + 自研”实现分工建议

### 8.1 建议复用开源

- 网络通信、连接池、调度、可观测、日志
- 减少底层 bug 与维护成本

### 8.2 必须自研

- Mule XML 子集解析与语义映射
- IR 与执行计划
- 运行时上下文与错误语义对齐
- 兼容回归工具（双跑与结果比对）

一句话：**底座复用开源，兼容语义必须自研。**

---

## 9. 风险与缓解

1. **语义细节不一致** -> 建立黄金用例 + 双跑差异报告
2. **历史 app 配置非标准** -> 增加预检器与自动修复建议
3. **性能抖动** -> 压测基线、连接池与线程池参数模板化
4. **迁移节奏失控** -> 严格按 Must 清单推进，避免范围膨胀

---

## 10. 你现在可以马上启动的 5 件事

1. 做 app XML 扫描器，产出前 20 高频算子组合。
2. 锁定 Phase-1 兼容边界（本方案第 5 节 Must）。
3. 开发 XML->IR 编译链路原型（先不做自研算子）。
4. 建立 Mule vs 新引擎双跑回归基线。
5. 选 3~5 个核心 app 做 POC，验证“零改/微改”比例。

---

## 11. 最终建议

按你的最新要求，最优路径是：

- **先实现 Mule 自带能力可替换运行**（以 app XML 为输入）
- **后续再平滑纳入自研算子**（语法一致，技术路径复用）

这条路径能显著降低第一阶段复杂度，同时最大化资产复用与上线成功率。
