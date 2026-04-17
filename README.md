# mule-replacement-engine

基于 `docs/mule-replacement-plan.md` 的 Java 21 + Maven 引擎工程，当前实现到 **Phase-1 完整骨架 + Phase-2/3 部分能力**。

## 已实现能力

- XML 兼容输入：`MuleXmlParser` 支持 `flow/sub-flow`、嵌套节点，含映射：
  - `http:listener -> http-listener`
  - `http:request -> http-request`
  - `db:select -> jdbc-query`
  - `raise-error -> fail`
- IR 模型：`AppModel` / `FlowModel` / `NodeModel`（含 children）。
- Runtime：`EngineRuntime` + `ExecutionBridge`，支持 flow 执行和 inline 节点执行。
- 表达式：`ExpressionEvaluator` 支持：
  - `#[payload]`
  - `#[vars.xxx]`
  - `#[attributes.xxx]`
  - `#[a == b]` / `#[vars.x == 'foo']`
  - `${vars.xxx}` 插值
- 处理器 SPI：`Processor` + `ProcessorRegistry`。
- 内置处理器：
  - `logger`
  - `set-variable`
  - `flow-ref`
  - `choice`（when/otherwise）
  - `for-each`
  - `try` + `on-error-continue/on-error-propagate`
  - `fail`（测试/故障注入）
  - `http-request`
  - `jdbc-query`
- 连接器：
  - HTTP 客户端：`JdkHttpConnector`（`java.net.http.HttpClient`）
  - HTTP 监听：`JdkHttpListenerServer`（`com.sun.net.httpserver.HttpServer`）
  - JDBC：`SimpleJdbcConnector`（`DataSource`）
- 部署：`HttpListenerDeployer` 可扫描包含 `http-listener` 的 flow 并启动监听。

## 运行计划（迭代）

1. **P1（已完成）**：XML -> IR -> Runtime 主链路 + 常用算子子集。
2. **P2（已完成）**：flow-ref/choice/for-each/try-error 等流程语义。
3. **P3（进行中）**：HTTP Listener/Request、JDBC 执行器、错误语义映射。
4. **P4（规划）**：调度、可观测、双跑回归、更多连接器（含自研算子）。

## 结论

- **尚未达到“完全替代 Mule”**。
- 目前已具备可扩展基础与部分可执行能力，适合继续按分阶段路线补齐。

## 构建与测试

```bash
mvn test
```

## 启动示例

```bash
mvn -q -DskipTests package
java -jar target/mule-replacement-engine-0.1.0-SNAPSHOT.jar /path/to/app.xml demoFlow
```
